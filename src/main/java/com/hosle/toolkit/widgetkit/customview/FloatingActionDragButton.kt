package com.hosle.toolkit.widgetkit.customview

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import com.hosle.toolkit.widgetkit.getScreenHeight
import com.hosle.toolkit.widgetkit.getScreenWidth

/**
 * Created by tanjiahao on 2018/9/23
 * Original Project ho-widget-kit
 * 可拖动的悬浮按钮
 */
class FloatingActionDragButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Button(context, attrs, defStyleAttr) {
    private var mLastX: Int = 0
    private var mLastY: Int = 0

    private var mIsDragMove: Boolean = false
    
    private var screenWidth: Int = 0
    private var screenHeight: Int = 0
    private var screenWidthHalf: Int = 0
    private var statusHeight: Int = 0
    
    private var mTouchSlop: Int = 0
    
    init {
        ViewConfiguration.get(context).hasPermanentMenuKey()
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop

        screenWidth = getScreenWidth(getContext())
        screenWidthHalf = screenWidth / 2
        screenHeight = getScreenHeight(getContext())
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val downX = event.rawX.toInt()
        val downY = event.rawY.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isPressed = true
                mIsDragMove = false
                mLastX = downX
                mLastY = downY
            }
            MotionEvent.ACTION_MOVE -> {
                //计算手指移动了多少
                val dx = downX - mLastX
                val dy = downY - mLastY
                if (Math.abs(dx) < mTouchSlop / 4 && Math.abs(dy) < mTouchSlop / 4 && !mIsDragMove) {
                    mIsDragMove = false
                } else {
                    mIsDragMove = true
                    var x = x + dx
                    var y = y + dy
                    //检测是否到达边缘 左上右下
                    x = if (x < 0) 0F else if (x > screenWidth - width) (screenWidth - width).toFloat() else x
                    if (y < 0) {
                        y = 0f
                    }
                    if (y > screenHeight - statusHeight - height) {
                        y = (screenHeight - statusHeight - height).toFloat()
                    }
                    setX(x)
                    setY(y)
                    mLastX = downX
                    mLastY = downY
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                //恢复按压效果
                isPressed = false
                if (downX >= screenWidthHalf) {
                    animate().setInterpolator(DecelerateInterpolator())
                            .setDuration(500)
                            .xBy(screenWidth - width - x)
                            .start()
                } else {
                    val oa = ObjectAnimator.ofFloat(this, "x", x, 0F)
                    oa.interpolator = DecelerateInterpolator()
                    oa.duration = 500
                    oa.start()
                }
                if (!mIsDragMove) {
                    performClick()
                }
            }
        }
        //如果是拖拽则消耗事件，否则正常传递即可。
        return true
    }
}


package com.hosle.toolkit.widgetkit.customview

import android.content.Context
import android.graphics.Typeface
import android.support.annotation.ColorInt
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.hosle.toolkit.widgetkit.R
import com.hosle.toolkit.widgetkit.dp2px

/**
 * Created by tanjiahao on 2018/9/23
 * Original Project ho-widget-kit
 */
open class SlideButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val floatView: View
    private val floatViewLayoutParams:LayoutParams

    private val textView:TextView

    private val radioAutoScroll = 0.66
    private val radius2 = 45f

    init {
        layoutParams = ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        isClickable = false

        val txtLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.CENTER_VERTICAL
            marginStart = dp2px(context, radius2) - dp2px(context, 8f)
        }
        textView = TextView(context)
        textView.gravity = Gravity.CENTER
        this.addView(textView,txtLayoutParams)


        floatViewLayoutParams = LayoutParams(dp2px(context, radius2), dp2px(context, radius2)).apply {
            gravity = Gravity.CENTER_VERTICAL
            marginStart = dp2px(context, 5f)
            marginEnd = dp2px(context, 5f)
        }

        floatView = ImageView(context).apply {
            layoutParams = floatViewLayoutParams
            setImageResource(R.drawable.icon_swap_btn)
        }
        this.addView(floatView)

        this.setBackgroundResource(R.drawable.bg_slide_btn_blue)

    }

    fun setText(text:CharSequence){
        textView.text = text
    }

    fun setTextSize(size:Float){
        textView.textSize = size
    }

    fun setTextSize(unit :Int, size:Float){
        textView.setTextSize(unit,size)
    }

    fun setTextColor(@ColorInt resId:Int){
        textView.setTextColor(resId)
    }

    fun setTextStyle(tf:Typeface){
        textView.typeface = tf
    }

    fun setGravity(gravity:Int){
        textView.gravity = gravity
    }


    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {

        when(event.action){
            MotionEvent.ACTION_DOWN ->{
//                Log.i("SlideButton","dispatchTouchEvent:Action_Down")
                parent.requestDisallowInterceptTouchEvent(true)

                if (event.x in floatView.left..floatView.right && event.y in floatView.top..floatView.bottom) {
                    return super.dispatchTouchEvent(event)
                } else {
                    return false
                }
            }
            MotionEvent.ACTION_MOVE ->{
//                Log.i("SlideButton","dispatchTouchEvent:Action_Move")

            }

            MotionEvent.ACTION_CANCEL->{
//                Log.i("SlideButton","dispatchTouchEvent:Action_Cancel")

            }

        }

        return super.dispatchTouchEvent(event)
    }

    private var lastX:Float = 0f
    private var lastY:Float = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action){
            MotionEvent.ACTION_DOWN ->{
//                Log.i("SlideButton","onTouchEvent:Action_Cancel")

            }

            MotionEvent.ACTION_MOVE ->{
//                Log.i("SlideButton","onTouchEvent:Action_Move")

                val deltaX = event.x - lastX
                    val deltaY = event.y - lastY

                    if (Math.abs(deltaX) > Math.abs(deltaY)) {
                        //move
                        val distanceX = Math.max(floatViewLayoutParams.marginStart - floatView.left,
                                Math.min(deltaX.toInt(), measuredWidth - floatView.right - floatViewLayoutParams.marginEnd))

                        val newFloatViewLeft = floatView.left + distanceX
                        floatView.layout(newFloatViewLeft, floatView.top, floatView.right + distanceX, floatView.bottom)
                        notifyTextAlpha(newFloatViewLeft)
                    }
            }

            MotionEvent.ACTION_UP ->{
                    if (event.x < measuredWidth * radioAutoScroll) {
                        autoScrollToStart(floatViewLayoutParams.marginStart - floatView.left.toFloat())
                    } else {
                        autoScrollToEnd((measuredWidth - floatView.right - floatViewLayoutParams.marginEnd).toFloat())
                    }
            }

            MotionEvent.ACTION_CANCEL ->{
//                Log.i("SlideButton","onTouchEvent:Action_Cancel")
                    autoScrollToStart(floatViewLayoutParams.marginStart - floatView.left.toFloat())
            }
        }
        lastX = event.x
        lastY = event.y

        return true
    }

    private fun notifyTextAlpha(floatViewLeft:Int){
        textView.alpha = 1 - (floatViewLeft / measuredWidth.toFloat())
    }

    protected fun slideToStartPos(){
        autoScrollToStart(floatViewLayoutParams.marginStart - floatView.left.toFloat())
    }

    private fun autoScrollToEnd(deltaX: Float){
        autoScroll(deltaX,true)
    }

    private fun autoScrollToStart(deltaX: Float){
        autoScroll(deltaX,false)
    }

    private fun autoScroll(deltaX:Float,activated:Boolean){
        val translateAnimation = TranslateAnimation(0f,deltaX,0f,0f)
        translateAnimation.duration = 100
        translateAnimation.setAnimationListener(object :Animation.AnimationListener{
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                floatView.clearAnimation()

                val floatViewLeft = floatView.left + deltaX.toInt()
                floatView.layout(floatViewLeft, floatView.top, floatView.right + deltaX.toInt(), floatView.bottom)
                notifyTextAlpha(floatViewLeft)

                if(activated){
                    callOnClick()
                }
            }

            override fun onAnimationStart(animation: Animation?) {
            }

        })
        floatView.startAnimation(translateAnimation)

    }
}
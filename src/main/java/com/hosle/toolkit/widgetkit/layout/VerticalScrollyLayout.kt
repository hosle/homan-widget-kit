package com.hosle.toolkit.widgetkit.layout

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.Scroller
import com.hosle.toolkit.widgetkit.dp2px
import com.hosle.toolkit.widgetkit.getScreenHeight
import kotlin.math.roundToLong

/**
 * Created by tanjiahao on 2018/9/28
 * Original Project verticalScrollyLayout
 */

const val NORMAL_TOP = 395f
const val MIN_TOP_MARGIN_DP = 0f
const val TITLE_HEIGHT = 36 + 15 + 20f

class VerticalScrollyLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private val constNormalOffsetFromTop = dp2px(context,NORMAL_TOP)
    private val constMaxOffsetFromTop =  getScreenHeight(context)  - dp2px(context, 117f)
    private var constMinOffsetFromTop =  dp2px(context, MIN_TOP_MARGIN_DP)
    private val constBoundaryMinNormal :Int
        get()= (constMinOffsetFromTop + constNormalOffsetFromTop) / 2
    private val constBoundaryMaxNormal = (constMaxOffsetFromTop + constNormalOffsetFromTop) / 2
    private val constStatusBarHeight = dp2px(context, 30f)
    private val constTitleHeight = dp2px(context, TITLE_HEIGHT)

    private var lastX = 0
    private var lastY = 0

    private val scroller = Scroller(context)

    private val contentLayout : FrameLayout = FrameLayout(context)

    private var isFirstLayout = true

    init {
        addView(contentLayout, LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT))
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {

        val evX = ev!!.x.toInt()
        val evY = ev.y.toInt()

        if(evX in contentLayout.left .. contentLayout.right &&
                evY in contentLayout.top .. contentLayout.bottom){
            return super.dispatchTouchEvent(ev)
        }

        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        val evX = ev!!.x.toInt()
        val evY = ev.y.toInt()

        if (evX in contentLayout.x..contentLayout.x + contentLayout.right &&
                evY in contentLayout.y..contentLayout.y + contentLayout.bottom) {
            var isIntecept = false

            when (ev.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = evX
                    lastY = evY
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = evX - lastX
                    val dy = evY - lastY

                    isIntecept = if (Math.abs(dy) - Math.abs(dx) > 0) {
                        if (isNotFullScreen()) {
                            true
                        } else {
                            isRecyclerViewReachTopWhenFullScreen(dy)
                        }
                    } else {
                        false
                    }
                }
                MotionEvent.ACTION_UP -> {
                }
            }

            return isIntecept

        }

        return false

    }

    private fun isNotFullScreen():Boolean{
        return contentLayout.top > constMinOffsetFromTop
    }

    private fun isRecyclerViewReachTopWhenFullScreen(dy:Int):Boolean{
        var recyclerView : RecyclerView? = null

        for(i in 0 until contentLayout.childCount){
            val child = contentLayout.getChildAt(i)
            if(child is RecyclerView){
                recyclerView = child
            }
        }

        return dy >= 0 && (recyclerView?.canScrollVertically(-1) == false )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureChildren(widthMeasureSpec,heightMeasureSpec)

        //find total height
        var childMaxHeight = Int.MAX_VALUE
        for(i in 0 until contentLayout.childCount){
            val child = contentLayout.getChildAt(i)
            if(child is RecyclerView){
                if(child.canScrollVertically(-1)){
                    childMaxHeight = -1
                }else{
                    childMaxHeight = Math.min(childMaxHeight,child.measuredHeight)
                }
            }
        }

        constMinOffsetFromTop = Math.max(getScreenHeight(context) - childMaxHeight,constMinOffsetFromTop)

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (changed) {
            for (index in 0 until childCount) {
                val child = getChildAt(index)
                if (child == contentLayout && isFirstLayout) {
                    child.layout(l + paddingStart, t + constNormalOffsetFromTop, r - paddingEnd, b + constNormalOffsetFromTop)
                    isFirstLayout = false
                } else {
                    child.layout(l + paddingStart, t, r - paddingEnd, b)
                }
            }
        }
    }

    fun clearContent(){
        contentLayout.removeAllViews()
    }

    fun addContentView(child: View?){
        contentLayout.addView(child)
    }

    fun addContentView(child: View?,params: LayoutParams?) {
        contentLayout.addView(child,params)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val evY = event!!.y.toInt()

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                lastY = evY
                if (evY <= -constMaxOffsetFromTop + constTitleHeight) {
//                    performClick()
                }
            }

            MotionEvent.ACTION_MOVE -> {

                val dy = evY - lastY

                val distance = if (dy > 0) {
                    Math.min(constMaxOffsetFromTop - contentLayout.top, dy)
                } else {
                    Math.max(constMinOffsetFromTop - contentLayout.top, dy)
                }

                contentLayout.apply {
                    layout(left, top + distance, right, bottom + distance)
                }


                if(scrollY < constMaxOffsetFromTop + constNormalOffsetFromTop / 3){
//                    performClick()
                }

                lastY = evY
            }
            MotionEvent.ACTION_UP -> {
                when(contentLayout.top){
                    in constMinOffsetFromTop until constBoundaryMinNormal ->{
                        autoScroll(contentLayout, -(contentLayout.top - constMinOffsetFromTop).toFloat())
                    }
                    in constBoundaryMinNormal until constNormalOffsetFromTop ->{
                        autoScroll(contentLayout, -(contentLayout.top - constNormalOffsetFromTop).toFloat())

                    }
                    in constNormalOffsetFromTop until constBoundaryMaxNormal ->{
                        autoScroll(contentLayout, -(contentLayout.top - constNormalOffsetFromTop).toFloat())

                    }
                    in constBoundaryMaxNormal until constMaxOffsetFromTop ->{
                        autoScroll(contentLayout, -(contentLayout.top - constMaxOffsetFromTop).toFloat())

                    }
                }
            }
        }

        return true
    }

    private var topBar:View? = null

    private fun getTopBar(): View? {
        if (topBar == null) {
            topBar = contentLayout.findViewWithTag("topBar")
        }
        return topBar
    }

    fun resetDefaultPosition(){

        isFirstLayout = true
        requestLayout()
    }

    private fun autoScroll(view: View, deltaX: Float, activated: Boolean = false) {
        val translateAnimation = TranslateAnimation(0f, 0f, 0f, deltaX)
        translateAnimation.duration = (Math.abs(deltaX) / (500/100f)).roundToLong()
        translateAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                view.clearAnimation()
                view.layout(view.left , view.top + deltaX.toInt(), view.right, view.bottom + deltaX.toInt())
                if (activated) {
                    callOnClick()
                }

                if (contentLayout.top <= constMinOffsetFromTop) {
                    getTopBar()?.visibility = View.GONE
                } else {
                    getTopBar()?.visibility = View.VISIBLE
                }
            }

            override fun onAnimationStart(animation: Animation?) {
            }

        })
        view.startAnimation(translateAnimation)

    }


}
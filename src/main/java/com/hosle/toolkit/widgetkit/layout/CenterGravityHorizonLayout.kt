package com.hosle.toolkit.widgetkit.layout

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.hosle.toolkit.widgetkit.dp2px

/**
 * Created by tanjiahao on 2018/9/29
 * Original Project app-android-groundservice
 */
class CenterGravityHorizonLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private var addLine = false

    private var childWaitingToAdd:View? = null
    private var indexWaitingToAdd : Int =  0
    private var paramsWaitingToAdd : LayoutParams? = null

    override fun addView(child: View?, index: Int, params: LayoutParams?) {

        var visibleChildCount = childCount
        for (i in 0 until childCount) {
            if (getChildAt(i).visibility == View.GONE)
                visibleChildCount--
        }

        if(visibleChildCount > 0 && !addLine && child?.visibility != View.GONE ){
            val line = View(context).apply {
                setBackgroundColor(Color.parseColor("#d8d8d8"))
                layoutParams = ViewGroup.MarginLayoutParams(dp2px(context, 0.5f), dp2px(context, 45f))
                tag = "iamline"
            }

            childWaitingToAdd = child
            indexWaitingToAdd = index
            paramsWaitingToAdd = params
            addLine = true
            addView(line)
        }else{
            super.addView(child, index, params)
        }
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)

        if (child?.tag == "iamline" && addLine) {
            super.addView(childWaitingToAdd, indexWaitingToAdd, paramsWaitingToAdd)
        }else{
            childWaitingToAdd = null
            paramsWaitingToAdd = null
        }

        addLine = false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var height = 0

        for(i in 0 until childCount){
            val child = getChildAt(i)
            val layoutParams: MarginLayoutParams = child.layoutParams as MarginLayoutParams

            measureChild(child,widthMeasureSpec,heightMeasureSpec)
            height = Math.max(child.measuredHeight + layoutParams.topMargin + layoutParams.bottomMargin, height)
        }

        setMeasuredDimension(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height + paddingTop + paddingBottom,MeasureSpec.EXACTLY))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if(changed) {
            var space = 0
            var totalChildWidth = 0
            var eachSpace = 0

            var visibleChildCount = childCount

            for (i in 0 until childCount) {
                val child = getChildAt(i)

                if (child.visibility == View.GONE) {
                    visibleChildCount--
                    continue
                }

                totalChildWidth += child.measuredWidth
            }

            space = Math.max(0, measuredWidth - totalChildWidth)
            eachSpace = space / (visibleChildCount + 1)

            var startLeft = eachSpace

            for (i in 0 until childCount) {
                val child = getChildAt(i)

                if(child.visibility == View.GONE)
                    continue

                val layoutParams: MarginLayoutParams = child.layoutParams as MarginLayoutParams

                child.layout(startLeft, paddingTop + layoutParams.topMargin, startLeft + child.measuredWidth, paddingTop + layoutParams.topMargin + child.measuredHeight)
                startLeft += (eachSpace + child.measuredWidth)
            }
        }
    }

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return MarginLayoutParams(p)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
    }

}
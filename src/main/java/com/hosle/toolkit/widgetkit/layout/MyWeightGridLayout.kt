package com.hosle.toolkit.widgetkit.layout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.hosle.toolkit.widgetkit.dp2px

/**
 * Created by tanjiahao on 2018/8/29
 * Original Project ho-widget-kit
 */
class MyWeightGridLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private val columnCount = 3//can not be 0
    private var lineCount = 0
    private var cellWidth : Int = 0
    private var cellHeight : Int = 0

    private val lineHeight = ArrayList<Int>()
    private var marginMidVertical :Int = dp2px(context, 15f)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val childWidthMeasureSpec = MeasureSpec.makeMeasureSpec((width - paddingLeft - paddingRight)/columnCount,MeasureSpec.getMode(widthMeasureSpec))

        var heightParent = paddingTop + paddingBottom
        var heightPerLine:Int = 0

        lineHeight.clear()
        var goneViewCount = 0

        for(i in 0 until childCount){
            val child = getChildAt(i)

            if (child.visibility != View.GONE) {
                child.measure(childWidthMeasureSpec, heightMeasureSpec)

                heightPerLine = Math.max(heightPerLine, child.measuredHeight)

                if ((i-goneViewCount) % columnCount == 0) {
                    lineHeight.add(heightPerLine)
                    heightParent += (heightPerLine + marginMidVertical)
                }
            }else{
                ++goneViewCount
            }
        }

        val parentHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightParent,MeasureSpec.UNSPECIFIED)

        setMeasuredDimension(widthMeasureSpec,parentHeightMeasureSpec)
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
            var heightTotal = paddingTop
            val widthChild = (r-l)/columnCount
            var heightPerLine = 0
            var widthStart = 0


            var goneViewCount = 0

            for (i in 0 until childCount) {
                val child = getChildAt(i)

                val visible_i = i - goneViewCount

                if(child.visibility!=View.GONE) {

                    heightPerLine = lineHeight[visible_i / columnCount]

                    child.layout(widthStart, heightTotal, widthStart + widthChild, heightPerLine + heightTotal)

                    widthStart += widthChild

                    if (visible_i >= columnCount - 1 && visible_i % columnCount == columnCount - 1) {
                        heightTotal += (heightPerLine + marginMidVertical)
                        widthStart = 0
                    }
                }else{
                    ++goneViewCount
                }
            }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context,attrs)
    }

}
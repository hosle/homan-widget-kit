package com.hosle.toolkit.widgetkit.customview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.hosle.toolkit.widgetkit.R

/**
 * Created by tanjiahao on 2018/8/14
 * Original Project ho-widget-kit
 */
class SpacedTextView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val typeArray:TypedArray = context.obtainStyledAttributes(attrs, R.styleable.SpacedTextView)

    var text:String? = typeArray.getString(R.styleable.SpacedTextView_text)
    var sTextSize:Int = typeArray.getDimensionPixelSize(R.styleable.SpacedTextView_textSize,30)
    var sTextColor:Int = typeArray.getColor(R.styleable.SpacedTextView_textColor,Color.parseColor("#000000"))

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(sTextSize,MeasureSpec.EXACTLY)
        setMeasuredDimension(widthMeasureSpec,newHeightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawText(canvas)
    }

    private fun drawText(canvas: Canvas){
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = sTextColor
            textSize = sTextSize.toFloat()
        }
        val textArr = text

        if(textArr == null || textArr.isEmpty()){
            return
        }

        val letterWidth = paint.measureText("8")
        val spaceEachLetter = (width - textArr.length * letterWidth)/(textArr.length-1)
        var startX = 0f
        val fontMetrics = paint.fontMetrics
        val startY = height / 2 + (Math.abs(fontMetrics.ascent) - fontMetrics.descent) / 2


        for (i in 0 until textArr.length) {
            canvas.drawText(textArr[i].toString(), startX, startY, paint)
            startX += (letterWidth + spaceEachLetter)
        }

    }

}
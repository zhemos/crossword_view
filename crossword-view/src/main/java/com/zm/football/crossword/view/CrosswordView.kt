package com.zm.football.crossword.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CrosswordView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val logger = Logger(isDebug = true)

    private val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }
    private val paint2 = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
    }

    private val size = 100f.px

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        logger.log("draw")
        val left = width
        canvas.drawRect(16f.px, 32f.px, width - 10f, 100f, paint2)
    }

    private val Float.px get() = this * context.resources.displayMetrics.density
}
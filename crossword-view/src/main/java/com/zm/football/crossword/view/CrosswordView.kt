package com.zm.football.crossword.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class CrosswordView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val logger = Logger(isDebug = true)

    private val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 1f.px
        style = Paint.Style.STROKE
    }
    private val paint2 = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
    }

    //8x6
    private val crossword = arrayOf(
        arrayOf(1, 1, 1, 1, 1, 1),
        arrayOf(1, 1, 1, 1, 1, 1),
        arrayOf(1, 1, 1, 1, 1, 1),
        arrayOf(1, 1, 1, 1, 1, 1),
        arrayOf(1, 1, 1, 1, 1, 1),
        arrayOf(1, 1, 1, 1, 1, 1),
        arrayOf(1, 1, 1, 1, 1, 1),
        arrayOf(1, 1, 1, 1, 1, 1),
    )


    private val horizontalMarginForPortrait = 64f.px
    private val verticalMarginForLandscape = 16f.px
    private val touchSlop = 10f.px

    private var crosswordRectF = RectF()
    private var size = 0f
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var crosswordX = 0f
    private var crosswordY = 0f
    private var horizontalMargin = 0f
    private var verticalMargin = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        logger.log("draw $width $height")
        if (canvas.isPortrait) {
            size = (width - horizontalMarginForPortrait * 2) / crossword.size
            horizontalMargin = horizontalMarginForPortrait
            verticalMargin = height / 2f - crossword[0].size * size / 2f
        } else {
            size = (height - verticalMarginForLandscape * 2) / crossword[0].size
            horizontalMargin = width / 2f - crossword.size * size / 2f
            verticalMargin = verticalMarginForLandscape
        }
        crossword.forEachIndexed { i, rows ->
            rows.forEachIndexed { j, column ->
                canvas.kotlinDrawRect(
                    left = i * size + horizontalMargin + crosswordX,
                    top = j * size + verticalMargin + crosswordY,
                    right = i * size + size + horizontalMargin + crosswordX,
                    bottom = j * size + size + verticalMargin + crosswordY,
                    paint = paint,
                )
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        logger.log("$event")
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                lastTouchY = event.y
                click()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - lastTouchX
                val dy = event.y - lastTouchY
                if (dx * dx + dy * dy > touchSlop * touchSlop) {
                    move(event, dx, dy)
                }
            }
            MotionEvent.ACTION_UP -> {
                performClick()
            }
        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun Canvas.redrawCrossword() {
        crossword.forEachIndexed { i, rows ->
            rows.forEachIndexed { j, column ->
            }
        }
    }

    private fun click() {
        logger.logError("click")
    }

    private fun move(event: MotionEvent, dx: Float, dy: Float) {
        logger.logError("move $size")
        val newX = crosswordX + dx
        val newY = crosswordY + dy
        crosswordX = newX.coerceIn(
            minimumValue = -horizontalMargin,
            maximumValue = width - crossword.size * size - horizontalMargin,
        )
        crosswordY = newY.coerceIn(
            minimumValue = -verticalMargin,
            maximumValue = height - crossword[0].size * size - verticalMargin,
        )
        lastTouchX = event.x
        lastTouchY = event.y
        invalidate()
    }

    private val Canvas.isPortrait get() = height >= width

    private val Float.px get() = this * context.resources.displayMetrics.density

    private fun Canvas.kotlinDrawRect(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        paint: Paint,
    ) = this.drawRect(left, top, right, bottom, paint)

    private fun genRectF(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
    ) = RectF(left, top, right, bottom)
}
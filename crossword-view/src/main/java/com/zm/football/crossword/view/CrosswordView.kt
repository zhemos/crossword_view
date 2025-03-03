package com.zm.football.crossword.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import kotlin.math.max
import kotlin.math.min

class CrosswordView(context: Context, attrs: AttributeSet?) : View(context, attrs),
    ScaleGestureDetector.OnScaleGestureListener {

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

    private val isScalable = true//outside
    private val horizontalMarginForPortrait = 0f.px
    private val verticalMarginForLandscape = 8f.px
    private val touchSlop = 10f.px
    private val scaleDetector = ScaleGestureDetector(context, this)
    private val minScale = 0.8f
    private val maxScale = 1.8f

    private var crosswordRectF = RectF()
    private var size = 0f
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var crosswordX = 0f
    private var crosswordY = 0f
    private var horizontalMargin = 0f
    private var verticalMargin = 0f
    private var scaleFactor = 1f

    fun scaleIn() {
        scaleFactor += 0.1f
        scale()
    }

    fun scaleOut() {
        scaleFactor -= 0.1f
        scale()
    }

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
        val scaleX = crosswordX + size * scaleFactor / 2
        canvas.scale(scaleFactor, scaleFactor, 0f, 0f)
        crossword.forEachIndexed { i, rows ->
            rows.forEachIndexed { j, column ->
                val right = i * size + size + horizontalMargin + crosswordX
                if (i == 7 && j == 0) logger.log("right = $right")
                canvas.kotlinDrawRect(
                    left = i * size + horizontalMargin + crosswordX,
                    top = j * size + verticalMargin + crosswordY,
                    right = right,
                    bottom = j * size + size + verticalMargin + crosswordY,
                    paint = paint,
                )
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        logger.log("$event")
        scaleDetector.onTouchEvent(event)
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

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        scaleFactor *= detector.scaleFactor
        scale()
        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {}

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
        if (isScalable.not()) return
        logger.logError("move")
        val newX = crosswordX + dx
        val newY = crosswordY + dy
        logger.log("$newX")
        val currentWidth = getCurrentWidth()
        val minX = (width - currentWidth).coerceAtMost(0f) / 2
        val maxX = width - currentWidth - minX
//        crosswordX = newX.coerceIn(
//            minimumValue = -horizontalMargin,
//            maximumValue = width - crossword.size * size * scaleFactor - horizontalMargin,
//        )
        crosswordX = newX.coerceIn(
            minimumValue = minX,
            maximumValue = maxX,
        )
        crosswordY = newY.coerceIn(
            minimumValue = -verticalMargin,
            maximumValue = height - crossword[0].size * size - verticalMargin,
        )
        logger.log("move $crosswordX $crosswordY")//-42.0 -727.0 42.0 -727.0
        lastTouchX = event.x
        lastTouchY = event.y
        invalidate()
    }

    private fun scale() {
        if (isScalable.not()) {
            scaleFactor = 1f
            return
        }
        logger.logError("scale")
        scaleFactor = max(minScale, min(scaleFactor, maxScale))
        invalidate()
    }

    private fun getCurrentWidth() = crossword.size * size * scaleFactor

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
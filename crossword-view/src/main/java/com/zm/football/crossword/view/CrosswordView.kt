package com.zm.football.crossword.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.SizeF
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

    private val isScalable = true//outside

    private val touchSlop = 10f.px
    private val minScale = 0.1f
    private val maxScale = 10f
    private val scaleDetector = ScaleGestureDetector(context, this)
    private val scaleMargin = 4f.px
    private val margin = 16f.px
    private val padding = 1f.px
    private val minCellSize = 16f.px
    private val maxCellSize = 56f.px

    private var crossword = arrayOf<Array<Int>>()
    private var wasScale = false
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var crosswordX = 0f
    private var crosswordY = 0f
    private var scaleFactor = 1f

    fun update(crossword: Array<Array<Int>>) {
        this.crossword = crossword
        invalidate()
    }

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
        val cellSize = getCurrentCellSize()
        val currentSize = getCurrentSize(cellSize)
        logger.log("draw ${currentSize.width} ${currentSize.height}")
        val offsetX = width / 2f - currentSize.width / 2f
        val offsetY = height / 2f - currentSize.height / 2f
        crossword.forEachIndexed { i, rows ->
            rows.forEachIndexed { j, column ->
                canvas.kotlinDrawRect(
                    left = i * cellSize + crosswordX + offsetX,
                    top = j * cellSize + crosswordY + offsetY,
                    right = i * cellSize + cellSize + crosswordX + offsetX,
                    bottom = j * cellSize + cellSize + crosswordY + offsetY,
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
                wasScale = false
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
        wasScale = true
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
        if (isScalable.not() || wasScale) return
        logger.logError("move")
        val newX = crosswordX + dx
        val newY = crosswordY + dy
        val cellSize = getCurrentCellSize()
        val currentSize = getCurrentSize(cellSize)
        val offsetX = width / 2f - currentSize.width / 2f
        val offsetY = height / 2f - currentSize.height / 2f
        val shift = if (scaleFactor <= 1f) 0f else scaleMargin * scaleFactor
        crosswordX = newX.coerceIn(
            minimumValue = (-currentSize.width + width - offsetX - shift).coerceAtMost(maximumValue = -offsetX),
            maximumValue = (width - currentSize.width - offsetX).coerceAtLeast(minimumValue = -offsetX + shift),
        )
        crosswordY = newY.coerceIn(
            minimumValue = (-currentSize.height + height - offsetY - shift).coerceAtMost(maximumValue = -offsetY),
            maximumValue = (height - currentSize.height - offsetY).coerceAtLeast(minimumValue = -offsetY + shift),
        )
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

    private fun getCurrentCellSize(): Float {
        val size = if (height >= width) {
            (width - margin * 2) / crossword.size
        } else {
            val columnSize = crossword.firstOrNull()?.size ?: return 0f
            (height / columnSize).toFloat()
        }.coerceIn(
            minimumValue = minCellSize,
            maximumValue = maxCellSize,
        )
        return size * scaleFactor
    }

    private fun getCurrentSize(cellSize: Float): SizeF {
        val columnSize = crossword.firstOrNull()?.size ?: return SizeF(0f, 0f)
        val width = cellSize * crossword.size
        val height = cellSize * columnSize
        return SizeF(width, height)
    }

    private val Float.px get() = this * context.resources.displayMetrics.density

    private fun Canvas.kotlinDrawRect(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        paint: Paint,
    ) = this.drawRect(left, top, right, bottom, paint)
}
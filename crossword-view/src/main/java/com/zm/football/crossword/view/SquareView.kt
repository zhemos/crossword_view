package com.zm.football.crossword.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Size
import android.util.SizeF
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import kotlin.math.max
import kotlin.math.min

class SquareView(context: Context, attrs: AttributeSet?) : View(context, attrs),
    ScaleGestureDetector.OnScaleGestureListener {

    private val logger = Logger(isDebug = true)

    private val paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
    }
    private val paint2 = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }
    private val paint3 = Paint().apply {
        color = Color.GREEN
        strokeWidth = 10f.px
        style = Paint.Style.STROKE
    }

    private val isScalable = true//outside

    private val crosswordWidth = 100f.px
    private val crosswordHeight = 100f.px
    private val horizontalMarginForPortrait = 16f.px
    private val verticalMarginForPortrait = 0f.px
    private val horizontalMarginForLandscape = 8f.px
    private val verticalMarginForLandscape = 8f.px
    private val touchSlop = 10f.px
    private val minScale = 0.1f
    private val maxScale = 10f
    private val scaleDetector = ScaleGestureDetector(context, this)

    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var crosswordX = 0f
    private var crosswordY = 0f
    private var scaleFactor = 1f

    fun scaleIn() {
        scaleFactor += 0.2f
        scale()
    }

    fun scaleOut() {
        scaleFactor -= 0.2f
        scale()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        logger.log("draw $width $height")
        val currentSize = getCurrentSize()
        val offsetX = width / 2f - currentSize.width / 2f
        val offsetY = height / 2f - currentSize.height / 2f
//        canvas.translate(centerX, centerY)
//        canvas.kotlinDrawRect(
//            left = crosswordX,
//            top = crosswordY,
//            right = crosswordX + currentSize.width / 2f,
//            bottom = crosswordY + currentSize.height / 2f,
//            paint = paint,
//        )
//        canvas.kotlinDrawRect(
//            left = crosswordX + currentSize.width / 2f,
//            top = crosswordY + currentSize.height / 2f,
//            right = crosswordX + currentSize.width,
//            bottom = crosswordY + currentSize.height,
//            paint = paint,
//        )
//        canvas.kotlinDrawRect(
//            left = crosswordX,
//            top = crosswordY + currentSize.height / 2f,
//            right = crosswordX + currentSize.width / 2f,
//            bottom = crosswordY + currentSize.height,
//            paint = paint2,
//        )
//        canvas.kotlinDrawRect(
//            left = crosswordX + currentSize.width / 2f,
//            top = crosswordY,
//            right = crosswordX + currentSize.width,
//            bottom = crosswordY + currentSize.height / 2f,
//            paint = paint2,
//        )
        canvas.kotlinDrawRect(
            left = crosswordX + offsetX,
            top = crosswordY + offsetY,
            right = crosswordX + currentSize.width + offsetX,
            bottom = crosswordY + currentSize.height + offsetY,
            paint = paint3,
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        logger.log("$event")
        scaleDetector.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                lastTouchY = event.y
                //click()
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

    private fun move(event: MotionEvent, dx: Float, dy: Float) {
        logger.logError("move")
        val newX = crosswordX + dx
        val newY = crosswordY + dy
        val currentSize = getCurrentSize()
        val offsetX = width / 2f - currentSize.width / 2f
        val offsetY = height / 2f - currentSize.height / 2f
        crosswordX = newX.coerceIn(
            minimumValue = (-currentSize.width + width - offsetX).coerceAtMost(maximumValue = -offsetX),
            maximumValue = (width - currentSize.width - offsetX).coerceAtLeast(minimumValue = -offsetX),
        )
        crosswordY = newY.coerceIn(
            minimumValue = (-currentSize.height + height - offsetY).coerceAtMost(maximumValue = -offsetY),
            maximumValue = (height - currentSize.height - offsetY).coerceAtLeast(minimumValue = -offsetY),
        )
        lastTouchX = event.x
        lastTouchY = event.y
        invalidate()
    }

    private fun scale() {
        logger.logError("scale $scaleFactor")
        if (isScalable.not()) {
            scaleFactor = 1f
            return
        }
        scaleFactor = max(minScale, min(scaleFactor, maxScale))
        invalidate()
    }

    private fun getCurrentSize() = SizeF(
        crosswordWidth * scaleFactor,
        crosswordHeight * scaleFactor,
    )

    private val Canvas.isPortrait get() = height >= width

    private val Float.px get() = this * context.resources.displayMetrics.density

    private fun Canvas.kotlinDrawRect(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        paint: Paint,
    ) = this.drawRect(left, top, right, bottom, paint)
}
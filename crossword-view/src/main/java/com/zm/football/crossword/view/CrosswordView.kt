package com.zm.football.crossword.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
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

    private val paintStroke = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 1.5f.px
    }
    private val paintSimple = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }
    private val paintFrozen = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }
    private val paintFocused = Paint().apply {
        color = Color.GRAY
        style = Paint.Style.FILL
    }
    private val paintFocusedLikeWord = Paint().apply {
        color = Color.DKGRAY
        style = Paint.Style.FILL
    }
    private val textPaint = Paint().apply {
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    private val isScalable = true//outside

    private val touchSlop = 10f.px
    private val minScale = 0.1f
    private val maxScale = 10f
    private val scaleDetector = ScaleGestureDetector(context, this)
    private val scaleMargin = 4f.px
    private val margin = 16f.px
    private val minCellSize = 16f.px
    private val maxCellSize = 56f.px
    private val textBounds = Rect()

    private var crossword = arrayOf<Array<Cell>>()
    private var rowCount: Int = 0
    private var colCount: Int = 0
    private var wasScale = false
    private var isMovingOrScaling = false
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var crosswordX = 0f
    private var crosswordY = 0f
    private var scaleFactor = 1f

    private var listener: OnClickCellListener? = null

    fun update(
        crossword: Array<Array<Cell>>,
        rowCount: Int = crossword.size,
        colCount: Int = crossword.firstOrNull()?.size ?: 0,
    ) {
        this.crossword = crossword
        this.rowCount = rowCount
        this.colCount = colCount
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

    fun setOnClickCellListener(listener: OnClickCellListener?) {
        this.listener = listener
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cellSize = getCurrentCellSize()
        val currentSize = getCurrentSize(cellSize)
        val offsetX = width / 2f - currentSize.width / 2f
        val offsetY = height / 2f - currentSize.height / 2f
        canvas.drawCrossword(cellSize, offsetX, offsetY)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        logger.log("$event")
        scaleDetector.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                lastTouchY = event.y
                isMovingOrScaling = false
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
                if (isMovingOrScaling.not()) {
                    click(event)
                }
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

    private fun Canvas.drawCrossword(
        cellSize: Float,
        offsetX: Float,
        offsetY: Float,
    ) {
        for (col in 0 until colCount) {
            for (row in 0 until rowCount) {
                val cell = crossword[row][col]
                val paint = when (cell) {
                    is Cell.Focusable.Focused -> paintFocused
                    is Cell.Focusable.FocusedLikeWord -> paintFocusedLikeWord
                    is Cell.Focusable.Simple -> paintSimple
                    is Cell.Frozen -> paintFrozen
                    Cell.None -> null
                }
                paint?.let {
                    drawCell(col, row, cellSize, offsetX, offsetY, it)
                    drawCell(col, row, cellSize, offsetX, offsetY, paintStroke)
                    drawLetter(col, row, cellSize, offsetX, offsetY, cell.letter.uppercaseChar())
                }
            }
        }
    }

    private fun Canvas.drawCell(
        x: Int,
        y: Int,
        cellSize: Float,
        offsetX: Float,
        offsetY: Float,
        paint: Paint,
    ) {
        kotlinDrawRect(
            left = x * cellSize + crosswordX + offsetX,
            top = y * cellSize + crosswordY + offsetY,
            right = x * cellSize + cellSize + crosswordX + offsetX,
            bottom = y * cellSize + cellSize + crosswordY + offsetY,
            paint = paint,
        )
    }

    private fun Canvas.drawLetter(
        x: Int,
        y: Int,
        cellSize: Float,
        offsetX: Float,
        offsetY: Float,
        letter: Char,
    ) {
        textPaint.textSize = cellSize / 1.5f
        textPaint.getTextBounds(letter.toString(), 0, 1, textBounds)
        val textX = x * cellSize + crosswordX + offsetX + cellSize / 2f
        val textY = y * cellSize + crosswordY + offsetY + cellSize / 2f - (textBounds.top + textBounds.bottom) / 2
        drawText(letter.toString(), textX, textY, textPaint)
    }

    private fun click(event: MotionEvent) {
        logger.logError("click")
        val cellSize = getCurrentCellSize()
        val currentSize = getCurrentSize(cellSize)
        val offsetX = width / 2f - currentSize.width / 2f
        val offsetY = height / 2f - currentSize.height / 2f
        val touchX = (event.x - crosswordX - offsetX) / 1f
        val touchY = (event.y - crosswordY - offsetY) / 1f
        val column = (touchX / cellSize).toInt()
        val row = (touchY / cellSize).toInt()
        logger.log("${event.x} ${event.y}")
        logger.log("$column $row")
        if (row in crossword.indices && column in 0 until crossword[0].size) {
            listener?.onCell(row, column)
            logger.log("listener")
        }
    }

    private fun move(event: MotionEvent, dx: Float, dy: Float) {
        if (isScalable.not() || wasScale) return
        logger.logError("move")
        isMovingOrScaling = true
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
        isMovingOrScaling = true
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

    interface OnClickCellListener {
        fun onCell(x: Int, y: Int)
    }
}
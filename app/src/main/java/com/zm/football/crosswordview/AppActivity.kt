package com.zm.football.crosswordview

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zm.football.crossword.view.Cell
import com.zm.football.crossword.view.CrosswordView

typealias None = Cell.None
typealias Empty = Cell.Focusable.Empty
typealias Fill = Cell.Fill

class AppActivity : AppCompatActivity(R.layout.activity_app), CrosswordView.OnClickCellListener {

    private lateinit var crosswordView: CrosswordView

    private val crossword: Array<Array<out Cell>> = arrayOf(
        arrayOf(None,  None,  Empty, None,  None),
        arrayOf(None,  None,  Empty, None,  None),
        arrayOf(Empty, Empty, Empty, Empty, Fill('a')),
        arrayOf(None,  None,  Empty, None,  None),
        arrayOf(None,  None,  Empty, None,  None),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crosswordView = findViewById(R.id.crossword_view)
        crosswordView.setOnClickCellListener(this)
        findViewById<TextView>(R.id.btn_minus).setOnClickListener { crosswordView.scaleOut() }
        findViewById<TextView>(R.id.btn_plus).setOnClickListener { crosswordView.scaleIn() }
        crosswordView.update(crossword)
    }

    override fun onCell(x: Int, y: Int) {
        if (crossword[x][y] !is Cell.Focusable) return
        Toast.makeText(this, "$x $y", Toast.LENGTH_SHORT).show()
    }
}
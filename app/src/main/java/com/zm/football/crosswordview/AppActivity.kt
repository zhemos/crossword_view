package com.zm.football.crosswordview

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zm.football.crossword.view.Cell
import com.zm.football.crossword.view.CrosswordView

typealias None = Cell.None
typealias Simple = Cell.Focusable.Simple
typealias Focused = Cell.Focusable.Focused
typealias FocusedLikeWord = Cell.Focusable.FocusedLikeWord
typealias Frozen = Cell.Frozen

class AppActivity : AppCompatActivity(R.layout.activity_app), CrosswordView.OnClickCellListener {

    private lateinit var crosswordView: CrosswordView

    private val crossword: Array<Array<Cell>> = arrayOf(
        arrayOf(FocusedLikeWord(),  FocusedLikeWord(),  Focused(), FocusedLikeWord(),  FocusedLikeWord()),
        arrayOf(None,  None,  Simple(), None,  None),
        arrayOf(Frozen('a'), Frozen('b'), Frozen('c'), Frozen('d'), Frozen('e')),
        arrayOf(None,  None,  Simple('c'), None,  None),
        arrayOf(None,  None,  Simple('s'), None,  None),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crosswordView = findViewById(R.id.crossword_view)
        crosswordView.setOnClickCellListener(this)
        findViewById<TextView>(R.id.btn_minus).setOnClickListener { crosswordView.scaleOut() }
        findViewById<TextView>(R.id.btn_plus).setOnClickListener { crosswordView.scaleIn() }
        findViewById<TextView>(R.id.btn_writer).setOnClickListener {  }
        crosswordView.update(crossword)
    }

    override fun onCell(x: Int, y: Int) {
        if (crossword[x][y] !is Cell.Focusable) return
        Toast.makeText(this, "$x $y", Toast.LENGTH_SHORT).show()
    }
}
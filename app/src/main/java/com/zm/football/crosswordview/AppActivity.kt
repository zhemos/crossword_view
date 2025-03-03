package com.zm.football.crosswordview

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.zm.football.crossword.view.CrosswordView
import com.zm.football.crossword.view.SquareView

class AppActivity : AppCompatActivity(R.layout.activity_app) {

    private lateinit var crosswordView: SquareView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crosswordView = findViewById(R.id.crossword_view)
        findViewById<TextView>(R.id.btn_minus).setOnClickListener { crosswordView.scaleOut() }
        findViewById<TextView>(R.id.btn_plus).setOnClickListener { crosswordView.scaleIn() }
    }
}
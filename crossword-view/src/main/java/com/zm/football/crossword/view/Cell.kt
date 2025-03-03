package com.zm.football.crossword.view

sealed class Cell {

    sealed class Focusable : Cell() {
        data object Empty : Focusable()
    }

    data class Fill(
        val letter: Char,
    ) : Cell()

    data object None : Cell()
}
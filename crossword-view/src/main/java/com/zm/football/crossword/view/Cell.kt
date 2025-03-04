package com.zm.football.crossword.view

sealed class Cell {
    abstract val letter: Char

    sealed class Focusable : Cell() {
        data class Simple(
            override val letter: Char = ' ',
        ) : Focusable()

        data class Focused(
            override val letter: Char = ' ',
        ) : Focusable()

        data class FocusedLikeWord(
            override val letter: Char = ' ',
        ) : Focusable()
    }

    data class Frozen(
        override val letter: Char,
    ) : Cell()

    data object None : Cell() {
        override val letter: Char get() = ' '
    }
}
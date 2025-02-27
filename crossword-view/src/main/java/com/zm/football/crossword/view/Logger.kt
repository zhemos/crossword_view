package com.zm.football.crossword.view

import android.util.Log

internal class Logger(private val isDebug: Boolean = false) {
    fun log(message: String) {
        if (isDebug) Log.d("zm1996", message)
    }

    fun logError(message: String) {
        if (isDebug) Log.e("zm1996", message)
    }
}
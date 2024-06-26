package com.example.mopera.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

fun String.insert(index: Int, charToInsert: Char): String {
    if (index == this.length) return this + charToInsert
    return StringBuilder(this).insert(index, charToInsert).toString()
}
object Node {
    var value by mutableStateOf("")
    var isArabic by mutableStateOf(false)
    private var index by mutableIntStateOf(0)

    fun changeLanguage() {
        isArabic = !isArabic
    }

    fun getRows(): List<CharArray> {
        return if (isArabic) KeysModel.ROWS_AR else KeysModel.ROWS
    }

    fun subIndex() {
        if (index > 0) index--
    }

    fun incIndex() {
        if (index < value.length) index++
    }

    fun addChar(letter: Char) {
        value = value.insert(index, letter)
        index++
    }

    fun backspace() {
        if (index > 0) {
            value = value.removeRange(index - 1, index)
            index--
        }
    }

    fun addSpace() {
        value = value.insert(index, ' ')
        index++
    }

    fun setSearchString(s: String) {
        value = s
        index = s.length
    }
    fun search() {
        // TODO Implement search feature to focus on cards.
    }
}

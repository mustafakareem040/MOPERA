package com.example.mopera.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.mopera.ui.components.insert


object Node {
    var value by mutableStateOf("")
    var isArabic by mutableStateOf(false)
    private var index by mutableIntStateOf(0)

    fun changeLanguage() {
        isArabic = !isArabic
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

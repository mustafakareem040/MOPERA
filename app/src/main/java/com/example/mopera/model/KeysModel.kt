package com.example.mopera.model

import androidx.compose.runtime.Immutable


@Immutable
object KeysModel {
    val ROWS = listOf(
        charArrayOf('1', '2', '3', '4', '5', '6', '7', '8', '9', '0'),
        charArrayOf('q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p'),
        charArrayOf('a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', ':'),
        charArrayOf('z', 'x', 'c', 'v', 'b', 'n', 'm', '.'))

    val ROWS_AR = listOf(
        charArrayOf('١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩', '٠'),
        charArrayOf('ض', 'ص', 'ث', 'ق', 'ف', 'غ', 'ع', 'ه', 'خ', 'ح'),
        charArrayOf('ش', 'س', 'ي', 'ب', 'ل', 'ا', 'ت', 'ن', 'م', 'ك'),
        charArrayOf('ظ', 'ط', 'ز', 'ر', 'ذ', 'د', 'ج', 'و')
    )
}
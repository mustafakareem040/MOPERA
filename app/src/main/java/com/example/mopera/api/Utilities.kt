package com.example.mopera.api
object Utilities {
    fun convert(input: Float): String {
        val t = input / 3600
        val hours = t.toInt()
        val minutes = ((t - hours) * 60).toInt()
        return "$hours:${minutes}h"
    }
}
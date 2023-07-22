package com.example.studentdiary.extensions

fun String.Companion.converterToPercent(value: Number): String = format("%.2f%%", value)

fun formatTimeLeft(timeLeftInMillis: Long?):String {
    val minutes = (timeLeftInMillis?.div(1000))?.div(60)
    val seconds = (timeLeftInMillis?.div(1000))?.rem(60)
    return  String.format("%02d:%02d", minutes, seconds)}

package com.example.studentdiary.extensions

fun String.Companion.converterToPercent(value: Number): String = format("%.2f%%", value)
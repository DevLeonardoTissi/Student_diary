package com.example.studentdiary.utils.concatUtils

import com.example.studentdiary.ui.TIME_ZONE_ID
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun converterMillisToDateString(timeInMillis: Long):String{
    val format = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
    format.timeZone = TimeZone.getTimeZone(TIME_ZONE_ID)

    return format.format(timeInMillis)
}
package com.example.studentdiary.utils

import android.os.Build
import com.example.studentdiary.utils.concatUtils.converterMillisToDateString
import java.util.Date

class PublicTenderSuggestion(
    val name: String? = null,
    val description: String? = null,
    val url: String? = null


) {
    @Suppress("unused")
    val date: String = converterMillisToDateString(Date().time)
    @Suppress("unused")
    val device: String = "${Build.BRAND}  ${Build.MODEL}"

}
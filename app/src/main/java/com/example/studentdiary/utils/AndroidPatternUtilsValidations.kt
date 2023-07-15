package com.example.studentdiary.utils

fun validateEmailFormat(email: String): Boolean {
    val emailPattern = android.util.Patterns.EMAIL_ADDRESS
    return emailPattern.matcher(email).matches()
}

fun validateUrlFormat(url:String):Boolean{
    val emailPattern = android.util.Patterns.WEB_URL
    return emailPattern.matcher(url).matches()
}
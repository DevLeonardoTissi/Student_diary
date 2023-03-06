package com.example.studentdiary.extensions

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.snackBar(
    message: String,
    duracao: Int = Snackbar.LENGTH_SHORT
){
    Snackbar.make(
        this,
        message,
        duracao
    ).show()
}
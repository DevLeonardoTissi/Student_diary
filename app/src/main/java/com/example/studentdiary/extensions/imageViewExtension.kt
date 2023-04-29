package com.example.studentdiary.extensions

import android.widget.ImageView
import coil.load
import com.example.studentdiary.R

fun ImageView.tryLoadImage(img: String? = null) {
    img?.let {
        load(it) {
            error(R.drawable.error)
            placeholder(R.drawable.load)
        }
    } ?: load(R.drawable.placeholder)
}
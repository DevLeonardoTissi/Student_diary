package com.example.studentdiary.utils

import android.content.Context
import android.content.Intent
import android.net.Uri


fun goToUri(address: String, context: Context) {
    val uri = Uri.parse(address)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
}
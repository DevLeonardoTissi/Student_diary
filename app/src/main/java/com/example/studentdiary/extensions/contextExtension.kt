package com.example.studentdiary.extensions

import android.content.Context
import com.example.studentdiary.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun Context.googleSignInClient(): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(this.getString(R.string.googleSignInClient_serverClientId))
        .requestEmail()
        .build()

    return GoogleSignIn.getClient(this, gso)
}

fun Context.alertDialog(
    title: String,
    message: String,
    onClickingOnPositiveButton: () -> Unit = {},
    onClickingOnNegativeButton: () -> Unit = {}
) {
    MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setMessage(message)
        .setNeutralButton(getString(R.string.common_cancel)) { _, _ ->

        }
        .setNegativeButton(getString(R.string.common_decline)) { _, _ ->
            onClickingOnNegativeButton()
        }
        .setPositiveButton(getString(R.string.common_confirm)) { _, _ ->
            onClickingOnPositiveButton()

        }
        .show()
}
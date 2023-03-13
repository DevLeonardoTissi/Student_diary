package com.example.studentdiary.extensions

import android.content.Context
import com.example.studentdiary.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

fun Context.googleSignInClient(): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(this.getString(R.string.googleSignInClient_serverClientId))
        .requestEmail()
        .build()

    return GoogleSignIn.getClient(this, gso)
}
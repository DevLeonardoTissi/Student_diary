package com.example.studentdiary.utils

import android.content.Context
import com.example.studentdiary.extensions.googleSignInClient
import com.facebook.AccessToken

 fun exitGoogleAndFacebookAccount(context : Context) {
    context.googleSignInClient().signOut()
    checkAndLogoutIfLoggedInFacebook()
}

private fun checkAndLogoutIfLoggedInFacebook() {
    val accessToken = AccessToken.getCurrentAccessToken()
    val isLoggedIn = accessToken != null && !accessToken.isExpired
    if (isLoggedIn) {
        AccessToken.setCurrentAccessToken(null)
    }
}
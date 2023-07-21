package com.example.studentdiary.extensions

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
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
    icon: Int? = null,
    onClickingOnPositiveButton: () -> Unit = {},
    onClickingOnNegativeButton: () -> Unit = {}
) {
    val alertDialog = MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setMessage(message)
        .setNegativeButton(getString(R.string.common_cancel)) { _, _ ->
            onClickingOnNegativeButton()
        }
        .setPositiveButton(getString(R.string.common_confirm)) { _, _ ->
            onClickingOnPositiveButton()
        }
    icon?.let {
        alertDialog.setIcon(it)
    }
    alertDialog.show()
}


fun Context.isOnline(): Boolean {
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(
        this,
        message,
        duration
    ).show()

}

fun Context.showToastNoConnectionMessage() {
    toast(getString(R.string.default_message_noConnection))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}



fun formatTimeLeft(timeLeftInMillis: Long?):String {
    val minutes = (timeLeftInMillis?.div(1000))?.div(60)
    val seconds = (timeLeftInMillis?.div(1000))?.rem(60)
    return  String.format("%02d:%02d", minutes, seconds)

}



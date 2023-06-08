package com.example.studentdiary.extensions

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import com.example.studentdiary.R
import com.example.studentdiary.notifications.Notification
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
        .setNegativeButton(getString(R.string.common_cancel)) { _, _ ->
            onClickingOnNegativeButton()
        }
        .setPositiveButton(getString(R.string.common_confirm)) { _, _ ->
            onClickingOnPositiveButton()

        }
        .show()
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

fun Context.showNotificationSuggestion() {
    val imgNotificationSuggestion =
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT56PuFScM4ZH3VwzMQOX50aenzv2hM2FlM5VEWvuy-wr3l1MVJU6bWUYOMi3rF_LSC55c&usqp=CAU"
    Notification(this).show(
        title = getString(R.string.suggestion_notification_title),
        description = getString(R.string.suggestion_notification_description),
        img = imgNotificationSuggestion
    )

}
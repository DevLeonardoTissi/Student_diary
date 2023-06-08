package com.example.studentdiary.notifications

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import com.example.studentdiary.R
import com.example.studentdiary.ui.CHANNEL_IDENTIFIER
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Notification(private val context: Context) {


    private val manager:NotificationManager  by lazy {
        context.getSystemService(FirebaseMessagingService.NOTIFICATION_SERVICE) as NotificationManager
    }

    companion object {
        private var id = 1
    }

    fun show(title: String, description: String, img: String? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            val image = trySearchImg(img)
            val style = createStyle(image, description)
            val notification = createNotification(title, description, style)
            manager.notify(id, notification)
        }
    }

    private suspend fun trySearchImg(img: String?): Bitmap? {
        val request = ImageRequest.Builder(context)
            .data(img)
            .build()
        return context.imageLoader.execute(request).drawable?.toBitmap()
    }


    private fun createNotification(
        title: String,
        description: String,
        style: NotificationCompat.Style
    ): Notification {
        return NotificationCompat.Builder(context, CHANNEL_IDENTIFIER)
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(R.drawable.ic_notification_add)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setStyle(style)
            .build()
    }

    private fun createStyle(img: Bitmap?, description: String): NotificationCompat.Style {
        return img?.let {
            NotificationCompat.BigPictureStyle().bigPicture(it)
        } ?: NotificationCompat.BigTextStyle().bigText(description)
    }

}
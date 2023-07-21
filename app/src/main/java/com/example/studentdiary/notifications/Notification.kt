package com.example.studentdiary.notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PRIVATE
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import com.example.studentdiary.R
import com.example.studentdiary.ui.CHANNEL_IDENTIFIER
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Notification(private val context: Context) {

    private val manager: NotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    companion object {
        var id = 1
            private set
    }

    fun show(
        title: String,
        description: String,
        img: String? = null,
        iconId: Int,
        isOnGoing: Boolean? = false,
        isAutoCancel: Boolean? = true,
        progress: Int? = null,
        exclusiveId: Int? = null,
        actionIcon: Int? = null,
        actionTitle: String? = null,
        actionIntent: PendingIntent? = null,
        secondActionIcon: Int? = null,
        secondActionTitle: String? = null,
        secondActionIntent: PendingIntent? = null,
        contentIntent: PendingIntent? = null
    ) {

        CoroutineScope(Dispatchers.IO).launch {
            val image = trySearchImg(img)
            val style = createStyle(image, description)
            val notification =
                createNotification(
                    title,
                    description,
                    style,
                    iconId,
                    isOnGoing ?: false,
                    isAutoCancel ?: true,
                    progress,
                    actionIcon,
                    actionTitle,
                    actionIntent,
                    secondActionIcon,
                    secondActionTitle,
                    secondActionIntent,
                    contentIntent
                )

            manager.notify(exclusiveId ?: id, notification)
            exclusiveId ?: id++
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
        style: NotificationCompat.Style,
        iconId: Int,
        isOnGoing: Boolean = false,
        isAutoCancel: Boolean = true,
        progress: Int? = null,
        actionIcon: Int? = null,
        actionTitle: String? = null,
        actionIntent: PendingIntent? = null,
        secondActionIcon: Int? = null,
        secondActionTitle: String? = null,
        secondActionIntent: PendingIntent? = null,
        contentIntent: PendingIntent? = null


    ): Notification {
        val builder = NotificationCompat.Builder(context, CHANNEL_IDENTIFIER)
            .setContentTitle(title)
            .setContentText(description)
            //.setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.logo_app))
            .setSmallIcon(iconId)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(isAutoCancel)
            .setStyle(style)
            .setVisibility(VISIBILITY_PRIVATE)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setOngoing(isOnGoing)
            .setOnlyAlertOnce(true)
            .setContentIntent(contentIntent)

        progress?.let { progressNonNull ->
            builder.setProgress(100, progressNonNull, false)
        }

        addActionIfNotNull(builder, actionIcon, actionTitle, actionIntent)
        addActionIfNotNull(builder, secondActionIcon, secondActionTitle, secondActionIntent)

        return builder.build()
    }

    private fun createStyle(img: Bitmap?, description: String): NotificationCompat.Style {
        return img?.let {
            NotificationCompat.BigPictureStyle().bigPicture(it)
        } ?: NotificationCompat.BigTextStyle().bigText(description)
    }

    private fun addActionIfNotNull(builder: NotificationCompat.Builder, icon: Int?, title: String?, intent: PendingIntent?) {
        if (listOf(icon, title, intent).all { it != null }) {
            builder.addAction(icon!!, title, intent)
        }
    }
}
package com.example.studentdiary.workManager

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.studentdiary.R
import  com.example.studentdiary.notifications.Notification
import com.example.studentdiary.ui.activity.MainActivity

class DisciplineReminderWorker(context: Context, workerParams: WorkerParameters) : Worker(
    context,
    workerParams
) {
    override fun doWork(): Result {

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )


        Notification(applicationContext)
            .show(
                title = applicationContext.getString(R.string.discipline_reminder_worker_notification_title),
                description = applicationContext.getString(R.string.discipline_reminder_worker_notification_description),
                iconId = R.drawable.ic_info,
                contentIntent = pendingIntent
            )
        return Result.success()
    }
}
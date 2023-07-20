package com.example.studentdiary.workManager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.studentdiary.R
import  com.example.studentdiary.notifications.Notification

class DisciplineReminderWorker(context: Context, workerParams: WorkerParameters) : Worker(
    context,
    workerParams
) {
    override fun doWork(): Result {
        Notification(applicationContext)
            .show(
                title = applicationContext.getString(R.string.discipline_reminder_worker_notification_title),
                description = applicationContext.getString(R.string.discipline_reminder_worker_notification_description),
                iconId = R.drawable.ic_info
            )
        return Result.success()
    }
}
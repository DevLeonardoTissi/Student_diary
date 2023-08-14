package com.example.studentdiary.workManager

import android.app.PendingIntent
import android.content.Context
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.studentdiary.R
import com.example.studentdiary.notifications.Notification

class DisciplineReminderWorker(context: Context, workerParams: WorkerParameters) : Worker(
    context,
    workerParams
) {
    override fun doWork(): Result {

        val clickIntent : PendingIntent = NavDeepLinkBuilder(applicationContext)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.disciplinesFragment)
            .createPendingIntent()


        Notification(applicationContext)
            .show(
                title = applicationContext.getString(R.string.discipline_reminder_worker_notification_title),
                description = applicationContext.getString(R.string.discipline_reminder_worker_notification_description),
                iconId = R.drawable.ic_info,
                contentIntent = clickIntent
            )
        return Result.success()
    }
}
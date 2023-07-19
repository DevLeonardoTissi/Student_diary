package com.example.studentdiary.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.studentdiary.R
import com.example.studentdiary.notifications.Notification
import com.example.studentdiary.services.PomodoroService

class BatteryStatusBroadcastReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pomodoroIsRunning = PomodoroService.timerIsRunning.value
        if (pomodoroIsRunning == true){
            if (intent.action == Intent.ACTION_BATTERY_LOW){
                Notification(context).show(
                    title = context.getString(R.string.low_battery_notification_pomodoro_title),
                    description = context.getString(R.string.low_battery_notification_pomodoro_description),
                    iconId = R.drawable.ic_low_battery
                )
            }
        }
    }
}
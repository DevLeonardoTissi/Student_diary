package com.example.studentdiary.utils.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.studentdiary.ui.POMODORO_ACTION_PAUSE
import com.example.studentdiary.ui.POMODORO_ACTION_START
import com.example.studentdiary.ui.POMODORO_ACTION_STOP
import com.example.studentdiary.utils.services.PomodoroService

class PomodoroNotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, int: Intent?) {
        val intentPomodoroService = Intent(context, PomodoroService::class.java)
        context?.let {contextNonNull ->
            when (int?.action) {
                POMODORO_ACTION_START -> context.startService(intentPomodoroService)
                POMODORO_ACTION_STOP -> context.stopService(intentPomodoroService)
                POMODORO_ACTION_PAUSE -> PomodoroService.pauseTimer(contextNonNull)
                else -> {}
            }
        }
    }
}
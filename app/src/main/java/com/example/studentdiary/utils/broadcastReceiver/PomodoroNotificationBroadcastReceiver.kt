package com.example.studentdiary.utils.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.studentdiary.ui.POMODORO_ACTION_PAUSE
import com.example.studentdiary.ui.POMODORO_ACTION_START
import com.example.studentdiary.ui.POMODORO_ACTION_STOP
import com.example.studentdiary.utils.services.PomodoroService

class PomodoroNotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        val intentPomodoroService = Intent(p0, PomodoroService::class.java)
        when (p1?.action) {
            POMODORO_ACTION_START ->
                p0?.startService(intentPomodoroService)


            POMODORO_ACTION_STOP ->
                p0?.stopService(intentPomodoroService)

            POMODORO_ACTION_PAUSE -> PomodoroService.pauseTimer()
        }

    }
}
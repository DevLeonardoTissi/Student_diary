package com.example.studentdiary.utils.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.studentdiary.utils.services.PomodoroService

class PomodoroNotificationBroadcastReceiver:BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        val action = p1?.action
        if (action.equals("STOP")){
            PomodoroService.pauseTimer()
        }

    }
}
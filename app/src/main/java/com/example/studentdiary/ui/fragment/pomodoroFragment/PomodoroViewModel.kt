package com.example.studentdiary.ui.fragment.pomodoroFragment

import android.app.NotificationManager
import androidx.lifecycle.ViewModel
import com.example.studentdiary.utils.services.PomodoroService

class PomodoroViewModel : ViewModel() {

    var timeLeftInMillis = PomodoroService.timeLeftInMillis

    val timerIsRunning = PomodoroService.timerIsRunning

    fun pauseTimer(notificationManager: NotificationManager) = PomodoroService.pauseTimer(notificationManager)

}
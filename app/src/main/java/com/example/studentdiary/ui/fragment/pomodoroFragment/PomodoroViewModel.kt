package com.example.studentdiary.ui.fragment.pomodoroFragment

import androidx.lifecycle.ViewModel
import com.example.studentdiary.utils.services.PomodoroService

class PomodoroViewModel : ViewModel() {

    var timeLeftInMillis = PomodoroService.timeLeftInMillis

    val timerIsRunning = PomodoroService.timerIsRunning

    fun pauseTimer() = PomodoroService.pauseTimer()

    fun getValuePomodoroTimer() = PomodoroService.getValuePomodoroTimer()

    val pomodoroStartTime = PomodoroService.pomodoroStartTime

    val intervalTime = PomodoroService.interalTime

    fun setValueIntervalTimer(time: Long) = PomodoroService.setValueIntervalTimer(time)
    fun setValuePomodoroTimer(time: Long) = PomodoroService.setValuePomodoroTimer(time)


}
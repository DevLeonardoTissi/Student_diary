package com.example.studentdiary.ui.fragment.pomodoroFragment

import androidx.lifecycle.ViewModel
import com.example.studentdiary.utils.services.PomodoroService

class PomodoroViewModel : ViewModel() {

    var pomodoroLeftTime = PomodoroService.pomodoroLeftTime

    val timerIsRunning = PomodoroService.timerIsRunning

    fun pauseTimer() = PomodoroService.pauseTimer()

    fun getValuePomodoroTimer() = PomodoroService.getValuePomodoroStartTimer()

    val pomodoroStartTime = PomodoroService.pomodoroStartTime

    val intervalLeftTime = PomodoroService.intervalLeftTime

    val intervalStartTime = PomodoroService.interalStartTime

    fun setValueIntervalTimer(time: Long) = PomodoroService.setValueIntervalTimer(time)
    fun setValuePomodoroTimer(time: Long) = PomodoroService.setValuePomodoroTimer(time)


}
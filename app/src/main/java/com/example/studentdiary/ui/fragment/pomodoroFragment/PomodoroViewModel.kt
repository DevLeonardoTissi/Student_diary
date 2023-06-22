package com.example.studentdiary.ui.fragment.pomodoroFragment

import androidx.lifecycle.ViewModel
import com.example.studentdiary.utils.services.PomodoroService

class PomodoroViewModel : ViewModel() {

    var pomodoroLeftTime = PomodoroService.pomodoroLeftTime

    val timerIsRunning = PomodoroService.timerIsRunning

    val pomodoroStartTime = PomodoroService.pomodoroStartTime

    val intervalLeftTime = PomodoroService.intervalLeftTime

    val intervalStartTime = PomodoroService.interalStartTime
    fun pauseTimer() = PomodoroService.pauseTimer()
    fun setValueIntervalStartTime(time: Long) = PomodoroService.setValueIntervalStartTime(time)
    fun setValuePomodoroStartTime(time: Long) = PomodoroService.setValuePomodoroStartTime(time)

}
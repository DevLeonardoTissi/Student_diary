package com.example.studentdiary.ui.fragment.pomodoroFragment

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.studentdiary.services.PomodoroService

class PomodoroViewModel : ViewModel() {

    var pomodoroLeftTime = PomodoroService.leftTime

    val timerIsRunning = PomodoroService.timerIsRunning

    val pomodoroStartTime = PomodoroService.pomodoroStartTime

    val intervalStartTime = PomodoroService.interalStartTime

    val pomodoroState = PomodoroService.pomodoroState

    val extraIntervalStartTime = PomodoroService.extraIntervalStartTime

    val pomodoroCycles = PomodoroService.pomodoroCycle
    fun pauseTimer(context:Context) = PomodoroService.pauseTimer(context)
    fun setValueIntervalStartTime(time: Long) = PomodoroService.setValueIntervalStartTime(time)
    fun setValuePomodoroStartTime(time: Long) = PomodoroService.setValuePomodoroStartTime(time)
    fun setValueExtraIntervalStartTime(time: Long) =
        PomodoroService.setValueExtraIntervalStartTime(time)

    fun setPomodoroCycles(numCycles: Int) =
        PomodoroService.setPomodoroCycles(numCycles)


}

package com.example.studentdiary.utils.services

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.studentdiary.R
import com.example.studentdiary.extensions.formatTimeLeft
import com.example.studentdiary.notifications.Notification
import org.koin.android.ext.android.inject

class PomodoroService : Service() {
    private val notificationManager: NotificationManager by inject()

    private val notificationsPomodoroId: Int = Int.MAX_VALUE
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startPomodoroTimer()
        return START_NOT_STICKY
    }


    companion object {
        private lateinit var countDownTimer: CountDownTimer

        private val _pomodoroStartTime = MutableLiveData<Long>(3000)
        val pomodoroStartTime: LiveData<Long> = _pomodoroStartTime

        private val _intervalStartTime = MutableLiveData<Long>(3000)
        val interalStartTime: LiveData<Long> = _intervalStartTime

        private val _extraIntervalStartTime = MutableLiveData<Long>(10000)
        val extraIntervalStartTime: LiveData<Long> = _extraIntervalStartTime


        private val _timerIsRunning = MutableLiveData(false)
        val timerIsRunning: LiveData<Boolean> = _timerIsRunning

        private var _extraIntervalLeftTime =
            MutableLiveData<Long?>(extraIntervalStartTime.value)
        val extraIntervalLeftTime: LiveData<Long?> = _extraIntervalLeftTime


        private var _pomodoroLeftTime = MutableLiveData<Long?>(pomodoroStartTime.value)
        val pomodoroLeftTime: LiveData<Long?> = _pomodoroLeftTime


        private val _intervalLeftTime = MutableLiveData<Long?>(interalStartTime.value)
        val intervalLeftTime: LiveData<Long?> = _intervalLeftTime


        private var isInterval: Boolean = false

        fun setValueExtraIntervalStartTime(time: Long) {
            _extraIntervalStartTime.value = time
            _extraIntervalLeftTime.value = time
        }


        fun setValuePomodoroStartTime(time: Long) {
            _pomodoroStartTime.value = time
            _pomodoroLeftTime.value = time
        }


        fun setValueIntervalStartTime(time: Long) {
            _intervalStartTime.value = time
            _intervalLeftTime.value = time
        }


        fun pauseTimer() {
            countDownTimer.cancel()
            _timerIsRunning.value = false
        }

        private val _pomodoroCycles = MutableLiveData<Int>(4)
        val pomodoroCycle:LiveData<Int> = _pomodoroCycles

        fun setPomodoroCycles (cycles:Int){
            if (pomodoroCount == pomodoroCycle.value){
                _pomodoroCycles.value = cycles
                pomodoroCount = pomodoroCycle.value!!
            }
            _pomodoroCycles.value = cycles
        }

        var pomodoroCount:Int = 0
    }


    private fun stopTimer() {
        countDownTimer.cancel()
        _pomodoroLeftTime.value = pomodoroStartTime.value
        _intervalLeftTime.value = interalStartTime.value
        _extraIntervalLeftTime.value = extraIntervalStartTime.value
        isInterval = false
        _timerIsRunning.value = false
        pomodoroCount = 0
        notificationManager.cancel(Int.MAX_VALUE)
    }


    fun startPomodoroTimer() {
        if (isInterval) {
            if (pomodoroCount == pomodoroCycle.value) {
                startExtraIntervalTimer()
            } else {
                startIntervalTimer()
            }

        } else {

            pomodoroLeftTime.value?.let {
                countDownTimer = object : CountDownTimer(it, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        _pomodoroLeftTime.value = millisUntilFinished
                        showPomodoroNotification(
                            applicationContext,String.format(
                                "%s %s",
                                applicationContext.getString(R.string.pomodoro_notification_description),
                                formatTimeLeft(pomodoroLeftTime.value)
                            ), calculateTimerPercent(
                                pomodoroStartTime.value, pomodoroLeftTime.value
                            )
                        )
                    }

                    override fun onFinish() {
                        pomodoroCount ++
                        notificationManager.cancel(notificationsPomodoroId)
                        isInterval = true
                        _pomodoroLeftTime.value = pomodoroStartTime.value


                        if (pomodoroCount == pomodoroCycle.value) {
                            startExtraIntervalTimer()
                        } else {
                            startIntervalTimer()
                        }

                    }
                }


                countDownTimer.start()
                _timerIsRunning.value = true
            }

        }
    }


    private fun startIntervalTimer() {
        intervalLeftTime.value?.let {
            countDownTimer = object : CountDownTimer(it, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    _intervalLeftTime.value = millisUntilFinished
                    showPomodoroNotification(
                        applicationContext,String.format(
                            "%s %s",
                            applicationContext.getString(R.string.pomodoro_interval_notification_description),
                            formatTimeLeft(intervalLeftTime.value)
                        ), calculateTimerPercent(
                            interalStartTime.value, intervalLeftTime.value
                        )
                    )
                }

                override fun onFinish() {
                    _intervalLeftTime.value = interalStartTime.value
                    notificationManager.cancel(notificationsPomodoroId)
                    isInterval = false
                    startPomodoroTimer()

                }
            }

            countDownTimer.start()
            _timerIsRunning.value = true
        }

    }

    private fun startExtraIntervalTimer() {


        extraIntervalLeftTime.value?.let {
            countDownTimer = object : CountDownTimer(it, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    _extraIntervalLeftTime.value = millisUntilFinished
                    showPomodoroNotification(
                        applicationContext,String.format(
                            "%s %s",
                            applicationContext.getString(R.string.pomodoro_extra_interval_notification_description),
                            formatTimeLeft(extraIntervalLeftTime.value)
                        ), calculateTimerPercent(
                            extraIntervalStartTime.value, extraIntervalLeftTime.value
                        )
                    )
                }

                override fun onFinish() {
                    pomodoroCount = 0
                    _extraIntervalLeftTime.value = extraIntervalStartTime.value
                    notificationManager.cancel(notificationsPomodoroId)
                    isInterval = false
                    startPomodoroTimer()
                }
            }

            countDownTimer.start()
            _timerIsRunning.value = true
        }
    }


    private fun showPomodoroNotification(context: Context, description:String, progress: Int) {
        Notification(context).show(
            title = context.getString(R.string.pomodoro_notification_title),
            description = description,
            iconId = R.drawable.ic_time,
            isOnGoing = true,
            isAutoCancel = false,
            progress = progress,
            exclusiveId = notificationsPomodoroId
        )
    }






    fun calculateTimerPercent(totalTime: Long?, timeLeft: Long?): Int {
        return timeLeft?.let {
            totalTime?.let {
                ((totalTime - timeLeft) * 100 / totalTime).toInt()
            }
        } ?: 100
    }


    override fun onTaskRemoved(rootIntent: Intent?) {
        stopSelf()
    }

    override fun onDestroy() {
        stopTimer()
    }
}
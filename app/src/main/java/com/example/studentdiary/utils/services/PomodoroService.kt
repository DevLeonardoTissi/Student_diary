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


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startPomodoroTimer()
        return START_NOT_STICKY
    }


    companion object {
        private lateinit var countDownTimer: CountDownTimer


        private val _timerIsRunning = MutableLiveData(false)
        val timerIsRunning: LiveData<Boolean> = _timerIsRunning


        private var _timeLeftInMillis = MutableLiveData<Long?>(null)
        val timeLeftInMillis: LiveData<Long?> = _timeLeftInMillis

        private val _pomodoroStartTime = MutableLiveData<Long>(1500000)
        val pomodoroStartTime: LiveData<Long> = _pomodoroStartTime

        private val _intervalTime = MutableLiveData<Long>(300000)
        val interalTime: LiveData<Long> = _intervalTime


        private var isInterval:Boolean = false


        fun setValuePomodoroTimer(timer: Long) {
            _pomodoroStartTime.value = timer
            setTimeLeft(timer)
        }

        fun getValuePomodoroTimer(): Long? = pomodoroStartTime.value


        fun setValueIntervalTimer(timer: Long) {
            _intervalTime.value = timer
        }

        private fun setTimerIsRunning(isRunning: Boolean) {
            _timerIsRunning.value = isRunning
        }
        private fun setTimeLeft(time: Long?) {
            _timeLeftInMillis.value = time
        }

        fun pauseTimer() {
            countDownTimer.cancel()
            setTimerIsRunning(false)

        }
    }

    private fun getValueIntervalTimer(): Long? = interalTime.value



    fun getTimeLeft(): Long? = timeLeftInMillis.value

    private fun setIsInterval(state: Boolean) {
        isInterval = state
    }

    private fun getIsInterval(): Boolean = isInterval


    private fun stopTimer() {
        countDownTimer.cancel()
        setTimeLeft(getValuePomodoroTimer())
        setIsInterval(false)
        setTimerIsRunning(false)
        notificationManager.cancel(Int.MAX_VALUE)
    }


    fun startPomodoroTimer() {
        if (getIsInterval()) {
            startIntervalTimer()
        } else {

            val timer = getTimeLeft() ?: getValuePomodoroTimer()!!
            countDownTimer = object : CountDownTimer(timer, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    setTimeLeft(millisUntilFinished)
                    showPomodoroNotification(
                        applicationContext, calculateTimerPercent(
                            getValuePomodoroTimer(), getTimeLeft()
                        )
                    )
                }

                override fun onFinish() {
                    setIsInterval(true)
                    startIntervalTimer()
                }
            }


            countDownTimer.start()
            setIsInterval(false)
            setTimerIsRunning(true)
        }
    }


    private fun startIntervalTimer() {
        if (getIsInterval() && (getTimeLeft()?.div(1000))?.rem(60) == 0L) {
            setTimeLeft(getValueIntervalTimer())
        }

        val timer = getTimeLeft() ?: 0
        countDownTimer = object : CountDownTimer(timer, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                setTimeLeft(millisUntilFinished)
                showIntervalNotification(
                    applicationContext, calculateTimerPercent(
                        getValuePomodoroTimer(), getTimeLeft()
                    )
                )
            }

            override fun onFinish() {
                setTimeLeft(getValuePomodoroTimer())
                setIsInterval(false)
                startPomodoroTimer()
            }
        }

        countDownTimer.start()
        setIsInterval(true)
        setTimerIsRunning(true)
    }


    private fun showPomodoroNotification(context: Context, progress: Int) {
        Notification(context).show(
            title = context.getString(R.string.pomodoro_notification_title),
            description = String.format(
                "%s %s",
                context.getString(R.string.pomodoro_notification_description),
                formatTimeLeft(getTimeLeft())
            ),
            iconId = R.drawable.ic_time,
            isOnGoing = true,
            isAutoCancel = false,
            progress = progress,
            exclusiveId = Int.MAX_VALUE
        )
    }


    private fun showIntervalNotification(context: Context, progress: Int) {
        Notification(context).show(
            title = context.getString(R.string.pomodoro_notification_title),
            description = String.format(
                "%s %s",
                context.getString(R.string.pomodoro_interval_notification_description),
                formatTimeLeft(getTimeLeft())
            ),
            iconId = R.drawable.ic_time,
            isOnGoing = true,
            isAutoCancel = false,
            progress = progress,
            exclusiveId = Int.MAX_VALUE
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
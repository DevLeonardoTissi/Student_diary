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


        private var _pomodoroLeftTime = MutableLiveData<Long?>(null)
        val pomodoroLeftTime: LiveData<Long?> = _pomodoroLeftTime

        private val _pomodoroStartTime = MutableLiveData<Long>(10000)
        val pomodoroStartTime: LiveData<Long> = _pomodoroStartTime

        private val _intervalLeftTime = MutableLiveData<Long?>(null)
        val intervalLeftTime: LiveData<Long?> = _intervalLeftTime

        private val _intervalStartTime = MutableLiveData<Long>(10000)
        val interalStartTime: LiveData<Long> = _intervalStartTime


        private var isInterval:Boolean = false


        fun setValuePomodoroTimer(timer: Long) {
            _pomodoroStartTime.value = timer
            setTimeLeft(timer)
        }

        fun getValuePomodoroStartTimer(): Long? = pomodoroStartTime.value


        fun setValueIntervalTimer(timer: Long) {
            _intervalLeftTime.value = timer
            _intervalStartTime.value = timer
        }

        private fun setTimerIsRunning(isRunning: Boolean) {
            _timerIsRunning.value = isRunning
        }
        private fun setTimeLeft(time: Long?) {
            _pomodoroLeftTime.value = time
        }

        fun pauseTimer() {
            countDownTimer.cancel()
            setTimerIsRunning(false)


        }
         fun getValueIntervalStartTimer(): Long? = interalStartTime.value



    }

    private  fun getValueIntervalLeftTimer(): Long? = intervalLeftTime.value



    fun getTimeLeft(): Long? = pomodoroLeftTime.value


    private fun setIsInterval(state: Boolean) {
        isInterval = state
    }

    private fun getIsInterval(): Boolean = isInterval




    private fun stopTimer() {
        countDownTimer.cancel()
        setTimeLeft(getValuePomodoroStartTimer())
        _intervalLeftTime.value = interalStartTime.value
        setIsInterval(false)
        setTimerIsRunning(false)
        notificationManager.cancel(Int.MAX_VALUE)
    }


    fun startPomodoroTimer() {
        if (getIsInterval()) {
            startIntervalTimer()
        } else {

            val timer = getTimeLeft() ?: getValuePomodoroStartTimer()
            timer?.let {
                countDownTimer = object : CountDownTimer(timer, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        setTimeLeft(millisUntilFinished)
                        showPomodoroNotification(
                            applicationContext, calculateTimerPercent(
                                getValuePomodoroStartTimer(), getTimeLeft()
                            )
                        )
                    }

                    override fun onFinish() {
                        setIsInterval(true)
                        notificationManager.cancel(Int.MAX_VALUE)
                        _intervalLeftTime.value = interalStartTime.value
                        startIntervalTimer()
                    }
                }


                countDownTimer.start()
                setIsInterval(false)
                setTimerIsRunning(true)
            }

        }
    }


    private fun startIntervalTimer() {
        if (getIsInterval() && (intervalLeftTime.value?.div(1000))?.rem(60) == 0L) {
            _intervalLeftTime.value = getValueIntervalStartTimer()
        }

        intervalLeftTime.value?.let {
            countDownTimer = object : CountDownTimer(it, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    _intervalLeftTime.value = millisUntilFinished
                    showIntervalNotification(
                        applicationContext, calculateTimerPercent(
                            getValueIntervalStartTimer(), getValueIntervalLeftTimer()
                        )
                    )
                }

                override fun onFinish() {
                    setTimeLeft(getValuePomodoroStartTimer())
                    setIsInterval(false)
                    notificationManager.cancel(Int.MAX_VALUE)
                    startPomodoroTimer()

                }
            }

            countDownTimer.start()
            setIsInterval(true)
            setTimerIsRunning(true)
        }

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
                formatTimeLeft(getValueIntervalLeftTimer())
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
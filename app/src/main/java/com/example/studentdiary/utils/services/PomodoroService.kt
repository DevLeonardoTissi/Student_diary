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

        private val _extraIntervalStartTime = MutableLiveData<Long>(5000)
        val extraIntervalStartTime: LiveData<Long> = _extraIntervalStartTime


        private fun getValuePomodoroStartTimer(): Long? = pomodoroStartTime.value
        private fun getValueIntervalStartTimer(): Long? = interalStartTime.value

        private fun getValueExtraIntervalStartTimer(): Long? = extraIntervalStartTime.value


        private val _timerIsRunning = MutableLiveData(false)
        val timerIsRunning: LiveData<Boolean> = _timerIsRunning

        private var _extraIntervalLeftTime = MutableLiveData<Long?>(getValueExtraIntervalStartTimer())
        val extraIntervalLeftTime: LiveData<Long?> = _extraIntervalLeftTime


        private var _pomodoroLeftTime = MutableLiveData<Long?>(getValuePomodoroStartTimer())
        val pomodoroLeftTime: LiveData<Long?> = _pomodoroLeftTime


        private val _intervalLeftTime = MutableLiveData<Long?>(getValueIntervalStartTimer())
        val intervalLeftTime: LiveData<Long?> = _intervalLeftTime


        private var isInterval: Boolean = false

        fun setValeuExtraIntervalStartTime(time:Long){
            _extraIntervalStartTime.value = time
            setExtraIntervalLeftTime(time)
        }


        fun setValuePomodoroStartTime(timer: Long) {
            _pomodoroStartTime.value = timer
            setPomodoroLeftTime(timer)
        }


        fun setValueIntervalLeftTimer(time: Long?) {
            time?.let { timeNonNull ->
                _intervalLeftTime.value = timeNonNull

            }
        }

        fun setValueIntervalStartTime(time: Long) {
            _intervalStartTime.value = time
            setValueIntervalLeftTimer(time)
        }

        private fun setTimerIsRunning(isRunning: Boolean) {
            _timerIsRunning.value = isRunning
        }

        private fun setExtraIntervalLeftTime(time: Long?) {
            _extraIntervalLeftTime.value = time
        }
        private fun setPomodoroLeftTime(time: Long?) {
            _pomodoroLeftTime.value = time
        }

        fun pauseTimer() {
            countDownTimer.cancel()
            setTimerIsRunning(false)
        }

        var pomodoroQ:Int = 0
    }

    private fun getValueExtraIntervalLeftTimer(): Long? = extraIntervalLeftTime.value
    private fun getValueIntervalLeftTimer(): Long? = intervalLeftTime.value
    private fun getValuePomodoroLeftTime(): Long? = pomodoroLeftTime.value
    private fun setIsInterval(state: Boolean) {
        isInterval = state
    }
    private fun getIsInterval(): Boolean = isInterval


    private fun stopTimer() {
        countDownTimer.cancel()
        setPomodoroLeftTime(getValuePomodoroStartTimer())
        setValueIntervalLeftTimer(getValueIntervalStartTimer())
        setExtraIntervalLeftTime(getValueExtraIntervalStartTimer())
        setIsInterval(false)
        setTimerIsRunning(false)
        pomodoroQ = 0
        notificationManager.cancel(Int.MAX_VALUE)
    }


    fun startPomodoroTimer() {
        if (getIsInterval()) {
            if (pomodoroQ == 4){
                startExtraIntervalTimer()
            }else{
                startIntervalTimer()
            }

        } else {
            getValuePomodoroLeftTime() ?.let {
                countDownTimer = object : CountDownTimer(it, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        setPomodoroLeftTime(millisUntilFinished)
                        showPomodoroNotification(
                            applicationContext, calculateTimerPercent(
                                getValuePomodoroStartTimer(), getValuePomodoroLeftTime()
                            )
                        )
                    }

                    override fun onFinish() {
                        pomodoroQ ++
                        setIsInterval(true)
                        notificationManager.cancel(notificationsPomodoroId)
                        setValueIntervalLeftTimer(getValueIntervalStartTimer())

                        if (pomodoroQ == 4){
                            startExtraIntervalTimer()
                        }else{
                            startIntervalTimer()
                        }

                    }
                }


                countDownTimer.start()
                setIsInterval(false)
                setTimerIsRunning(true)
            }

        }
    }


    private fun startIntervalTimer(extraInterval: Long? = null) {

        val timer = extraInterval?: getValueIntervalLeftTimer()


        timer?.let {
            countDownTimer = object : CountDownTimer(it, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    setValueIntervalLeftTimer(millisUntilFinished)
                    showIntervalNotification(
                        applicationContext, calculateTimerPercent(
                            getValueIntervalStartTimer(), getValueIntervalLeftTimer()
                        )
                    )
                }

                override fun onFinish() {
                    setPomodoroLeftTime(getValuePomodoroStartTimer())
                    setIsInterval(false)
                    notificationManager.cancel(notificationsPomodoroId)
                    startPomodoroTimer()

                }
            }

            countDownTimer.start()
            setIsInterval(true)
            setTimerIsRunning(true)
        }

    }

    private fun startExtraIntervalTimer(extraInterval: Long? = null) {

        val timer = extraInterval?: getValueExtraIntervalLeftTimer()


        timer?.let {
            countDownTimer = object : CountDownTimer(it, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    setExtraIntervalLeftTime(millisUntilFinished)
                    showExtraIntervalNotification(
                        applicationContext, calculateTimerPercent(
                            getValueExtraIntervalStartTimer(), getValueExtraIntervalLeftTimer()
                        )
                    )
                }

                override fun onFinish() {
                    pomodoroQ = 0
                    setPomodoroLeftTime(getValuePomodoroStartTimer())
                    setIsInterval(false)
                    notificationManager.cancel(notificationsPomodoroId)
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
                formatTimeLeft(getValuePomodoroLeftTime())
            ),
            iconId = R.drawable.ic_time,
            isOnGoing = true,
            isAutoCancel = false,
            progress = progress,
            exclusiveId = notificationsPomodoroId
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
            exclusiveId = notificationsPomodoroId
        )
    }

    private fun showExtraIntervalNotification(context: Context, progress: Int) {
        Notification(context).show(
            title = context.getString(R.string.pomodoro_notification_title),
            description = String.format(
                "%s %s",
                context.getString(R.string.pomodoro_extra_interval_notification_description),
                formatTimeLeft(getValueExtraIntervalLeftTimer())
            ),
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
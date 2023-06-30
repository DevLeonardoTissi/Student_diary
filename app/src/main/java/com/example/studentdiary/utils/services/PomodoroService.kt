package com.example.studentdiary.utils.services

import android.app.NotificationManager
import android.app.PendingIntent
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
import com.example.studentdiary.ui.POMODORO_ACTION_PAUSE
import com.example.studentdiary.ui.POMODORO_ACTION_START
import com.example.studentdiary.ui.POMODORO_ACTION_STOP
import com.example.studentdiary.utils.broadcastReceiver.PomodoroNotificationBroadcastReceiver
import com.example.studentdiary.utils.enums.PomodoroState
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
        fun calculateTimerPercent(totalTime: Long?, timeLeft: Long?): Int {
            return timeLeft?.let {
                totalTime?.let {
                    ((totalTime - timeLeft) * 100 / totalTime).toInt()
                }
            } ?: 100
        }

        private val notificationsPomodoroId: Int = Int.MAX_VALUE
        private lateinit var countDownTimer: CountDownTimer

        private val _pomodoroStartTime = MutableLiveData<Long>(3000)
        val pomodoroStartTime: LiveData<Long> = _pomodoroStartTime

        private val _intervalStartTime = MutableLiveData<Long>(3000)
        val interalStartTime: LiveData<Long> = _intervalStartTime

        private val _extraIntervalStartTime = MutableLiveData<Long>(10000)
        val extraIntervalStartTime: LiveData<Long> = _extraIntervalStartTime


        private val _timerIsRunning = MutableLiveData(false)
        val timerIsRunning: LiveData<Boolean> = _timerIsRunning

        private var _leftTime = MutableLiveData<Long?>(pomodoroStartTime.value)
        val leftTime: LiveData<Long?> = _leftTime

        private val _pomodoroState = MutableLiveData<PomodoroState>(PomodoroState.POMODORO_TIMER)
        val pomodoroState: LiveData<PomodoroState> = _pomodoroState

        fun setValueExtraIntervalStartTime(time: Long) {
            _extraIntervalStartTime.value = time
            if (pomodoroState.value == PomodoroState.EXTRA_INTERVAL_TIMER) {
                _leftTime.value = time
            }
        }

        fun setValuePomodoroStartTime(time: Long) {
            _pomodoroStartTime.value = time
            if (pomodoroState.value == PomodoroState.POMODORO_TIMER) {
                _leftTime.value = time
            }
        }


        fun setValueIntervalStartTime(time: Long) {
            _intervalStartTime.value = time
            if (pomodoroState.value == PomodoroState.INTERVAL_TIMER) {
                _leftTime.value = time
            }
        }


        fun pauseTimer(context:Context) {
            countDownTimer.cancel()
            _timerIsRunning.value = false

            setupAndShowNotification(context,true)
        }

        private fun setupAndShowNotification(context: Context, isPaused:Boolean) {
            val (description, totalTime) = when (pomodoroState.value) {
                PomodoroState.POMODORO_TIMER -> {
                    Pair(
                        context.getString(R.string.pomodoro_notification_description),
                        pomodoroStartTime.value
                    )
                }

                PomodoroState.INTERVAL_TIMER -> {
                    Pair(
                        context.getString(R.string.pomodoro_interval_notification_description),
                        interalStartTime.value
                    )
                }

                else -> {
                    Pair(
                        context.getString(R.string.pomodoro_extra_interval_notification_description),
                        extraIntervalStartTime.value
                    )
                }
            }

            val (actionIcon, actionTitle, action)= if (isPaused){
                Triple(R.drawable.ic_play,
                    context.getString(R.string.pomodoro_notification_title_action_continue),
                    POMODORO_ACTION_START)
            }else{
                Triple(R.drawable.ic_pause,
                    context.getString(R.string.pomodoro_notification_title_action_pause),
                    POMODORO_ACTION_PAUSE)
            }

            showPomodoroNotification(
                context, String.format(
                    "%s %s",
                    description,
                    formatTimeLeft(leftTime.value)
                ), calculateTimerPercent(
                    totalTime, leftTime.value
                ),
                actionIcon,
                actionTitle,
                action
            )
        }

        private val _pomodoroCycles = MutableLiveData(4)
        val pomodoroCycle: LiveData<Int> = _pomodoroCycles

        fun setPomodoroCycles(cycles: Int) {
            if (pomodoroCount >= cycles) {
                pomodoroCount = 0
                _pomodoroCycles.value = cycles
            } else {
                _pomodoroCycles.value = cycles
            }
        }

        var pomodoroCount: Int = 0

        private fun showPomodoroNotification(
            context: Context,
            description: String,
            progress: Int,
            secondActionIcon: Int? = null,
            secondActionTitle: String? = null,
            secondAction: String? = null
        ) {

            val customIntent =
                Intent(context, PomodoroNotificationBroadcastReceiver::class.java)
            customIntent.action = POMODORO_ACTION_STOP

            val customPendingIntent: PendingIntent =
                PendingIntent.getBroadcast(context, 0, customIntent, PendingIntent.FLAG_IMMUTABLE)

            val secondCustomIntent =
                Intent(context, PomodoroNotificationBroadcastReceiver::class.java)
            secondCustomIntent.action = secondAction

            val secondCustomPendingIntent: PendingIntent =
                PendingIntent.getBroadcast(context, 0, secondCustomIntent, PendingIntent.FLAG_IMMUTABLE)

            Notification(context).show(
                title = context.getString(R.string.pomodoro_notification_title),
                description = description,
                iconId = R.drawable.ic_time,
                isOnGoing = true,
                isAutoCancel = false,
                progress = progress,
                exclusiveId = notificationsPomodoroId,
                actionIcon = R.drawable.ic_stop,
                actionTitle = context.getString(R.string.pomodoro_notification_title_action_stop),
                actionIntent = customPendingIntent,
                secondActionIcon = secondActionIcon,
                secondActionTitle = secondActionTitle,
                secondActionIntent = secondCustomPendingIntent
            )
        }

    }

    private fun stopTimer() {
        countDownTimer.cancel()
        _leftTime.value = pomodoroStartTime.value
        _pomodoroState.value = PomodoroState.POMODORO_TIMER
        _timerIsRunning.value = false
        pomodoroCount = 0
        notificationManager.cancel(Int.MAX_VALUE)
    }


    fun startPomodoroTimer() {
        when (pomodoroState.value) {
            PomodoroState.INTERVAL_TIMER -> {
                startIntervalTimer()
            }

            PomodoroState.EXTRA_INTERVAL_TIMER -> {
                startExtraIntervalTimer()
            }

            else -> {
                leftTime.value?.let {
                    countDownTimer = object : CountDownTimer(it, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            _leftTime.value = millisUntilFinished
                            setupAndShowNotification(applicationContext,false)
                        }

                        override fun onFinish() {
                            pomodoroCount++
                            notificationManager.cancel(notificationsPomodoroId)
                            if (pomodoroCount == pomodoroCycle.value) {
                                _pomodoroState.value = PomodoroState.EXTRA_INTERVAL_TIMER
                                _leftTime.value = extraIntervalStartTime.value
                                startExtraIntervalTimer()
                            } else {
                                _pomodoroState.value = PomodoroState.INTERVAL_TIMER
                                _leftTime.value = interalStartTime.value
                                startIntervalTimer()
                            }
                        }
                    }
                    countDownTimer.start()
                    _timerIsRunning.value = true
                }

            }
        }
    }


    private fun startIntervalTimer() {
        leftTime.value?.let {
            countDownTimer = object : CountDownTimer(it, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    _leftTime.value = millisUntilFinished
                    setupAndShowNotification(applicationContext,false)
                }

                override fun onFinish() {
                    _leftTime.value = pomodoroStartTime.value
                    notificationManager.cancel(notificationsPomodoroId)
                    _pomodoroState.value = PomodoroState.POMODORO_TIMER
                    startPomodoroTimer()

                }
            }

            countDownTimer.start()
            _timerIsRunning.value = true
        }

    }

    private fun startExtraIntervalTimer() {
        leftTime.value?.let {
            countDownTimer = object : CountDownTimer(it, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    _leftTime.value = millisUntilFinished
                    setupAndShowNotification(applicationContext,false)
                }

                override fun onFinish() {
                    pomodoroCount = 0
                    _leftTime.value = extraIntervalStartTime.value
                    notificationManager.cancel(notificationsPomodoroId)
                    _pomodoroState.value = PomodoroState.POMODORO_TIMER
                    startPomodoroTimer()
                }
            }

            countDownTimer.start()
            _timerIsRunning.value = true
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopSelf()
    }

    override fun onDestroy() {
        stopTimer()
    }
}
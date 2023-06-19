package com.example.studentdiary.utils.services

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import androidx.lifecycle.MutableLiveData
import com.example.studentdiary.R
import com.example.studentdiary.notifications.Notification

class PomodoroService : Service() {


    private val notificationManager: NotificationManager by lazy {
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }




    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startPomodoroTimer()
        return START_NOT_STICKY
    }

    private var fiveMinutesInMillis: Long = 5000
    private val countDownInterval: Long = 1000
    private var notificationId: Int? = null


    companion object {
        var timerIsRunning = MutableLiveData(false)
            private set
        private lateinit var countDownTimer: CountDownTimer
        private const val startTimeInMillis: Long = 10000
        var timeLeftInMillis = MutableLiveData<Long?>(null)
            private set


        fun pauseTimer(notificationManager: NotificationManager) {
            notificationManager.cancelAll()
            countDownTimer.cancel()
            timerIsRunning.value = false

        }
    }

    private fun stopTimer() {
        countDownTimer.cancel()
        timeLeftInMillis.value = startTimeInMillis
        timerIsRunning.value = false
        notificationManager.cancelAll()
    }


    fun startPomodoroTimer() {
        //verificar se estou no intervalo atavez de flag, e, se estiver, chamar o ccontador do intervalo sem adicionar os 5 minutos adicionais.

        val timer = timeLeftInMillis.value ?: startTimeInMillis
        countDownTimer = object : CountDownTimer(timer, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis.value = millisUntilFinished
            }

            override fun onFinish() {
                startIntervalTimer()
            }
        }

        notificationId?.let {
            notificationManager.cancel(it)
        }
        countDownTimer.start()
        notificationId = showPomodoroNotification(applicationContext)
        timerIsRunning.value = true

    }


    private fun startIntervalTimer() {
        timeLeftInMillis.value = fiveMinutesInMillis
        countDownTimer = object : CountDownTimer(timeLeftInMillis.value!!, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis.value = millisUntilFinished


            }

            override fun onFinish() {
                timeLeftInMillis.value = startTimeInMillis
                startPomodoroTimer()
            }
        }

        notificationId?.let {
            notificationManager.cancel(it)
        }
        notificationId = showIntervalNotification(applicationContext)
        countDownTimer.start()
        timerIsRunning.value = true
    }


    private fun showPomodoroNotification(context: Context): Int {
        Notification(context).show(
            title = "pomodoro",
            description = "Continue estudando",
            iconId = R.drawable.ic_notification_greeting_people,
            isOnGoing = true,
            isAutoCancel = false
        )
        return Notification.id
    }


    private fun showIntervalNotification(context: Context): Int {

        Notification(context).show(
            title = "pomodoro",
            description = "Hora do descanso",
            iconId = R.drawable.ic_notification_greeting_people,
            isOnGoing = true,
            isAutoCancel = false
        )
        return Notification.id
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopSelf()
    }

    override fun onDestroy() {
        stopTimer()
    }
}
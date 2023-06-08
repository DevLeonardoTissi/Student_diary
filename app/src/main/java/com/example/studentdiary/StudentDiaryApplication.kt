package com.example.studentdiary

import android.app.Application
import com.example.studentdiary.di.modules.*
import com.example.studentdiary.notifications.NotificationMainChannel
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class StudentDiaryApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@StudentDiaryApplication)
            modules(
                firebaseModule,
                repositoryModule,
                viewModelModule,
                retrofitModule,
                roomModule,
                adapterModule,
                notificationModule
            )
        }

        val mainChannel: NotificationMainChannel by inject()
        mainChannel.createChannel()


    }
}
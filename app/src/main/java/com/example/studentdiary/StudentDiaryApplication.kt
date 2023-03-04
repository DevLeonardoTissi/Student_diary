package com.example.studentdiary

import android.app.Application
import com.example.studentdiary.di.modules.modules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class StudentDiaryApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@StudentDiaryApplication)
            modules(modules)
        }
    }
}
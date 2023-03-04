package com.example.studentdiary

import android.app.Application
import com.example.studentdiary.di.modules.firebaseModule
import com.example.studentdiary.di.modules.repositoryModule
import com.example.studentdiary.di.modules.viewModewModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class StudentDiaryApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@StudentDiaryApplication)
            modules(firebaseModule, repositoryModule, viewModewModule)
        }
    }
}
package com.example.studentdiary.di.modules

import com.example.studentdiary.repository.FirebaseAuthRepository
import com.example.studentdiary.ui.fragment.registerFragment.RegisterViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.dsl.module

val firebaseModule = module {
    single { Firebase.auth }
}

val repositoryModule = module {
    single { FirebaseAuthRepository(get()) }
}

val viewModewModule = module {
    single { RegisterViewModel(get()) }
}
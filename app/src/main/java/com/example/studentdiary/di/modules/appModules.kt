package com.example.studentdiary.di.modules

import com.example.studentdiary.repository.DictionaryRepository
import com.example.studentdiary.repository.FirebaseAuthRepository
import com.example.studentdiary.ui.fragment.registerFragment.RegisterViewModel
import com.example.studentdiary.webClient.RetrofitLauncherDicioApi
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.dsl.module

val firebaseModule = module {
    single { Firebase.auth }
}

val retrofitModule = module {
    single {
        RetrofitLauncherDicioApi().dicioApiService
    }
}

val repositoryModule = module {
    single { FirebaseAuthRepository(get()) }
    single { DictionaryRepository(get()) }
}

val viewModewModule = module {
    single { RegisterViewModel(get()) }
}
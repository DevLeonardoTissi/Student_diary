package com.example.studentdiary.di.modules

import com.example.studentdiary.repository.DictionaryRepository
import com.example.studentdiary.repository.FirebaseAuthRepository
import com.example.studentdiary.ui.fragment.loginFragment.LoginViewModel
import com.example.studentdiary.ui.fragment.registerFragment.RegisterViewModel
import com.example.studentdiary.webClient.services.DicioApiService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val firebaseModule = module {
    single { Firebase.auth }
}

val retrofitModule = module {

    single {
        Retrofit.Builder()
            .baseUrl("https://dicio-api-ten.vercel.app/v2/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create(DicioApiService::class.java) }
}

val repositoryModule = module {
    single { FirebaseAuthRepository(get()) }
    single { DictionaryRepository(get()) }
}

val viewModelModule = module {
    single { RegisterViewModel(get()) }
    single { LoginViewModel(get()) }
}
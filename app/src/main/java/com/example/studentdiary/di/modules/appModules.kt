package com.example.studentdiary.di.modules

import androidx.room.Room
import com.example.studentdiary.database.AppDatabase
import com.example.studentdiary.repository.DictionaryRepository
import com.example.studentdiary.repository.DisciplineRepository
import com.example.studentdiary.repository.FirebaseAuthRepository
import com.example.studentdiary.repository.PublicTenderRepository
import com.example.studentdiary.ui.AppViewModel
import com.example.studentdiary.ui.fragment.dictionaryFragment.DictionaryViewModel
import com.example.studentdiary.ui.fragment.disciplineDetailsFragment.DisciplineDetailsViewModel
import com.example.studentdiary.ui.fragment.disciplineFormFragment.DisciplineFormViewModel
import com.example.studentdiary.ui.fragment.disciplinesFragment.DisciplinesViewModel
import com.example.studentdiary.ui.fragment.loginFragment.LoginViewModel
import com.example.studentdiary.ui.fragment.registerFragment.RegisterViewModel
import com.example.studentdiary.ui.recyclerView.adapter.DisciplineListAdapter
import com.example.studentdiary.webClient.services.DicioApiService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val DATABASE_NAME = "studentDiary.db"

val roomModule = module {
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    single { get<AppDatabase>().disciplineDAO }
}

val firebaseModule = module {
    single { Firebase.auth }
    single { Firebase.firestore }
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
    single { DisciplineRepository(get()) }
    single { PublicTenderRepository(get()) }
}

val viewModelModule = module {
    viewModel { RegisterViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { DisciplinesViewModel(get()) }
    viewModel {DictionaryViewModel(get())}
    viewModel{DisciplineFormViewModel(get())}
    viewModel{AppViewModel()}
    viewModel{DisciplineDetailsViewModel(get())}
}

val adapterModule = module {
    single { DisciplineListAdapter() }
}

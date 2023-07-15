package com.example.studentdiary.di.modules

import android.app.NotificationManager
import android.content.Context
import android.hardware.SensorManager
import androidx.room.Room
import com.example.studentdiary.database.AppDatabase
import com.example.studentdiary.notifications.NotificationMainChannel
import com.example.studentdiary.repository.DictionaryRepository
import com.example.studentdiary.repository.DisciplineRepository
import com.example.studentdiary.repository.FirebaseAuthRepository
import com.example.studentdiary.repository.FirebaseStorageRepository
import com.example.studentdiary.repository.PublicTenderRepository
import com.example.studentdiary.repository.SendTokenRepository
import com.example.studentdiary.ui.AppViewModel
import com.example.studentdiary.ui.fragment.dictionaryFragment.DictionaryViewModel
import com.example.studentdiary.ui.fragment.disciplineDetailsFragment.DisciplineDetailsViewModel
import com.example.studentdiary.ui.fragment.disciplineFormFragment.DisciplineFormViewModel
import com.example.studentdiary.ui.fragment.disciplinesFragment.DisciplinesViewModel
import com.example.studentdiary.ui.fragment.loginFragment.LoginViewModel
import com.example.studentdiary.ui.fragment.pomodoroFragment.PomodoroViewModel
import com.example.studentdiary.ui.fragment.publicTenderFragment.PublicTenderViewModel
import com.example.studentdiary.ui.fragment.registerFragment.RegisterViewModel
import com.example.studentdiary.ui.recyclerView.adapter.DisciplineListAdapter
import com.example.studentdiary.ui.recyclerView.adapter.PublicTenderAdapter
import com.example.studentdiary.webClient.services.DicioApiService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val DATABASE_NAME = "studentDiary.db"
private const val BASE_URL_DICTIONARY_API = "http://192.168.0.172:8080/"

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
    single { Firebase.storage }
}

val retrofitModule = module {
    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL_DICTIONARY_API)
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
    single { SendTokenRepository(get()) }
    single { FirebaseStorageRepository(get()) }
}


val viewModelModule = module {
    viewModel { RegisterViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { DisciplinesViewModel(get()) }
    viewModel { DictionaryViewModel(get()) }
    viewModel { PublicTenderViewModel(get()) }
    viewModel { AppViewModel(get(), get(), get()) }
    viewModel{PomodoroViewModel()}
    viewModel { (disciplineId: String) ->
        DisciplineFormViewModel(get(), disciplineId)
    }

    viewModel { (disciplineId: String) ->
        DisciplineDetailsViewModel(get(), disciplineId)
    }
}

val adapterModule = module {
    single { DisciplineListAdapter() }
    single { PublicTenderAdapter() }
}

val notificationModule = module {
    single { NotificationMainChannel(get(), get()) }
    single { get<Context>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
}

val sensorManagerModule = module {
    single { get<Context>().getSystemService(Context.SENSOR_SERVICE) as SensorManager }
}

package com.example.studentdiary.webClient

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RetrofitLauncherDicioApi {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://dicio-api-ten.vercel.app/v2/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

}
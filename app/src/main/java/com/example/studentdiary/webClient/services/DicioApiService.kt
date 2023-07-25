package com.example.studentdiary.webClient.services

import com.example.studentdiary.webClient.model.MeaningResponse
import com.example.studentdiary.webClient.model.RatingSender
import com.example.studentdiary.webClient.model.SentenceResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DicioApiService {

    @GET("meanings/{word}")
    suspend fun searchMeaning(@Path("word") word: String): List<MeaningResponse>

    @GET("synonyms/{word}")
    suspend fun searchSynonyms(@Path("word") word: String): List<String>

    @GET("syllables/{word}")
    suspend fun searchSyllables(@Path("word") word: String): List<String>

    @GET("sentences/{word}")
    suspend fun searchSentences(@Path("word") word: String): List<SentenceResponse>

    @POST("rating")
    suspend fun sendRating(@Body rating: RatingSender)

}

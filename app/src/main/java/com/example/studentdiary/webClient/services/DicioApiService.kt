package com.example.studentdiary.webClient.services

import com.example.studentdiary.webClient.model.MeaningsResponse
import com.example.studentdiary.webClient.model.SentencesResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface DicioApiService {

    @GET("meanings/{word}")
    suspend fun searchMeaning(@Path("word") word: String): List<MeaningsResponse>

    @GET("synonyms/{word}")
    suspend fun searchSynonyms(@Path("word") word: String): List<String>

    @GET("syllables/{word}")
    suspend fun searchSyllables(@Path("word") word: String): List<String>

    @GET("sentences/{word}")
    suspend fun searchSentences(@Path("word") word: String): List<SentencesResponse>

}

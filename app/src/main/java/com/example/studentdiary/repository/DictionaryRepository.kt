package com.example.studentdiary.repository

import com.example.studentdiary.model.Meaning
import com.example.studentdiary.model.Sentence
import com.example.studentdiary.webClient.services.DicioApiService

class DictionaryRepository(private val dicioApiService: DicioApiService) {

    suspend fun searchMeaning(word: String): List<Meaning> {
        return dicioApiService.searchMeaning(word).map { meaningsResponse ->
            meaningsResponse.meaning
        }
    }

    suspend fun searchSynonyms(word: String): List<String> {
        return dicioApiService.searchSynonyms(word)
    }

    suspend fun searchSyllables(word: String): List<String> {
        return dicioApiService.searchSyllables(word)
    }

    suspend fun searchSentences(word: String): List<Sentence> {
        return dicioApiService.searchSentences(word).map { sentencesResponse ->
            sentencesResponse.sentences
        }
    }
}
package com.example.studentdiary.ui.fragment.dictionaryFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studentdiary.model.Meaning
import com.example.studentdiary.repository.DictionaryRepository

class DictionaryViewModel(private val repository: DictionaryRepository) : ViewModel() {

    private val _fieldWord = MutableLiveData<String>()
    val fieldWord: LiveData<String> = _fieldWord



    suspend fun searchMeaning(word: String): List<Meaning> = repository.searchMeaning(word)


    suspend fun searchSynonyms(word: String) {
        repository.searchSynonyms(word)
    }

    suspend fun searchSyllables(word: String) {
        repository.searchSyllables(word)
    }

    suspend fun searchSentences(word: String) {
        repository.searchSentences(word)
    }
}


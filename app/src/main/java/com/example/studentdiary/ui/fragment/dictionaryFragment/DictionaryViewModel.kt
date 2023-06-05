package com.example.studentdiary.ui.fragment.dictionaryFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studentdiary.model.Meaning
import com.example.studentdiary.model.Sentence
import com.example.studentdiary.repository.DictionaryRepository

class DictionaryViewModel(
    private val repository: DictionaryRepository
) : ViewModel() {

    private val _fieldWord = MutableLiveData<String>()
    val fieldWord: LiveData<String> = _fieldWord

    private val _meaning = MutableLiveData<List<Meaning>?>()
    val meaning: LiveData<List<Meaning>?> = _meaning

    private val _synonyms = MutableLiveData<List<String>?>()
    val synonyms: LiveData<List<String>?> = _synonyms

    private val _syllables = MutableLiveData<List<String>?>()
    val syllables: LiveData<List<String>?> = _syllables

    private val _sentences = MutableLiveData<List<Sentence>?>()
    val sentences: LiveData<List<Sentence>?> = _sentences

    private val _searchedMeaning = MutableLiveData<Boolean>()

    private val _searchedSynonyms = MutableLiveData<Boolean>()

    private val _searchedSyllables = MutableLiveData<Boolean>()

    private val _searchedSentences = MutableLiveData<Boolean>()


    fun meaningWasResearched(): Boolean? = _searchedMeaning.value
    fun synonymsWasResearched(): Boolean? = _searchedSynonyms.value
    fun syllablesWasResearched(): Boolean? = _searchedSyllables.value
    fun sentencesWasResearched(): Boolean? = _searchedSentences.value


    fun setQuery(word: String) {
        _fieldWord.value = word
    }


    suspend fun searchMeaning(word: String) {
        _searchedMeaning.value = true
        try {
            _meaning.value = repository.searchMeaning(word)
        } catch (e: Exception) {
            _meaning.value = null
        }
    }


    suspend fun searchSynonyms(word: String) {
        _searchedSynonyms.value = true
        try {
            _synonyms.value = repository.searchSynonyms(word)
        } catch (e: Exception) {
            _synonyms.value = null
        }
    }

    suspend fun searchSyllables(word: String) {
        _searchedSyllables.value = true
        try {
            _syllables.value = repository.searchSyllables(word)
        } catch (e: Exception) {
            _syllables.value = null
        }
    }

    suspend fun searchSentences(word: String) {
        _searchedSentences.value = true
        try {
            _sentences.value = repository.searchSentences(word)
        } catch (e: Exception) {
            _sentences.value = null
        }
    }

    fun clearValues() {
        _searchedMeaning.value = false
        _searchedSynonyms.value = false
        _searchedSyllables.value = false
        _searchedSentences.value = false
    }
}


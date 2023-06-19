package com.example.studentdiary.ui.fragment.publicTenderFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studentdiary.model.PublicTender
import com.example.studentdiary.repository.PublicTenderRepository
import com.example.studentdiary.utils.PublicTenderSuggestion

class PublicTenderViewModel(private val firebaseFirestoreRepository: PublicTenderRepository) :
    ViewModel() {
    val publicTenderList: LiveData<List<PublicTender>> = firebaseFirestoreRepository.search()

    private val _cardViewSuggestionsIsOpen = MutableLiveData(false)

    fun setIsOpen(isOpen: Boolean) {
        _cardViewSuggestionsIsOpen.value = isOpen
    }

    fun getIsOpen(): Boolean = _cardViewSuggestionsIsOpen.value ?: false

    fun addPublicTenderSuggestion(publicTenderSuggestion: PublicTenderSuggestion) =
        firebaseFirestoreRepository.addPublicTenderSuggestion(publicTenderSuggestion)
}
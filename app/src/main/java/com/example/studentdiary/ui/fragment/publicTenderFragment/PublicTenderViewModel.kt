package com.example.studentdiary.ui.fragment.publicTenderFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.studentdiary.model.PublicTender
import com.example.studentdiary.repository.PublicTenderRepository

class PublicTenderViewModel(firebaseFirestoreRepository: PublicTenderRepository) :
    ViewModel() {
    val publicTenderList: LiveData<List<PublicTender>> = firebaseFirestoreRepository.search()
}
package com.example.studentdiary.ui.fragment.disciplineDetailsFragment

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentdiary.repository.DisciplineRepository
import kotlinx.coroutines.launch

class DisciplineDetailsViewModel(val repository: DisciplineRepository, val disciplineId: String) :
    ViewModel() {

     val foundDiscipline  = repository.searchIdLiveData(disciplineId)

    fun delete(context:Context) {
        viewModelScope.launch {
            repository.delete(disciplineId)
            repository.cancelWorkerDisciplineReminder(context, disciplineId)
        }
    }
}
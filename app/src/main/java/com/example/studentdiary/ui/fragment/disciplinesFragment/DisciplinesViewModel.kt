package com.example.studentdiary.ui.fragment.disciplinesFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.studentdiary.model.Discipline
import com.example.studentdiary.repository.DisciplineRepository

class DisciplinesViewModel(private val repository: DisciplineRepository) : ViewModel() {


    var disciplineList: LiveData<List<Discipline>> = repository.searchAll()

    suspend fun insert(discipline: Discipline) {
        repository.insert(discipline)
    }

    suspend fun searchId(id:String){
        repository.searchId(id)
    }
}

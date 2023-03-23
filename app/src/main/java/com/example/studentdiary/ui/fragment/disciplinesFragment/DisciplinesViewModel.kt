package com.example.studentdiary.ui.fragment.disciplinesFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studentdiary.model.Discipline
import com.example.studentdiary.repository.DisciplineRepository
import java.util.*

class DisciplinesViewModel(private val repository: DisciplineRepository) : ViewModel() {

    private var _disciplineList = MutableLiveData<List<Discipline>>(emptyList())
    var disciplineList: LiveData<List<Discipline>> = _disciplineList


    suspend fun searchAll() {
        _disciplineList.value = repository.searchAll()
    }

    suspend fun insert(){
        repository.insert(Discipline(name = UUID.randomUUID().toString(), description = "", startTime = "", endTime = "", favorite = true))
        searchAll()

    }
}
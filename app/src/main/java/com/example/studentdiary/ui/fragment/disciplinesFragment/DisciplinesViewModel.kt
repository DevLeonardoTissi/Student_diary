package com.example.studentdiary.ui.fragment.disciplinesFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentdiary.model.Discipline
import com.example.studentdiary.repository.DisciplineRepository
import kotlinx.coroutines.launch

class DisciplinesViewModel( val repository: DisciplineRepository) : ViewModel() {

    var disciplineList: LiveData<List<Discipline>> = repository.searchAll()

    private val _query = MutableLiveData<String>()
    var query: LiveData<String> = _query

     fun delete(id:String) {
        viewModelScope.launch {
            repository.delete(id)
        }
    }

    fun getQuery():String{
        query.value?.let {
            return it
        }?: return ""
    }

    fun setQuery(query:String){
        _query.value = query
    }
}

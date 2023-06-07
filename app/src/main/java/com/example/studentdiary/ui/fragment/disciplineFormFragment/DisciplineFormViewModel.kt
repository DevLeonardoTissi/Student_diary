package com.example.studentdiary.ui.fragment.disciplineFormFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentdiary.model.Discipline
import com.example.studentdiary.repository.DisciplineRepository
import kotlinx.coroutines.launch

class DisciplineFormViewModel(private val repository: DisciplineRepository, val disciplineId:String?) : ViewModel() {

    private val _discipline = MutableLiveData<Discipline?>(null)
    val discipline: LiveData<Discipline?> = _discipline

    fun insert() {
        viewModelScope.launch {
            discipline.value?.let {
                repository.insert(it)
            }
        }
    }

    fun searchDisciplineForId() {
        viewModelScope.launch {
            _discipline.value = repository.searchId(disciplineId)
        }
    }

    fun getDiscipline(): Discipline? = discipline.value
    fun setDiscipline() {
        _discipline.postValue(Discipline())
    }

    fun setName(name: String) {
        _discipline.value?.let {
            _discipline.postValue(it.copy(name = name))
        }
    }

    fun getName(): String? = discipline.value?.name
    fun setDescription(description: String) {
        _discipline.value?.let {
            _discipline.postValue(it.copy(description = description))
        }
    }

    fun getDescription(): String? = discipline.value?.description
    fun setStartTime(hour: Int, minute: Int) {
        _discipline.value?.let {
            _discipline.postValue(it.copy(startTime = Pair(hour, minute)))
        }
    }

    fun getStartTime(): Pair<Int, Int>? = discipline.value?.startTime
    fun getEndTime(): Pair<Int, Int>? = discipline.value?.endTime
    fun setEndTime(hour: Int, minute: Int) {
        _discipline.value?.let {
            _discipline.postValue(it.copy(endTime = Pair(hour, minute)))
        }
    }

    fun setFavorite(favorite: Boolean) {
        _discipline.value?.let {
            _discipline.postValue(it.copy(favorite = favorite))
        }
    }

    fun setImg(url: String? = null) {
        _discipline.value?.let {
            _discipline.postValue(it.copy(img = url))
        }
    }

    fun getImg(): String? = discipline.value?.img
    fun setDate(date: androidx.core.util.Pair<Long, Long>) {
        _discipline.value?.let {
            _discipline.postValue(it.copy(date = date))
        }
    }

    fun getDate(): androidx.core.util.Pair<Long, Long>? = discipline.value?.date

    fun setEventId(id: Long?) {
            _discipline.value?.eventId = id
    }

    fun getEventId(): Long? = discipline.value?.eventId
    fun getUserCalendarEmail(): String? = discipline.value?.userCalendarEmail
    fun setUserCalendarEmail(email: String) {
        _discipline.value?.let {
            _discipline.postValue(it.copy(userCalendarEmail = email))
        }
    }

    fun setUserEmailType(emailType: String) {
        _discipline.value?.let {
            _discipline.postValue(it.copy(userEmailType = emailType))
        }
    }

    fun getUserEmailType(): String? = discipline.value?.userEmailType
    fun setCompleted(completed: Boolean) {
        _discipline.value?.let {
            _discipline.postValue(it.copy(completed = completed))
        }
    }

}
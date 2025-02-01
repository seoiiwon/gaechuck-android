package com.example.gaechuck.ui.rent.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gaechuck.data.response.RentList
import com.example.gaechuck.repository.RentRepository
import kotlinx.coroutines.launch

class RentViewModel(private val repository: RentRepository): ViewModel() {

    // 대여 물품 리스트
    private val _rentList = MutableLiveData<List<RentList>>()
    val rentList : LiveData<List<RentList>>
        get() = _rentList

    // 초기화
    init {
        viewModelScope.launch {
            try {
                val response = repository.getRentList()
                response?.let {
                    _rentList.value = it.content
                }
            } catch (e: Exception) {
                // 에러 처리
                Log.e("LoseViewModel", "에러 발생: ${e.message}")
            }
        }
    }

    class RentViewModelFactory(private val repository: RentRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RentViewModel::class.java)) {
                return RentViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
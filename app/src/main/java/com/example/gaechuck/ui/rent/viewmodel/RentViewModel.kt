package com.example.gaechuck.ui.rent.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gaechuck.data.response.GetRentDetailResponse
import com.example.gaechuck.data.response.RentList
import com.example.gaechuck.repository.RentRepository
import kotlinx.coroutines.launch

class RentViewModel(private val repository: RentRepository): ViewModel() {

    // 대여 물품 리스트
    private val _rentList = MutableLiveData<List<RentList>>()
    val rentList : LiveData<List<RentList>>
        get() = _rentList

    // 대여 물품 상세
    private val _rentDetailData = MutableLiveData<GetRentDetailResponse>()
    val rentDetailData :MutableLiveData<GetRentDetailResponse>
        get() = _rentDetailData

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
                Log.e("RentViewModel", "에러 발생: ${e.message}")
            }
        }
    }

    fun RentDetailRetrofit(rentItemId : Int) {
        viewModelScope.launch {
            try {
                val response = repository.getRentDetailData(rentItemId)
                response?.let {
                    Log.d("RentViewModel", "데이터 받아옴: $it")
                    _rentDetailData.value = it
                }
            } catch (e: Exception) {
                // 에러 처리
                Log.e("RentViewModel", "에러 발생: ${e.message}")
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
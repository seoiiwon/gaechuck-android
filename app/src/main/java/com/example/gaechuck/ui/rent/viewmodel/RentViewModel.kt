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

    // 검색 필터링 변수
    private val _filterRentList = MutableLiveData<List<RentList>>()
    val filterRentList : LiveData<List<RentList>>
        get() = _filterRentList

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

    // 디테일 불러오기
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

    // 검색 (필터링) 기능
    fun searchRentItems(query: String) {
        val originalList = _rentList.value ?: emptyList()
        if (query.isBlank()) {
            _filterRentList.value = originalList
        } else {
            _filterRentList.value = originalList.filter { it.rentItemName.contains(query, ignoreCase = true) }
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
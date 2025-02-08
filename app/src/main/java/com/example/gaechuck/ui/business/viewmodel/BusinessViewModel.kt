package com.example.gaechuck.ui.business.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gaechuck.api.AuthManager
import com.example.gaechuck.data.response.BusinessList
import com.example.gaechuck.data.response.GetBusinessDetailResponse
import com.example.gaechuck.repository.BusinessRepository
import kotlinx.coroutines.launch

class BusinessViewModel(private val repository: BusinessRepository) : ViewModel(){

    // 제휴 물품 리스트
    private val _businessList = MutableLiveData<List<BusinessList>>()
    val businessList : LiveData<List<BusinessList>>
        get() = _businessList

    // 제휴 물품 상세
    private val _businessDetailData = MutableLiveData<GetBusinessDetailResponse>()
    val businessDetailData : MutableLiveData<GetBusinessDetailResponse>
        get() = _businessDetailData

    // 로그인 상태관리
    private val _isLoggedIn = MutableLiveData<Boolean>().apply {
        value = !AuthManager.getToken().isNullOrEmpty()
    }
    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn

    fun checkLoginStatus() {
        _isLoggedIn.value != AuthManager.getToken().isNullOrEmpty()
    }

    // 초기화
    init {
        viewModelScope.launch {
            try {
                val response = repository.getBusinessData()
                response?.let {
                    _businessList.value = it.content
                }
            }catch (e:Exception) {
                // 에러 처리
                Log.e("BusinessViewModel", "에러 발생: ${e.message}")
            }
        }
    }

    // 디테일 불러오기
    fun BusinessDetailRetrofit(coalitionId : Int) {
        viewModelScope.launch {
            try {
                val response = repository.getBusinessDetailData(coalitionId)
                response?.let {
                    _businessDetailData.value = it
                }
            }catch (e:Exception) {
                Log.e("BusinessViewModel", "에러 발생: ${e.message}")

            }
        }
    }

    class BusinessViewModelFactory(private val repository: BusinessRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BusinessViewModel::class.java)) {
                return BusinessViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}
package com.example.gaechuck.ui.lose.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gaechuck.data.response.GetLoseDetailResponse
import com.example.gaechuck.data.response.LoseList
import com.example.gaechuck.repository.LoseRepository
import kotlinx.coroutines.launch

class LoseViewModel(private val repository: LoseRepository):ViewModel() {

    // 분실물 리스트
    private val _loseList = MutableLiveData<List<LoseList>>()
    val loseList : LiveData<List<LoseList>>
        get() = _loseList
    // 분실물 개별 정보
    private val _loseDetailData = MutableLiveData<GetLoseDetailResponse>()
    val loseDetailData : MutableLiveData<GetLoseDetailResponse>
        get() = _loseDetailData

    // 초기화
    init {
        viewModelScope.launch {
            try {
                val response = repository.getLoseData()
                response?.let {
                    _loseList.value = it.content
                }
            } catch (e: Exception) {
                // 에러 처리
                Log.e("LoseViewModel", "에러 발생: ${e.message}")
            }
        }
    }

    // Detail 받아오기
    fun loseDetailRetrofit(lostItemId : Int) {
        viewModelScope.launch {
            try {
                val response = repository.getLoseDetailData(lostItemId)
                response?.let {
                    Log.d("LoseViewModel", "데이터 받아옴: $it")
                    _loseDetailData.value = it
                }
            } catch (e: Exception) {
                // 에러 처리
                Log.e("LoseViewModel", "에러 발생: ${e.message}")
            }
        }
    }

    class LoseViewModelFactory(private val repository: LoseRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoseViewModel::class.java)) {
                return LoseViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
package com.example.gaechuck.ui.rent.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gaechuck.api.AuthManager
import com.example.gaechuck.data.response.BaseResponse
import com.example.gaechuck.data.response.GetRentDetailResponse
import com.example.gaechuck.data.response.PostRentCreateResponse
import com.example.gaechuck.data.response.RentList
import com.example.gaechuck.repository.RentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    // 로그인 상태관리
    private val _isLoggedIn = MutableLiveData<Boolean>().apply {
        value = !AuthManager.getToken().isNullOrEmpty()
    }
    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn

    // 작성 이미지 상태관리
    private val _selectedImages = MutableStateFlow<List<Uri>>(emptyList())
    val selectedImages: StateFlow<List<Uri>> = _selectedImages.asStateFlow()

    private val _postResult = MutableLiveData<Result<BaseResponse<PostRentCreateResponse>>>()
    val postResult : LiveData<Result<BaseResponse<PostRentCreateResponse>>>
        get() = _postResult

    fun checkLoginStatus() {
        _isLoggedIn.value != AuthManager.getToken().isNullOrEmpty()
    }

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

    // 이미지 상태관리하기
    fun addImages(uris : List<Uri>) {
        _selectedImages.value += uris
        Log.d("RentViewModel", "Images added to ViewModel: ${_selectedImages.value}")
    }

    fun removeImages(index : Int) {
        _selectedImages.value = _selectedImages.value.toMutableList().apply {
            removeAt(index)
        }
        Log.d("RentViewModel", "Image removed from ViewModel: ${_selectedImages.value}")
    }

    // data 보내기
    fun sendData(token: String, rentItemName: String, rentItemCount: Int, file : List<Uri>, context: Context) {
        Log.d("RentViewModel", "sendData 호출됨 - name: $rentItemName, count: $rentItemCount, file : $file")

        viewModelScope.launch {
            val result =
                repository.postRentCreate(token, rentItemName,rentItemCount, file, context.contentResolver)
            _postResult.value = result

            result.onSuccess {
                Log.d("BusinessViewModel", "데이터 전송 성공: ${it}")
            }.onFailure { error ->
                Log.e("BusinessViewModel", "데이터 전송 실패: ${error.message}")
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
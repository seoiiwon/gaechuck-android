package com.example.gaechuck.ui.business.viewmodel

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
import com.example.gaechuck.data.response.BusinessList
import com.example.gaechuck.data.response.GetBusinessDetailResponse
import com.example.gaechuck.repository.BusinessRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    // 작성 이미지 상태관리
    private val _selectedImages = MutableStateFlow<List<Uri>>(emptyList())
    val selectedImages: StateFlow<List<Uri>> = _selectedImages.asStateFlow()

    private val _postResult = MutableLiveData<Result<BaseResponse<String>>>()
    val postResult : LiveData<Result<BaseResponse<String>>>
        get() = _postResult


    fun checkLoginStatus() {
        _isLoggedIn.value = !AuthManager.getToken().isNullOrEmpty()
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

    // 이미지 상태관리하기
    fun addImages(uris : List<Uri>) {
        _selectedImages.value += uris
        Log.d("ViewModel", "Images added to ViewModel: ${_selectedImages.value}")
    }

    fun removeImages(index : Int) {
        _selectedImages.value = _selectedImages.value.toMutableList().apply {
            removeAt(index)
        }
        Log.d("ViewModel", "Image removed from ViewModel: ${_selectedImages.value}")
    }

    // data 보내기
    fun sendData(token: String, coalitionName: String, benefit: String, category: String, file : List<Uri>,context: Context) {
        Log.d("BusinessViewModel", "sendData 호출됨 - name: $coalitionName, benefit: $benefit, category: $category, file : $file")

        viewModelScope.launch {
            val result =
                repository.postBusinessCreate(token, coalitionName, benefit, category, file, context.contentResolver )
            _postResult.value = result

            result.onSuccess {
                Log.d("BusinessViewModel", "데이터 전송 성공: ${it}")
            }.onFailure { error ->
                Log.e("BusinessViewModel", "데이터 전송 실패: ${error.message}")
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
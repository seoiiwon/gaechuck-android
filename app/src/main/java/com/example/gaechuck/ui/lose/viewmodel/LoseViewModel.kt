package com.example.gaechuck.ui.lose.viewmodel

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
import com.example.gaechuck.data.response.GetLoseDetailResponse
import com.example.gaechuck.data.response.LoseList
import com.example.gaechuck.repository.LoseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    // 이미지 상태관리하기
    fun addImages(uris : List<Uri>) {
        _selectedImages.value += uris
        Log.d("LoseViewModel", "Images added to ViewModel: ${_selectedImages.value}")
    }

    fun removeImages(index : Int) {
        _selectedImages.value = _selectedImages.value.toMutableList().apply {
            removeAt(index)
        }
        Log.d("LoseViewModel", "Image removed from ViewModel: ${_selectedImages.value}")
    }

    // data 보내기
    fun sendData(token: String, title: String, lostDate: String, description: String, lostLocation: String, file : List<Uri>,context: Context) {
        Log.d("LoseViewModel", "sendData 호출됨 - name: $title, lostDate: $lostDate, description: $description, lostLocation:$lostLocation, file : $file")

        viewModelScope.launch {
            val result =
                repository.postLoseCreate(token, title, lostDate, description, lostLocation, file, context.contentResolver)
            _postResult.value = result

            result.onSuccess {
                Log.d("LoseViewModel", "데이터 전송 성공: ${it}")
            }.onFailure { error ->
                Log.e("LoseViewModel", "데이터 전송 실패: ${error.message}")
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
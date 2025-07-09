package com.gaechuck_package.gaechuck.ui.lose.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gaechuck_package.gaechuck.api.ApiConnection
import com.gaechuck_package.gaechuck.api.AuthManager
import com.gaechuck_package.gaechuck.data.response.BaseResponse
import com.gaechuck_package.gaechuck.data.response.GetLoseDetailResponse
import com.gaechuck_package.gaechuck.data.response.LoseList
import com.gaechuck_package.gaechuck.data.response.PatchLoseResponse
import com.gaechuck_package.gaechuck.data.response.PostUrlResponse
import com.gaechuck_package.gaechuck.repository.LoseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoseViewModel(private val repository: LoseRepository):ViewModel() {

    private var isLastPage = false

    // 분실물 리스트
    private val _loseList = MutableLiveData<List<LoseList>>()
    val loseList : LiveData<List<LoseList>>
        get() = _loseList

    private val _totalPages = MutableLiveData<Int>()
    val totalPages: LiveData<Int> get() = _totalPages

    // 분실물 개별 정보
    private val _loseDetailData = MutableLiveData<GetLoseDetailResponse>()
    val loseDetailData : MutableLiveData<GetLoseDetailResponse>
        get() = _loseDetailData

    // 로그인 상태관리
    private val _isLoggedIn = MutableLiveData<Boolean>().apply {
        value = !AuthManager.getToken().isNullOrEmpty()
    }
    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn

    // 검색 필터링 변수
    private val _filterLoseList = MutableLiveData<List<LoseList>>()
    val filterLoseList: LiveData<List<LoseList>>
        get() = _filterLoseList

    // 검색 상태 관리
    private val _isSearchResultEmpty = MutableLiveData<Boolean>()
    val isSearchResultEmpty: LiveData<Boolean> get() = _isSearchResultEmpty

    // 작성 이미지 상태관리
    private val _selectedImages = MutableStateFlow<List<Uri>>(emptyList())
    val selectedImages: StateFlow<List<Uri>> = _selectedImages.asStateFlow()

    fun setSelectedImages(images: List<Uri>) {
        _selectedImages.value = images
    }

    private val _postResult = MutableLiveData<Result<BaseResponse<String>>>()
    val postResult : LiveData<Result<BaseResponse<String>>>
        get() = _postResult

    // 수정 상태 관리
    private val _patchResult =  MutableLiveData<Result<BaseResponse<PatchLoseResponse>>>()
    val patchResult : LiveData<Result<BaseResponse<PatchLoseResponse>>>
        get() = _patchResult

    // 삭제 상태 관리
    private val _deleteResult = MutableLiveData<Result<BaseResponse<String>>>()
    val deleteData : LiveData<Result<BaseResponse<String>>>
        get() = _deleteResult

    // url 상태관리
    private val _urlResult = MutableLiveData<Result<BaseResponse<PostUrlResponse>>>()
    val urlResult : LiveData<Result<BaseResponse<PostUrlResponse>>>
        get() = _urlResult
    private val _loseUrl = MutableLiveData<String>()
    val loseUrl: LiveData<String> get() = _loseUrl

    fun checkLoginStatus() {
        _isLoggedIn.value = !AuthManager.getToken().isNullOrEmpty()
    }

    fun loadLoseData(page: Int) {
        if (isLastPage) return
        viewModelScope.launch {
            try {
                val response = repository.getLoseData(page,9,"")
                response?.let {
                    val currentList = _loseList.value.orEmpty()
                    val newList = if (page == 0) it.content else currentList + it.content
                    _loseList.value = newList
                    _totalPages.value = it.totalPages
                    isLastPage = it.last // API 응답에 마지막 페이지 여부가 포함되어 있다고 가정
                }
            } catch (e: Exception) {
                Log.e("LoseViewModel", "Error loading data: ${e.message}")
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
    fun addImages(uris: List<Uri>) {
        _selectedImages.value += uris
        Log.d("RentViewModel", "Images added to ViewModel: ${_selectedImages.value}")
    }

    fun removeImages(index: Int) {
        _selectedImages.value = _selectedImages.value.toMutableList().apply {
            removeAt(index)
        }
        Log.d("RentViewModel", "Image removed from ViewModel: ${_selectedImages.value}")
    }

    // data 보내기
    fun sendData(title: String, lostDate: String, description: String, lostLocation: String, file : List<Uri>,context: Context) {
        Log.d("LoseViewModel", "sendData 호출됨 - name: $title, lostDate: $lostDate, description: $description, lostLocation:$lostLocation, file : $file")

        viewModelScope.launch {
            val result =
                repository.postLoseCreate(title, lostDate, description, lostLocation, file, context)
            _postResult.value = result

            result.onSuccess {
                Log.d("LoseViewModel", "데이터 전송 성공: ${it}")
            }.onFailure { error ->
                Log.e("LoseViewModel", "데이터 전송 실패: ${error.message}")
            }
        }
    }

    // 삭제하기
    fun deleteData (lostItemId: Int) {
        viewModelScope.launch {
            val result =
                repository.postLoseDelete(lostItemId)
            _deleteResult.value = result

            result.onSuccess {
                Log.d("LoseViewModel", "데이터 전송 성공: ${it}")
            }.onFailure { error ->
                Log.e("LoseViewModel", "데이터 전송 실패: ${error.message}")
            }
        }
    }

    // 아이템 수정하기
    fun patchData(
//        token: String,
        lostItemId: Int,
        title : String,
        lostDate: String,
        description : String,
        lostLocation : String,
        file: List<Uri>,
        context: Context
    ) {
        Log.d(
            "LoseViewModel",
            "patchData 호출됨 - name: $lostItemId, count: $title, file : $file"
        )

        viewModelScope.launch(Dispatchers.IO) {
            val result =
                repository.patchLoseData(
//                    token,
                    lostItemId,
                    title,
                    lostDate,
                    description,
                    lostLocation,
                    file,
                    context
                )
            withContext(Dispatchers.Main) {  // 메인 스레드에서 실행
                _patchResult.value = result
            }

            result.onSuccess {
                Log.d("LoseViewModel", "데이터 전송 성공: ${it}")
            }.onFailure { error ->
                Log.e("LoseViewModel", "데이터 전송 실패: ${error.message}")
            }
        }
    }

    // 검색 필터링 기능
    fun searchLoseItems(title: String) {
        viewModelScope.launch {
            try {
                val response = repository.getLoseData(0, 9, title)
                response?.let {
                    if (it.content.isEmpty()) {
                        _isSearchResultEmpty.postValue(true) // 검색 결과 없음

                    } else {
                        _filterLoseList.postValue(it.content) // 검색 결과를 새로운 값으로 설정
                        _isSearchResultEmpty.postValue(false)
                    }
                }
            } catch (e: Exception) {
                Log.e("LSViewModel", "에러 발생: ${e.message}")
                _isSearchResultEmpty.postValue(true) // 검색 결과 없음
            }
        }
    }

    fun searchLoseItemsPaged(title: String, page: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getLoseData(page, 9, title)
                response?.let {
                    if (page == 0 && it.content.isEmpty()) {
                        _isSearchResultEmpty.postValue(true)
                    } else {
                        _isSearchResultEmpty.postValue(false)

                        val currentList = _filterLoseList.value?.toMutableList() ?: mutableListOf()
                        if (page == 0) currentList.clear()
                        currentList.addAll(it.content)
                        _filterLoseList.postValue(currentList)

                        if (it.content.isEmpty() || it.last) {
                            isLastPage = true
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("LoseViewModel", "검색 에러: ${e.message}")
            }
        }
    }


    // url 변경
    fun LoseDetailRetrofit(chatName: String) {
        viewModelScope.launch {
            try {
                val response = ApiConnection.getRetrofitService.getUrl(chatName)

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    // API에서 받은 URL을 LiveData에 저장
                    _loseUrl.postValue(response.body()?.result?.chatUrl ?: "")
                } else {
                    Log.e("LoseViewModel", "Failed to fetch URL: ${response.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                Log.e("LoseViewModel", "Error fetching rent details", e)
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
package com.gaechuck_package.gaechuck.ui.rent.viewmodel

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
import com.gaechuck_package.gaechuck.data.response.GetRentDetailResponse
import com.gaechuck_package.gaechuck.data.response.PostUrlResponse
import com.gaechuck_package.gaechuck.data.response.RentList
import com.gaechuck_package.gaechuck.repository.RentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RentViewModel(private val repository: RentRepository): ViewModel() {

    private var currentPage = 0 // 현재 페이지 번호 관리
    private var isLoading = false // 데이터를 로딩 중인지 확인
    private var isLastPage = false // 마지막 페이지 여부

    // 대여 물품 리스트
    private val _rentList = MutableLiveData<MutableList<RentList>>()
    val rentList: LiveData<MutableList<RentList>>
        get() = _rentList

    // 대여 물품 상세
    private val _rentDetailData = MutableLiveData<GetRentDetailResponse>()
    val rentDetailData: MutableLiveData<GetRentDetailResponse>
        get() = _rentDetailData

    // 검색 필터링 변수
    private val _filterRentList = MutableLiveData<List<RentList>>()
    val filterRentList: LiveData<List<RentList>>
        get() = _filterRentList

    // 검색 상태 관리
    private val _isSearchResultEmpty = MutableLiveData<Boolean>()
    val isSearchResultEmpty: LiveData<Boolean> get() = _isSearchResultEmpty

    // 로그인 상태관리
    private val _isLoggedIn = MutableLiveData<Boolean>().apply {
        value = !AuthManager.getToken().isNullOrEmpty()
    }
    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn

    // 작성 이미지 상태관리
    private val _selectedImages = MutableStateFlow<List<Uri>>(emptyList())
    val selectedImages: StateFlow<List<Uri>> = _selectedImages.asStateFlow()

    fun setSelectedImages(images: List<Uri>) {
        _selectedImages.value = images
    }

    private val _postResult = MutableLiveData<Result<BaseResponse<String>>>()
    val postResult: LiveData<Result<BaseResponse<String>>>
        get() = _postResult

    // 수정 상태관리
    private val _patchResult = MutableLiveData<Result<BaseResponse<String>>>()
    val patchResult : LiveData<Result<BaseResponse<String>>>
        get() = _patchResult

    // 삭제 상태관리
    private val _deleteResult = MutableLiveData<Result<BaseResponse<String>>>()
    val deleteResult: LiveData<Result<BaseResponse<String>>>
        get() = _deleteResult

    // url 상태관리
    private val _urlResult = MutableLiveData<Result<BaseResponse<PostUrlResponse>>>()
    val urlResult : LiveData<Result<BaseResponse<PostUrlResponse>>>
        get() = _urlResult
    private val _rentUrl = MutableLiveData<String>()
    val rentUrl: LiveData<String> get() = _rentUrl

    fun checkLoginStatus() {
        _isLoggedIn.value != AuthManager.getToken().isNullOrEmpty()
    }

    // 초기화
    init {
        _rentList.value = mutableListOf()
    }

    // 초기 데이터
    fun loadRentData() {
        if (isLoading || isLastPage) return // 데이터 로딩 중이거나 마지막 페이지인 경우 중복 요청 방지
        isLoading = true // 데이터 로딩 시작

        viewModelScope.launch {
            try {
                val response = repository.getRentList(currentPage, "")
                response?.let {
                    val currentList = _rentList.value ?: mutableListOf()
                    if (currentPage == 0) {
                        currentList.clear() // 첫 페이지인 경우 기존 데이터 초기화
                    }
                    currentList.addAll(it.content)
                    _rentList.postValue(currentList)

                    // 페이지 번호 증가
                    currentPage++
                }
                Log.d("RentViewModel", "${response}")
            } catch (e: Exception) {
                Log.e("RentViewModel", "에러 발생: ${e.message}")
                if (currentPage > 0) {
                    currentPage--
                }
            } finally {
                isLoading = false // 데이터 로딩 완료
            }
        }
    }

    // 디테일 불러오기
    fun RentDetailRetrofit(rentItemId: Int) {
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
    fun searchRentItemsPaged(query: String, page: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getRentList(page, query)
                response?.let {
                    if (page == 0 && it.content.isEmpty()) {
                        _isSearchResultEmpty.postValue(true)
                    } else {
                        _isSearchResultEmpty.postValue(false)
                        val currentList = _filterRentList.value?.toMutableList() ?: mutableListOf()
                        if (page == 0) currentList.clear()
                        currentList.addAll(it.content)
                        _filterRentList.postValue(currentList)

                        // 마지막 페이지 판단
                        if (it.content.isEmpty()) {
                            isLastPage = true
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("RentViewModel", "검색 에러: ${e.message}")
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
    fun sendData(
//        token: String,
        rentItemName: String,
        rentItemCount: Int,
        file: List<Uri>,
        context: Context
    ) {
        Log.d(
            "RentViewModel",
            "sendData 호출됨 - name: $rentItemName, count: $rentItemCount, file : $file"
        )

        viewModelScope.launch {
            val result =
                repository.postRentCreate(rentItemName, rentItemCount, file, context)
            _postResult.value = result

            result.onSuccess {
                Log.d("BusinessViewModel", "데이터 전송 성공: ${it}")
            }.onFailure { error ->
                Log.e("BusinessViewModel", "데이터 전송 실패: ${error.message}")
            }
        }
    }

    // 아이템 삭제하기
    fun deleteData( rentItemId: Int) {
        viewModelScope.launch {
            val result =
                repository.postRentDelete(rentItemId)
            _deleteResult.value = result

            result.onSuccess {
                Log.d("RentViewModel", "데이터 전송 성공: ${it}")
            }.onFailure { error ->
                Log.e("RentViewModel", "데이터 전송 실패: ${error.message}")
            }
        }
    }

    // 아이템 수정하기
    fun patchData(
//        token: String,
        rentItemId: Int,
        rentItemName: String,
        rentItemCount: Int,
        file: List<Uri>,
        context: Context
    ) {
        Log.d(
            "RentViewModel",
            "patchData 호출됨 - name: $rentItemName, count: $rentItemCount, file : $file"
        )

        viewModelScope.launch(Dispatchers.IO) {
            val result =
                repository.patchRentData(
//                    token,
                    rentItemId,
                    rentItemName,
                    rentItemCount,
                    file,
                    context
                )
            withContext(Dispatchers.Main) {  // 메인 스레드에서 실행
                _patchResult.value = result
            }
            result.onSuccess {
                Log.d("BusinessViewModel", "데이터 전송 성공: ${it}")
            }.onFailure { error ->
                Log.e("BusinessViewModel", "데이터 전송 실패: ${error.message}")
            }
        }
    }

    // url 변경
    fun RentDetailRetrofit(chatName: String) {
        viewModelScope.launch {
            try {
                val response = ApiConnection.getRetrofitService.getUrl(chatName)

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    // API에서 받은 URL을 LiveData에 저장
                    _rentUrl.postValue(response.body()?.result?.chatUrl ?: "")
                } else {
                    Log.e("RentViewModel", "Failed to fetch URL: ${response.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                Log.e("RentViewModel", "Error fetching rent details", e)
            }
        }
    }


    class RentViewModelFactory(private val repository: RentRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RentViewModel::class.java)) {
                return RentViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
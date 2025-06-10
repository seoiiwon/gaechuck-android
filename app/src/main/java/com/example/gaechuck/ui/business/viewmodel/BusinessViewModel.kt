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
import com.example.gaechuck.data.response.PatchBusinessResponse
import com.example.gaechuck.data.response.RentList
import com.example.gaechuck.repository.BusinessRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BusinessViewModel(private val repository: BusinessRepository) : ViewModel(){

    // 제휴 물품 리스트
    private val _businessList = MutableLiveData<MutableList<BusinessList>>()
    val businessList : LiveData<MutableList<BusinessList>>
        get() = _businessList

    private var lastRequestedCategory: String? = null
    private var lastRequestedPage: Int? = null

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

    fun setSelectedImages(images: List<Uri>) {
        _selectedImages.value = images
    }

    // 검색 필터링 변수
    private val _filterBusinessList = MutableLiveData<List<BusinessList>>()
    val filterBusinessList: LiveData<List<BusinessList>>
        get() = _filterBusinessList

    // 검색 상태 관리
    private val _isSearchResultEmpty = MutableLiveData<Boolean>()
    val isSearchResultEmpty: LiveData<Boolean> get() = _isSearchResultEmpty

    private val _postResult = MutableLiveData<Result<BaseResponse<String>>>()
    val postResult : LiveData<Result<BaseResponse<String>>>
        get() = _postResult

    // 수정 상태관리
    private val _patchResult = MutableLiveData<Result<BaseResponse<PatchBusinessResponse>>>()
    val patchResult : LiveData<Result<BaseResponse<PatchBusinessResponse>>>
        get() = _patchResult

    // 삭제 결과 상태관리
    private val _deleteResult = MutableLiveData<Result<BaseResponse<String>>>()
    val deleteResult : LiveData<Result<BaseResponse<String>>>
        get() = _deleteResult

    fun checkLoginStatus() {
        _isLoggedIn.value = !AuthManager.getToken().isNullOrEmpty()
    }

    init {
        _businessList.value = mutableListOf()
    }

    // 값 불러오기
    fun loadBusinessData(page: Int, category: String? = null) {
        if (lastRequestedCategory == category && lastRequestedPage == page) {
            Log.d("BusinessViewModel", "중복 요청 방지: $category, 페이지: $page")
            return
        }

        lastRequestedCategory = category
        lastRequestedPage = page

        viewModelScope.launch {
            try {
                val response = repository.getBusinessData(page, category ?: "", "")
                response?.let {
                    val currentList = _businessList.value ?: mutableListOf()
                    if (page == 0) {
                        currentList.clear() // 첫 페이지인 경우 기존 데이터 초기화
                    }
                    currentList.addAll(it.content)
                    _businessList.postValue(currentList)
                }
            } catch (e: Exception) {
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

    fun clearBusinessList() {
        _businessList.value = mutableListOf()
        _businessList.postValue(mutableListOf()) // Clear the list
    }

    // 검색 (필터링) 기능
    fun searchBusinessItemsPaged(query: String, page: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getBusinessData(page, "", query)
                response?.let {
                    if (page == 0 && it.content.isEmpty()) {
                        _isSearchResultEmpty.postValue(true)
                    } else {
                        _isSearchResultEmpty.postValue(false)
                        val currentList = _filterBusinessList.value?.toMutableList() ?: mutableListOf()
                        if (page == 0) currentList.clear()
                        currentList.addAll(it.content)
                        _filterBusinessList.postValue(currentList)
                    }
                }
            } catch (e: Exception) {
                Log.e("BSViewModel", "에러 발생: ${e.message}")
                _isSearchResultEmpty.postValue(true)
            }
        }
    }

    // 이미지 상태관리하기
    fun addImages(uris: List<@JvmSuppressWildcards Uri>) {
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
    fun sendData(coalitionName: String, benefit: String, category: String, file : List<Uri>,context: Context) {
        Log.d("BusinessViewModel", "sendData 호출됨 - name: $coalitionName, benefit: $benefit, category: $category, file : $file")

        viewModelScope.launch {
            val result =
                repository.postBusinessCreate(coalitionName, benefit, category, file, context )
            _postResult.value = result

            result.onSuccess {
                Log.d("BusinessViewModel", "데이터 전송 성공: ${it}")
            }.onFailure { error ->
                Log.e("BusinessViewModel", "데이터 전송 실패: ${error.message}")
            }
        }
    }

    // 아이템 삭제하기
    fun deleteData (coalitionId: Int) {
        viewModelScope.launch {
            val result =
                repository.postBusinessDelete(coalitionId)
            _deleteResult.value = result

            result.onSuccess {
                Log.d("BusinessViewModel", "데이터 전송 성공: ${it}")
            }.onFailure { error ->
                Log.e("BusinessViewModel", "데이터 전송 실패: ${error.message}")
            }
        }
    }

    // 아이템 수정하기
    fun patchData(
//        token: String,
        coalitionId: Int,
        coalitionName : String,
        benefit : String,
        category : String,
        file: List<Uri>,
        context: Context
    ) {
        Log.d(
            "BusinessViewModel",
            "patchData 호출됨 - name: $coalitionId, count: $coalitionName, file : $file"
        )

        viewModelScope.launch(Dispatchers.IO) {
            val result =
                repository.patchBusinessData(
//                    token,
                    coalitionId,
                    coalitionName,
                    benefit,
                    category,
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

    class BusinessViewModelFactory(private val repository: BusinessRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BusinessViewModel::class.java)) {
                return BusinessViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}
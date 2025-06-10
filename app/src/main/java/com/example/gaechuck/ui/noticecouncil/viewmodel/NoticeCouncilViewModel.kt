package com.example.gaechuck.ui.noticecouncil.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gaechuck.data.response.BaseResponse
import com.example.gaechuck.data.response.GetCouncilNoticeDataResponse
import com.example.gaechuck.data.response.GetCouncilNoticeDetailResponse
import com.example.gaechuck.repository.NoticeCouncilRepository
import kotlinx.coroutines.launch
import okio.IOException

class NoticeCouncilViewModel(
    private val repository: NoticeCouncilRepository
) : ViewModel() {
    private var currentPage = 0
    private val pageSize = 10
    private var isLastPage = false
    private var isLoading = false

    private var searchPage = 0
    private val searchSize = 20
    private val _searchResults = MutableLiveData<List<GetCouncilNoticeDataResponse>>(emptyList())
    val searchResults: LiveData<List<GetCouncilNoticeDataResponse>> = _searchResults

    private val _noticeList = MutableLiveData<List<GetCouncilNoticeDataResponse>>()
    val noticeList: LiveData<List<GetCouncilNoticeDataResponse>> get() = _noticeList

    private val _deleteResult = MutableLiveData<Result<BaseResponse<String>>>()
    val deleteResult: LiveData<Result<BaseResponse<String>>> get() = _deleteResult

    private val _postResult = MutableLiveData<Result<BaseResponse<String>>>()
    val postResult: LiveData<Result<BaseResponse<String>>> get() = _postResult

//    private val _patchResult = MutableLiveData<Result<PatchNoticeResponse>>()
//    val patchResult: LiveData<Result<PatchNoticeResponse>> get() = _patchResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchNotices() {
        currentPage = 0
        isLastPage = false
        _noticeList.value = emptyList()
        loadMoreNotices()
    }


    fun loadMoreNotices() {
        if (isLastPage || isLoading) return

        isLoading = true
        viewModelScope.launch {
            try {
                val resp = repository.getNoticeCouncilList(currentPage, pageSize)
                    ?: throw IOException("Empty response")

                val items = resp.content
                isLastPage = resp.last

                if (items.isNotEmpty()) {
                    _noticeList.value = _noticeList.value.orEmpty() + items
                    currentPage++
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "공지 불러오기 오류"
            } finally {
                isLoading = false
            }
        }
    }

    suspend fun getNoticeDetail(noticeId: Int): GetCouncilNoticeDetailResponse? =
        try {
            repository.getNoticeDetail(noticeId)
        } catch (e: Exception) {
            _errorMessage.postValue("상세 불러오기 실패: ${e.message}")
            null
        }


    fun search(query: String) {
        searchPage = 0
        _searchResults.value = emptyList()
        loadMoreSearch(query)
    }


    fun loadMoreSearch(query: String) {
        viewModelScope.launch {
            try {
                val pageItems = repository.searchNotices(query, searchPage, searchSize)
                if (pageItems.isNotEmpty()) {
                    _searchResults.value = _searchResults.value.orEmpty() + pageItems
                    searchPage++
                }
            } catch (e: IOException) {
                _errorMessage.value = "검색 실패: ${e.message}"
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "알 수 없는 오류"
            }
        }
    }

    fun postNotice(title: String, body: String, imageUris: List<Uri>, context: Context) {
        viewModelScope.launch {
            try {
                val imageParts = repository.createImageParts(imageUris, context)
                val result = repository.postNotice(title, body, imageParts)
                _postResult.value = result
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "작성 중 오류 발생"
            }
        }
    }

    fun deleteNotice(noticeId: Int) {
        viewModelScope.launch {
            val result = repository.deleteNotice(noticeId)
            _deleteResult.value = result

            result.onSuccess {
                Log.d("NoticeCouncilViewModel", "삭제 성공")
            }.onFailure {
                Log.e("NoticeCouncilViewModel", "삭제 실패: ${it.message}")
            }
        }
    }


//    fun patchNotice(noticeId: Int, title: String, body: String, imageUris: List<Uri>, context: Context) {
//        viewModelScope.launch {
//            try {
//                val imageParts = repository.createImageParts(imageUris, context)
//                val result = repository.patchNotice(noticeId, title, body, imageParts)
//                _patchResult.value = result
//            } catch (e: Exception) {
//                _errorMessage.value = e.message ?: "수정 중 오류 발생"
//            }
//        }
//    }


    fun removeNoticeFromList(noticeId: Int) {
        val currentList = _noticeList.value ?: return
        _noticeList.value = currentList.filterNot { it.id == noticeId }
    }

    class Factory(private val repo: NoticeCouncilRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NoticeCouncilViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NoticeCouncilViewModel(repo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

package com.example.gaechuck.repository

import com.example.gaechuck.api.ApiConnection
import com.example.gaechuck.api.AuthManager
import com.example.gaechuck.data.response.BaseResponse
import com.example.gaechuck.data.response.DeleteCouncilNoticeResponse
import com.example.gaechuck.data.response.GetCouncilNoticeDataResponse
import com.example.gaechuck.data.response.GetCouncilNoticeDetailResponse
import com.example.gaechuck.data.response.PagenatedResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException
import retrofit2.Response

// 총학생회 공지 호출 관련 Repo
class NoticeCouncilRepository {
    private val apiService = ApiConnection.getRetrofitService

    // 총학생회 공지 리스트 가져오기
    suspend fun getNoticeCouncilList(
        page: Int,
        size: Int
    ): PagenatedResponse<GetCouncilNoticeDataResponse>? = withContext(Dispatchers.IO) {
        val resp = apiService.getNoticeCouncilList(page, size)
        if (!resp.isSuccess) {
            throw IOException("API error: ${resp.message}")
        }
        // resp.result 가 List<T> 라면 바로 리턴
        resp.result
    }

    suspend fun searchNotices(
        title: String,
        page: Int = 0,
        size: Int = 20
    ): List<GetCouncilNoticeDataResponse> = withContext(Dispatchers.IO) {
        // 이미 suspend로 선언된 Retrofit 호출
        val resp = apiService.getNoticeCouncilSearchList(page, size, title)
        // BaseListResponse<T> 형식이라 .isSuccess/.message/.result 접근 가능
        if (!resp.isSuccess) {
            throw IOException("API Error: ${resp.message}")
        }
        // resp.result 안에 페이징된 content 필드가 있다고 가정
        return@withContext resp.result?.content.orEmpty()
    }

    // 총학생회 공지 상세 내용 가져오기
    suspend fun getNoticeDetail(noticeId: Int): GetCouncilNoticeDetailResponse? = withContext(
        Dispatchers.IO) {
        val response: Response<BaseResponse<GetCouncilNoticeDetailResponse>> = apiService.getNoticeCouncilDetailData(noticeId)

        if (response.isSuccessful && response.body()?.isSuccess == true) {
            return@withContext response.body()!!.result
        } else {
            return@withContext null
        }
    }

    // 총학생회 공지 삭제
    suspend fun deleteNotice(noticeId: Int): Response<DeleteCouncilNoticeResponse> = withContext(
        Dispatchers.IO) {
        val token = AuthManager.getToken() ?: throw IllegalStateException("토큰이 존재하지 않습니다.")
//        ApiConnection.getRetrofitService.deleteNoticeCouncil(noticeId, "Bearer $token")
        apiService.deleteNoticeCouncil(noticeId, "Bearer $token")
    }

}

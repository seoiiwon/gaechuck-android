package com.example.gaechuck.repository

import com.example.gaechuck.api.ApiConnection
import com.example.gaechuck.api.AuthManager
import com.example.gaechuck.data.response.BaseResponse
import com.example.gaechuck.data.response.DeleteCouncilNoticeResponse
import com.example.gaechuck.data.response.GetCouncilNoticeDataResponse
import com.example.gaechuck.data.response.GetCouncilNoticeDetailResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

// 총학생회 공지 호출 관련 Repo
class NoticeCouncilRepository {
    private val apiService = ApiConnection.getRetrofitService

    // 총학생회 공지 리스트 가져오기
    suspend fun getNoticeCouncilList(): List<GetCouncilNoticeDataResponse> = withContext(Dispatchers.IO) {
        val response = withContext(Dispatchers.IO) { apiService.getNoticeCouncilList(0, 20) }

        if (response.isSuccessful) {
            val body = response.body()
            if (body?.isSuccess == true) {
                return@withContext body.result?.content.orEmpty()
            } else {
                throw Exception("API Error : ${body?.message ?: "Unknown Message"}")
            }
        } else {
            throw Exception("HTTP ${response.code()} ${response.message()}")
        }
    }

    suspend fun searchNotices(title: String): List<GetCouncilNoticeDataResponse> = withContext(Dispatchers.IO) {
        val response = apiService.getNoticeCouncilSearchList(0, 20, title)
        if (response.isSuccessful) {
            val body = response.body()
            if (body?.isSuccess == true) {
                return@withContext body.result?.content.orEmpty()
            } else {
                throw Exception("API Error: ${body?.message}")
            }
        } else {
            throw Exception("HTTP ${response.code()} ${response.message()}")
        }
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

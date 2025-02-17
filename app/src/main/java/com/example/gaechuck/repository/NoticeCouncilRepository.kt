package com.example.gaechuck.repository

import android.util.Log
import com.example.gaechuck.api.ApiConnection
import com.example.gaechuck.data.response.BaseResponse
import com.example.gaechuck.data.response.GetCouncilNoticeDataResponse
import com.example.gaechuck.data.response.GetCouncilNoticeDetailResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class NoticeCouncilRepository {
    private val apiService = ApiConnection.getRetrofitService

    // 총학생회 공지 리스트 가져오기
    suspend fun getNoticeCouncilList(): List<GetCouncilNoticeDataResponse>? {
        return try {
            withContext(Dispatchers.IO) {
                val response: Response<BaseResponse<List<GetCouncilNoticeDataResponse>>> = apiService.getNoticeCouncilList()
                val body = response.body()

                if (response.isSuccessful && body?.isSuccess == true) {
                    body.result ?: emptyList()
                } else {
                    throw Exception(body?.message ?: "Unknown API error")
                }
            }
        } catch (e: Exception) {
            throw Exception("Network error: ${e.message}")
        }
    }

    // 총학생회 공지 상세 내용 가져오기
    suspend fun getNoticeDetail(noticeId: Int): GetCouncilNoticeDetailResponse? {
        return try {
            withContext(Dispatchers.IO) {
                Log.d("NoticeDetail", "API 요청 시작: noticeId = $noticeId")
                val response: Response<BaseResponse<GetCouncilNoticeDetailResponse>> = apiService.getNoticeCouncilDetailData(noticeId)

                if (!response.isSuccessful) {
                    Log.e("NoticeDetail", "API 응답 실패: HTTP ${response.code()}")
                    return@withContext null
                }

                val body = response.body()
                Log.d("NoticeDetail", "API 응답 바디: $body")

                if (body?.isSuccess == true) {
                    val result = body.result
                    Log.d("NoticeDetail", "Parsed Result: id=${result?.id}, title=${result?.title}, body=${result?.body}, images=${result?.images}, time=${result?.time}")

                    if (result?.id == 0) {
                        Log.e("NoticeDetail", "⚠️ API에서 id 값이 0으로 반환됨! 백엔드 확인 필요")
                    }

                    result
                } else {
                    Log.e("NoticeDetail", "API 요청 실패: ${body?.message}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("NoticeDetail", "네트워크 오류: ${e.message}")
            null
        }
    }




}

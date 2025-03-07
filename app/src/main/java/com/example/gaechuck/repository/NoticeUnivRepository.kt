package com.example.gaechuck.repository

import android.util.Log
import com.example.gaechuck.api.ApiConnection
import com.example.gaechuck.data.model.NoticeUnivModel
import com.example.gaechuck.data.response.GetAllNoticeDataResponse

class NoticeUnivRepository {
    private val apiService = ApiConnection.getRetrofitService

    // 교내 공지 리스트 가져오기
    suspend fun getNoticeUnivList(page: Int, pageSize: Int, bbsId: String): List<NoticeUnivModel> {
        return try {

            val requestBbsId = if (bbsId.isNullOrEmpty()) "2" else bbsId

            Log.d("API_REQUEST", "Fetching notices with page=$page, pageSize=$pageSize, bbsId=$requestBbsId")

            val result = apiService.getAllNoticeData(page, pageSize, requestBbsId)

            if (result.isSuccess && result.result != null) {
                Log.d("API_SUCCESS", "Response: ${result.result}")

                result.result.map {
                    NoticeUnivModel(
                        id = it.id,
                        title = it.title,
                        body = it.body,
                        representationImages = it.representationImages,
                        time = it.time,
                        departmentName = it.departmentName,
                        bbsId = it.bbsId
                    )
                }

            } else {
                Log.e("API_ERROR", "Error Message: ${result.message}")
                throw Exception(result.message ?: "Unknown error")
            }
        } catch (e: Exception) {
            Log.e("API_EXCEPTION", "Network error: ${e.message}")
            throw Exception("Network error: ${e.message}")
        }
    }

    private fun GetAllNoticeDataResponse.toNoticeUnivModel(): NoticeUnivModel {
        return NoticeUnivModel(
            id = this.id,
            title = this.title,
            body = this.body,
            representationImages = this.representationImages,
            time = this.time,
            departmentName = this.departmentName,
            bbsId = this.bbsId
        )
    }

    // bbsId에 따라 카테고리 이름 변환
    private fun getCategoryByBbsId(bbsId: String): String {
        return when (bbsId) {
            "1" -> "학사"
            "2" -> "기관"
            "3" -> "채용"
            "4" -> "장학"
            "5" -> "입법예고"
            else -> "기타"
        }
    }


}
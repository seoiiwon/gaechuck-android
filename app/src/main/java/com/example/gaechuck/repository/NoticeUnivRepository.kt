package com.example.gaechuck.repository

import android.util.Log
import com.example.gaechuck.api.ApiConnection
import com.example.gaechuck.data.model.NoticeUnivModel
import com.example.gaechuck.data.response.GetAllNoticeDataResponse

class NoticeUnivRepository {
    private val apiService = ApiConnection.getRetrofitService

    // 교내 공지 리스트 가져오기
    suspend fun getNoticeUnivList(page: Int, bbsId: String): Pair<List<NoticeUnivModel>, Boolean> {
        return try {

            val requestBbsId = if (bbsId.isNullOrEmpty()) "2" else bbsId
            val size = 1000

            Log.d("API_REQUEST", "🔹 Fetching notices → page=$page, size=$size, bbsId=$requestBbsId")

            val result = apiService.getAllNoticeData(page, size, requestBbsId)
            if (result.isSuccess && result.result != null) {

                val notices = result.result.map {
                    NoticeUnivModel(
                        notiSeq = it.notiSeq,
                        notiNum = it.notiNum,
                        title = it.title,
                        regiDate = it.regiDate,
                        categoryName = it.categoryName,
                        departmentName = it.departmentName,
                        url = it.url,
                        bbsId = it.bbsId,
                        dataId = it.dataId
                    )
                }

                Log.d("API_SUCCESS", "Page: $page → Fetched: ${notices.size} items")

                val totalDataCount = notices.size
                Log.d("API_SUCCESS", "Total fetched so far: ${totalDataCount} items")

                val hasMoreData = notices.isNotEmpty()
                Pair(notices, hasMoreData)

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
            notiSeq = this.notiSeq,
            notiNum = this.notiNum,
            title = this.title,
            regiDate = this.regiDate,
            categoryName = this.categoryName,
            departmentName = this.departmentName,
            url = this.url,
            bbsId = this.bbsId,
            dataId = this.dataId
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
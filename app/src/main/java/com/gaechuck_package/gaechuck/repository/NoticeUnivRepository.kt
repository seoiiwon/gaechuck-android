package com.gaechuck_package.gaechuck.repository

import android.util.Log
import com.gaechuck_package.gaechuck.api.ApiConnection
import com.gaechuck_package.gaechuck.data.model.NoticeUnivModel
import com.gaechuck_package.gaechuck.data.response.GetAllNoticeDataResponse

class NoticeUnivRepository {
    private val apiService = ApiConnection.getRetrofitService

    // 교내 공지 리스트 가져오기
    @Throws(Exception::class)
    suspend fun getNoticeUnivList(
        page: Int,
        bbsId: String?,
        title: String?=null,
        size: Int = 20
    ): Pair<List<NoticeUnivModel>, Boolean> {
        return try {
            Log.d("API_REQUEST", "Fetching notices → page=$page, size=$size, bbsId=$bbsId, title=$title")

            val paramBbsId = bbsId?.takeIf { it.isNotBlank() }

            val result = apiService.getAllNoticeData(
                page = page,
                size = size,
                bbsId = paramBbsId,
                title = title
            )
            if (result.isSuccess && result.result != null) {
                val contentList = result.result.content

                val notices = contentList.map {
                    it.toNoticeUnivModel()
                }

                val hasMoreData = !result.result.last
                Pair(notices, hasMoreData)
            } else {
                Log.e("API_ERROR", "Error Message: ${result.message}")
                throw Exception(result.message)
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
}
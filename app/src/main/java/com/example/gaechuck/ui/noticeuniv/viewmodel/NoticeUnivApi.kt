package com.example.gaechuck.ui.noticeuniv.viewmodel

import com.example.gaechuck.data.model.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NoticeUnivApi {
    @GET("api/v1/notifications/allNotification")
    suspend fun getNotices(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("bbsId") bbsId: String,
        @Query("title") title: String
    ): retrofit2.Response<ApiResponse>
}
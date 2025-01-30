package com.example.gaechuck.api

import com.example.gaechuck.data.response.BaseResponse
import com.example.gaechuck.data.response.GetFoodDataResponse
import com.example.gaechuck.data.response.GetLoseDataResponse
import com.example.gaechuck.data.response.GetLoseDetailResponse
import com.example.gaechuck.data.response.GetRentDataResponse
import com.example.gaechuck.data.response.GetUnivNoticeDataResponse
import com.example.gaechuck.data.response.GetUnivNoticeDetailResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Lose
    // 분실물 리스트 가져오기
    @GET("/api/v1/lostitems/all")
    fun getLoseData(@Query("page") page : String, @Query("size") size : String)
            : Call<BaseResponse<GetLoseDataResponse>>

    // 분실물 디테일 정보 가져오기
    @GET("/api/v1/lostitems/detail")
    fun getLoseDetailData(@Query("lostItemId") lostItemId : String )
            : Call<BaseResponse<GetLoseDetailResponse>>

    // Rent
    // 대여 리스트 가져오기
    @GET("/api/v1/rent/list")
    fun getRentData(@Query("page") page : String, @Query("size") size : String)
            : Call<BaseResponse<GetRentDataResponse>>

    // Notice
    // 총학생회 공지 리스트
    @GET("/api/v1/council/show")
    fun getUnivNoticeData()
            : Call<BaseResponse<GetUnivNoticeDataResponse>>

    // 총학생회 공지 상세보기
    @GET("/api/v1/council/show/{id}")
    fun getUnivNoticeDetailData(@Path("id") id : Int)
            : Call<BaseResponse<GetUnivNoticeDetailResponse>>

    // Food
    // 식당 메뉴 보기
    @GET("/api/v1/menus/weeklyMenu")
    fun getFoodData(@Query("cafeteriaSeq") seq : Int, @Query("startDate") date : String)
            : Call<BaseResponse<GetFoodDataResponse>>

    // Admin

}
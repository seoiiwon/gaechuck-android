package com.example.gaechuck.api

import com.example.gaechuck.data.request.LoginRequest
import com.example.gaechuck.data.response.BaseListResponse
import com.example.gaechuck.data.response.BaseResponse
import com.example.gaechuck.data.response.GetAllNoticeDataResponse
import com.example.gaechuck.data.response.GetBusinessDataResponse
import com.example.gaechuck.data.response.GetBusinessDetailResponse
import com.example.gaechuck.data.response.GetCouncilNoticeDataResponse
import com.example.gaechuck.data.response.GetCouncilNoticeDetailResponse
import com.example.gaechuck.data.response.GetFoodDataResponse
import com.example.gaechuck.data.response.GetLoseDataResponse
import com.example.gaechuck.data.response.GetLoseDetailResponse
import com.example.gaechuck.data.response.GetRentDataResponse
import com.example.gaechuck.data.response.GetRentDetailResponse
import com.example.gaechuck.data.response.LoginResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Lose
    // 분실물 리스트 가져오기
    @GET("/api/v1/lostitems/all")
    suspend fun getLoseData()
            : Response<BaseResponse<GetLoseDataResponse>>

    // 분실물 디테일 정보 가져오기
    @GET("/api/v1/lostitems/detail")
    suspend fun getLoseDetailData(@Query("lostItemId") lostItemId : Int )
            : Response<BaseResponse<GetLoseDetailResponse>>

    // Rent
    // 대여 리스트 가져오기
    @GET("/api/v1/rent/list")
    suspend fun getRentData()
            : Response<BaseResponse<GetRentDataResponse>>

    // 대여 디테일 정보 가져오기
    @GET("/api/v1/rent/detailItem")
    suspend fun getRentDetailData(@Query("rentItemId") rentItemId : Int)
            : Response<BaseResponse<GetRentDetailResponse>>

    // Business
    // 제휴 리스트 가져오기
    @GET("/api/v1/coalition/all")
    suspend fun getBusinessData()
            :Response<BaseResponse<GetBusinessDataResponse>>

    // 제휴 디테일 정보 가져오기
    @GET("/api/v1/coalition/detail")
    suspend fun getBusinessDetailData(@Query("coalitionId") coalitionId: Int)
            : Response<BaseResponse<GetBusinessDetailResponse>>

    // Notice
    // 총학생회 공지 리스트
    @GET("/api/v1/council/show")
    suspend fun getNoticeCouncilList()
            : Response<BaseResponse<List<GetCouncilNoticeDataResponse>>>

    // 총학생회 공지 상세보기
    @GET("/api/v1/council/show/{id}")
    suspend fun getNoticeCouncilDetailData(@Path("id") id : Int)
            : Response<BaseResponse<GetCouncilNoticeDetailResponse>>

    // 학교 공지 리스트
    @GET("/api/v1/notifications/allNotification")
    fun getAllNoticeData()
            : Call<BaseResponse<GetAllNoticeDataResponse>>

    // Food
    // 식당 메뉴 보기
    @GET("/api/v1/menus/weeklyMenu")
    fun getFoodData(@Query("cafeteriaSeq") seq : Int, @Query("startDate") date : String)
            : Call<BaseListResponse<GetFoodDataResponse>>

    // Admin
    @POST("/api/v1/master/sign-in")
    suspend fun login(@Body request: LoginRequest)
            : Response<BaseResponse<LoginResponse>>
}
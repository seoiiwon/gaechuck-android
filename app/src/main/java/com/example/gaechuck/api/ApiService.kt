package com.example.gaechuck.api

import android.media.session.MediaSession.Token
import com.example.gaechuck.data.request.BusinessDeleteRequest
import com.example.gaechuck.data.request.BusinessPatchRequest
import com.example.gaechuck.data.request.LoginRequest
import com.example.gaechuck.data.request.LoseDeleteRequest
import com.example.gaechuck.data.request.LosePatchRequest
import com.example.gaechuck.data.request.RefreshRequest
import com.example.gaechuck.data.request.RentDeleteRequest
import com.example.gaechuck.data.request.RentPatchRequest
import com.example.gaechuck.data.request.UrlChangeRequest
import com.example.gaechuck.data.response.BaseListResponse
import com.example.gaechuck.data.response.BaseResponse
import com.example.gaechuck.data.response.DeleteCouncilNoticeResponse
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
import com.example.gaechuck.data.response.PatchBusinessResponse
import com.example.gaechuck.data.response.PatchLoseResponse
import com.example.gaechuck.data.response.PatchNoticeResponse
import com.example.gaechuck.data.response.PostRentCreateResponse
import com.example.gaechuck.data.response.PostUrlResponse
//import com.example.gaechuck.data.response.PostUrlResponse
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Lose
    // 분실물 리스트 가져오기
    @GET("/api/v1/lostitems/all")
    suspend fun getLoseData(
        @Query("page") page : Int,
        @Query("size") size : Int = 9,
    )
            : Response<BaseResponse<GetLoseDataResponse>>

    // 분실물 디테일 정보 가져오기
    @GET("/api/v1/lostitems/detail")
    suspend fun getLoseDetailData(@Query("lostItemId") lostItemId : Int )
            : Response<BaseResponse<GetLoseDetailResponse>>

    // 분실물 글 작성하기
    @Multipart
    @POST("/api/v1/lostitems/write")
    suspend fun postLoseCreate(
        @Header("Authorization") Authorization: String,
        @Part("data") data: RequestBody, // "data" 파트 추가
        @Part file: List<MultipartBody.Part> // "file" 파트 추가
    ) : Response<BaseResponse<String>>

    // 분실물 글 삭제하기
    @POST("/api/v1/lostitems/delete")
    suspend fun postLoseDelete(
        @Header("Authorization") Authorization: String,
        @Body request : LoseDeleteRequest
    ) : Response<BaseResponse<String>>

    // 분실물 글 수정하기
    @Multipart
    @PATCH("/api/v1/lostitems/update")
    suspend fun patchLoseData(
        @Header("Authorization") Authorization: String,
        @Part("data") data: LosePatchRequest,
        @Part file: List<MultipartBody.Part>
    ) : Response<BaseResponse<PatchLoseResponse>>


    // Rent
    // 대여 리스트 가져오기
    @GET("/api/v1/rent/list")
    suspend fun getRentData(
        @Query("page") page : Int = 0,
        @Query("size") size : Int = 10,
        @Query("rentItemName") rentItemId: String? = null
    )
            : Response<BaseResponse<GetRentDataResponse>>

    // 대여 디테일 정보 가져오기
    @GET("/api/v1/rent/detailItem")
    suspend fun getRentDetailData(@Query("rentItemId") rentItemId : Int)
            : Response<BaseResponse<GetRentDetailResponse>>

    // 대여 글 작성하기
    @Multipart
    @POST("/api/v1/rent/createItem")
    suspend fun postRentCreate(
        @Header("Authorization") Authorization: String,
        @Part("data") data: RequestBody, // "data" 파트 추가
        @Part file: List<MultipartBody.Part> // "file" 파트 추가
    ) : Response<BaseResponse<String>>

    // 대여 글 삭제하기
    @POST("/api/v1/rent/deleteItem")
    suspend fun postRentDelete(
        @Header("Authorization") Authorization: String,
        @Body request : RentDeleteRequest
    ) : Response<BaseResponse<String>>

    // 대여 글 수정하기
    @Multipart
    @PATCH("/api/v1/rent/updateItem")
    suspend fun patchRentData(
        @Header("Authorization") Authorization: String,
        @Part("data") data: RentPatchRequest,
        @Part file: List<MultipartBody.Part>
    ) : Response<BaseResponse<String>>

    // Business
    // 제휴 리스트 가져오기
    @GET("/api/v1/coalition/all")
    suspend fun getBusinessData(
        @Query("page") page : Int,
        @Query("size") size : Int = 9,
        @Query("category") category : String ?= null,
    )
            :Response<BaseResponse<GetBusinessDataResponse>>

    // 제휴 디테일 정보 가져오기
    @GET("/api/v1/coalition/detail")
    suspend fun getBusinessDetailData(@Query("coalitionId") coalitionId: Int)
            : Response<BaseResponse<GetBusinessDetailResponse>>

    // 제휴 글 작성하기
    @Multipart
    @POST("/api/v1/coalition/write")
    suspend fun postBusinessCreate(
        @Header("Authorization") Authorization: String,
        @Part("data") data: RequestBody, // "data" 파트 추가
        @Part file: List<MultipartBody.Part> // "file" 파트 추가
    ) : Response<BaseResponse<String>>

    // 제휴 글 삭제하기
    @POST("/api/v1/coalition/delete")
    suspend fun postBusinessDelete(
        @Header("Authorization") Authorization: String,
        @Body request : BusinessDeleteRequest
    ) : Response<BaseResponse<String>>

    // 제휴 글 수정하기
    @Multipart
    @PATCH("/api/v1/coalition/update")
    suspend fun patchBusinessData(
        @Header("Authorization") Authorization: String,
        @Part("data") data: BusinessPatchRequest,
        @Part file: List<MultipartBody.Part>
    ) : Response<BaseResponse<PatchBusinessResponse>>

    // Notice
    // 총학생회 공지 리스트
    @GET("/api/v1/council/show")
    suspend fun getNoticeCouncilList()
            : Response<BaseResponse<List<GetCouncilNoticeDataResponse>>>

    // 총학생회 공지 상세보기
    @GET("/api/v1/council/show/{id}")
    suspend fun getNoticeCouncilDetailData(@Path("id") id : Int)
            : Response<BaseResponse<GetCouncilNoticeDetailResponse>>

    // 총학생회 공지 작성
    @Multipart
    @POST("api/v1/council/post")
    suspend fun postNoticeCouncil(
        @Header("Authorization") authToken: String,
        @Part("data") data: RequestBody,
        @Part file: List<MultipartBody.Part>
    ): Response<BaseResponse<String>>

    @Multipart
    @PATCH("api/v1/council/{id}")
    suspend fun updateNoticeCouncil(
        @Path("id") noticeId: Int,
        @Header("Authorization") token: String,
//        @PartMap requestBody: Map<String, @JvmSuppressWildcards RequestBody>,
        @Body requestBody : RequestBody,
        @Part images: List<MultipartBody.Part>
    ): Response<PatchNoticeResponse>


    @DELETE("api/v1/council/{id}")
    suspend fun deleteNoticeCouncil(
        @Path("id") noticeId: Int,
        @Header("Authorization") token: String
    ): Response<DeleteCouncilNoticeResponse>

    // 학교 공지 리스트
    @GET("/api/v1/notifications/allNotification")
    suspend fun getAllNoticeData(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("bbsId") bbsId: String? = null
    ): BaseResponse<List<GetAllNoticeDataResponse>>

    // Food
    // 식당 메뉴 보기
    @GET("/api/v1/menus/weeklyMenu")
    fun getFoodData(
        @Query("cafeteriaSeq") seq : Int,
        @Query("startDate") date : String
    ): Call<BaseListResponse<GetFoodDataResponse>>


    // Url
    // URL 수정 및 
    @POST("/api/v1/chaturl/insertUrl")
    suspend fun postUrl(
        @Header("Authorization") authToken: String,
        @Body request: UrlChangeRequest
        ) : Response<BaseResponse<PostUrlResponse>>

    // URL 불러오기
    @GET("/api/v1/chaturl/url")
    suspend fun getUrl(
        @Query("chatName") chatName: String,
    ) : Response<BaseResponse<PostUrlResponse>>

    // Admin
    @POST("/api/v1/master/sign-in")
    suspend fun login(@Body request: LoginRequest)
            : Response<BaseResponse<LoginResponse>>

    // Refresh
    @POST("/api/v1/master/token/reissue")
    suspend fun refresh(
        @Body request : RefreshRequest
    ) : Response<BaseResponse<LoginResponse>>

}
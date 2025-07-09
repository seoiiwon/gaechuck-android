package com.gaechuck_package.gaechuck.repository

import android.content.Context
import android.net.Uri
import com.gaechuck_package.gaechuck.api.ApiConnection
import com.gaechuck_package.gaechuck.api.AuthManager
import com.gaechuck_package.gaechuck.data.request.NoticeCouncilRequest
import com.gaechuck_package.gaechuck.data.response.BaseResponse
import com.gaechuck_package.gaechuck.data.response.GetCouncilNoticeDataResponse
import com.gaechuck_package.gaechuck.data.response.GetCouncilNoticeDetailResponse
import com.gaechuck_package.gaechuck.data.response.PagenatedResponse
import com.gaechuck_package.gaechuck.data.response.PatchNoticeResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

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
        resp.result
    }

    // 공지 검색
    suspend fun searchNotices(
        title: String,
        page: Int = 0,
        size: Int = 20
    ): List<GetCouncilNoticeDataResponse> = withContext(Dispatchers.IO) {
        val resp = apiService.getNoticeCouncilSearchList(page, size, title)
        if (!resp.isSuccess) {
            throw IOException("API Error: ${resp.message}")
        }
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

    // 공지 작성
    suspend fun postNotice(
        title: String,
        body: String,
        images: List<MultipartBody.Part>
    ): Result<BaseResponse<String>> = withContext(Dispatchers.IO) {
        try {
            val request = NoticeCouncilRequest(title, body)
            val json = Gson().toJson(request)
            val dataBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())
            val response = apiService.postNoticeCouncil( dataBody, images)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("공지 작성 실패: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // 공지 수정
    suspend fun patchNotice(
        noticeId: Int,
        title: String,
        body: String,
        images: List<MultipartBody.Part>
    ): Result<PatchNoticeResponse> = withContext(Dispatchers.IO) {
        try {
            val request = NoticeCouncilRequest(title, body)
            val json = Gson().toJson(request)
            val dataBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())
            val response = apiService.updateNoticeCouncil(noticeId, dataBody, images)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("공지 수정 실패: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    suspend fun deleteNotice(noticeId: Int): Result<BaseResponse<String>> {
        return try {
            val token = AuthManager.getToken()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("사용자 토큰이 존재하지 않습니다."))
            }

            val response = ApiConnection.getRetrofitService.deleteNoticeCouncil(noticeId)

            if (response.isSuccessful && response.body()?.isSuccess == true) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = response.body()?.message ?: "알 수 없는 서버 오류"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("삭제 실패: ${e.message}"))
        }
    }


    //
    suspend fun createImageParts(uris: List<Uri>, context: Context): List<MultipartBody.Part> = withContext(Dispatchers.IO) {
        uris.mapIndexedNotNull { index, uri ->
            try {
                val inputStream = context.contentResolver.openInputStream(uri) ?: return@mapIndexedNotNull null
                val file = File(context.cacheDir, "notice_image_$index.jpg")
                FileOutputStream(file).use { inputStream.copyTo(it) }
                val requestFile = RequestBody.create("image/*".toMediaType(), file)
                MultipartBody.Part.createFormData("file", file.name, requestFile)
            } catch (e: Exception) {
                null
            }
        }
    }
}

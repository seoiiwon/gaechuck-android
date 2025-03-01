package com.example.gaechuck.repository

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.example.gaechuck.api.ApiConnection
import com.example.gaechuck.data.request.LoseDeleteRequest
import com.example.gaechuck.data.response.BaseResponse
import com.example.gaechuck.data.response.GetLoseDataResponse
import com.example.gaechuck.data.response.GetLoseDetailResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class LoseRepository {
    private val apiService = ApiConnection.getRetrofitService

    // 분실물 리스트 가져오기
    suspend fun getLoseData(page : Int, size: Int = 9): GetLoseDataResponse? {
        return try {
            val response = apiService.getLoseData(page,size)
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                response.body()?.result
            } else {
                throw Exception(response.body()?.message ?: "Unknown error")
            }
        } catch (e: Exception) {
            throw Exception("Network error: ${e.message}")
        }
    }

    // 분실물 상세 내용 가져오기
    suspend fun getLoseDetailData(lostItemId: Int): GetLoseDetailResponse? {
        return try {
            val response = apiService.getLoseDetailData(lostItemId)
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                response.body()?.result
            } else {
                throw Exception(response.body()?.message ?: "Unknown error")
            }
        } catch (e: Exception) {
            throw Exception("Network error: ${e.message}")
        }
    }

    // 제휴 글쓰기
    suspend fun postLoseCreate(
        token: String,
        title : String,
        lostDate : String,
        description : String,
        lostLocation : String,
        file : List<Uri>,
        contentResolver : ContentResolver
    ) : Result<BaseResponse<String>> {
        return try {
            // ✅ "data" 파트 JSON 변환
            val jsonData = JSONObject().apply {
                put("title", title)
                put("lostDate", lostDate)
                put("description", description)
                put("lostLocation", lostLocation)
            }.toString()

            val dataRequestBody = jsonData.toRequestBody("application/json".toMediaTypeOrNull())

            // ✅ 이미지 리스트 -> Multipart 변환
            val imageParts = file.mapNotNull { uri ->
                uriToMultipart(uri, contentResolver)
            }

            Log.d("LoseRepository", "데이터 전송 시작: name: $title, lostDate: $lostDate, description: $description, lostLocation:$lostLocation, data=$dataRequestBody")

            val response =  apiService.postLoseCreate(
                Authorization = token,
                data = dataRequestBody, // JSON 형식으로 보냄
                file = imageParts
            )

            if (response.isSuccessful && response.body()?.isSuccess == true) {
                Log.d("LoseRepository", "서버 응답 성공: ${response.body()}")
                Result.success(response.body()!!)
            } else {
                Log.e("LoseRepository", "서버 응답 실패: ${response.errorBody()?.string()}")
                Result.failure(Exception(response.body()?.message ?: "Unknown error"))
            }

        } catch(e: Exception) {
            Log.e("LoseRepository", "네트워크 오류 발생: ${e.message}")
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    private fun uriToMultipart(uri: Uri, contentResolver: ContentResolver): MultipartBody.Part? {
        val inputStream = contentResolver.openInputStream(uri) ?: return null

        // 파일 이름 추출
        var fileName: String? = null
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    fileName = cursor.getString(displayNameIndex)
                }
            }
        }
        val mimeType = contentResolver.getType(uri) ?: "image/*"
        val requestBody = inputStream.readBytes().toRequestBody(mimeType.toMediaTypeOrNull())

        return MultipartBody.Part.createFormData("file", fileName ?: "image.jpg", requestBody)
    }

    // 분실물 글 삭제
    suspend fun postLoseDelete (
        token: String,
        lostItemId : Int,
    ) : Result<BaseResponse<String>>{
        return try {
            val response = apiService.postLoseDelete(
                Authorization = "Bearer $token",
                request = LoseDeleteRequest(lostItemId),
            )

            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("삭제 실패: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 싱글톤 패턴 적용
    companion object {
        @Volatile
        private var instance: LoseRepository? = null

        fun getInstance(): LoseRepository {
            return instance ?: synchronized(this) {
                instance ?: LoseRepository().also { instance = it }
            }
        }
    }
}
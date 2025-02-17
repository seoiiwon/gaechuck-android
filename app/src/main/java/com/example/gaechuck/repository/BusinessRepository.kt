package com.example.gaechuck.repository

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.example.gaechuck.api.ApiConnection
import com.example.gaechuck.data.response.BaseResponse
import com.example.gaechuck.data.response.GetBusinessDataResponse
import com.example.gaechuck.data.response.GetBusinessDetailResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class BusinessRepository {
    private val apiService = ApiConnection.getRetrofitService


    // 제휴 리스트 가져오기
    suspend fun getBusinessData() : GetBusinessDataResponse? {
        return try {
            val response = apiService.getBusinessData()
            if(response.isSuccessful && response.body()?.isSuccess == true) {
                response.body()?.result
            } else {
                throw Exception(response.body()?.message ?: "Unkown error")
            }
        }catch (e : Exception) {
            throw Exception("Network error: ${e.message}")
        }
    }

    // 제휴 상세 내용 가져오기
    suspend fun getBusinessDetailData(coalitionId : Int) : GetBusinessDetailResponse? {
        return try {
            val response = apiService.getBusinessDetailData(coalitionId)
            if(response.isSuccessful && response.body()?.isSuccess == true) {
                response.body()?.result
            } else {
                throw Exception(response.body()?.message ?: "Unkown error")
            }
        }catch (e : Exception) {
            throw Exception("Network error: ${e.message}")
        }
    }

    // TODO : 제휴 글쓰기 new (POST) -
    suspend fun postBusinessCreate(
        token: String,
        coalitionName : String,
        benefit : String,
        category : String,
        file : List<Uri>,
        contentResolver : ContentResolver
        ) : Result<BaseResponse<String>> {
        return try {
            // ✅ "data" 파트 JSON 변환
            val jsonData = JSONObject().apply {
                put("coalitionName", coalitionName)
                put("benefit", benefit)
                put("category", category)
            }.toString()

            val dataRequestBody = jsonData.toRequestBody("application/json".toMediaTypeOrNull())

            // ✅ 이미지 리스트 -> Multipart 변환
            val imageParts = file.mapNotNull { uri ->
                uriToMultipart(uri, contentResolver)
            }

            Log.d("BusinessRepository", "데이터 전송 시작: name=$coalitionName, benefit=$benefit, category=$category, data=$dataRequestBody")

            val response =  apiService.postBusinessCreate(
                Authorization = token,
                data = dataRequestBody, // JSON 형식으로 보냄
                file = imageParts
            )

            if (response.isSuccessful && response.body()?.isSuccess == true) {
                Log.d("BusinessRepository", "서버 응답 성공: ${response.body()}")
                Result.success(response.body()!!)
            } else {
                Log.e("BusinessRepository", "서버 응답 실패: ${response.errorBody()?.string()}")
                Result.failure(Exception(response.body()?.message ?: "Unknown error"))
            }

        } catch(e: Exception) {
            Log.e("BusinessRepository", "네트워크 오류 발생: ${e.message}")
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

    // 싱글톤 패턴 적용
    companion object {
        @Volatile
        private var instance: BusinessRepository? = null

        fun getInstance(): BusinessRepository {
            return instance ?: synchronized(this) {
                instance ?: BusinessRepository().also { instance = it }
            }
        }
    }
}
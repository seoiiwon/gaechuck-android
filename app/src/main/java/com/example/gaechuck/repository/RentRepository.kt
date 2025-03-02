package com.example.gaechuck.repository

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.example.gaechuck.api.ApiConnection
import com.example.gaechuck.data.request.RentDeleteRequest
import com.example.gaechuck.data.response.BaseResponse
import com.example.gaechuck.data.response.GetRentDataResponse
import com.example.gaechuck.data.response.GetRentDetailResponse
import com.example.gaechuck.data.response.PostRentCreateResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class RentRepository {
    private val apiService = ApiConnection.getRetrofitService

    // 대여 리스트 가져오기
    suspend fun getRentList(page:Int): GetRentDataResponse? {
        return try {
            val response = apiService.getRentData(page)
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                response.body()?.result
            } else {
                throw Exception(response.body()?.message ?: "Unknown error")
            }
        } catch (e: Exception) {
            throw Exception("Network error: ${e.message}")
        }
    }

    // 대여 상세 정보 가져오기
    suspend fun getRentDetailData(rentItemId : Int) : GetRentDetailResponse? {
        return try {
            val response = apiService.getRentDetailData(rentItemId)
            if(response.isSuccessful && response.body()?.isSuccess == true){
                response.body()?.result
            } else {
                throw Exception(response.body()?.message ?: "Unknown error")
            }
        } catch (e:Exception) {
            throw Exception("Network error: ${e.message}")
        }

    }

    // 대여 글쓰기
    suspend fun postRentCreate(
        token: String,
        rentItemName : String,
        rentItemCount : Int,
        file : List<Uri>,
        contentResolver : ContentResolver
    ) : Result<BaseResponse<PostRentCreateResponse>> {
        return try {
            // ✅ "data" 파트 JSON 변환
            val jsonData = JSONObject().apply {
                put("rentItemName", rentItemName)
                put("rentItemCount", rentItemCount)
            }.toString()

            val dataRequestBody = jsonData.toRequestBody("application/json".toMediaTypeOrNull())

            // ✅ 이미지 리스트 -> Multipart 변환
            val imageParts = file.mapNotNull { uri ->
                uriToMultipart(uri, contentResolver)
            }

            Log.d("RentRepository", "데이터 전송 시작: name=$rentItemName, rentItemCount=$rentItemCount, data=$dataRequestBody")

            val response =  apiService.postRentCreate(
                Authorization = token,
                data = dataRequestBody, // JSON 형식으로 보냄
                file = imageParts
            )

            if (response.isSuccessful && response.body()?.isSuccess == true) {
                Log.d("RentRepository", "서버 응답 성공: ${response.body()}")
                Result.success(response.body()!!)
            } else {
                Log.e("RentRepository", "서버 응답 실패: ${response.errorBody()?.string()}")
                Result.failure(Exception(response.body()?.message ?: "Unknown error"))
            }

        } catch(e: Exception) {
            Log.e("RentRepository", "네트워크 오류 발생: ${e.message}")
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

    // 대여 글 삭제하기
    suspend fun postRentDelete (
        token: String,
        rentItemId : Int,
    ) : Result<BaseResponse<String>>{
        return try {
            val request = RentDeleteRequest(rentItemId)

            val response = ApiConnection.getRetrofitService.postRentDelete(
                Authorization = token,  // Authorization 헤더에 token 추가
                request = request
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
        private var instance: RentRepository? = null

        fun getInstance(): RentRepository {
            return instance ?: synchronized(this) {
                instance ?: RentRepository().also { instance = it }
            }
        }
    }
}
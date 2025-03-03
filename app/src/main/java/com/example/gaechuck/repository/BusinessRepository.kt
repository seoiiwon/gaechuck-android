package com.example.gaechuck.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.gaechuck.api.ApiConnection
import com.example.gaechuck.data.request.BusinessCreateRequest
import com.example.gaechuck.data.request.BusinessDeleteRequest
import com.example.gaechuck.data.response.BaseResponse
import com.example.gaechuck.data.response.GetBusinessDataResponse
import com.example.gaechuck.data.response.GetBusinessDetailResponse
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class BusinessRepository {
    private val apiService = ApiConnection.getRetrofitService


    // 제휴 리스트 가져오기
    suspend fun getBusinessData(page: Int, category: String? = null) : GetBusinessDataResponse? {
        return try {
            val categoryToUse = category ?: ""
            val response = apiService.getBusinessData(page, 9, categoryToUse)
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

    // 제휴 글쓰기
    suspend fun postBusinessCreate(
        token: String,
        coalitionName : String,
        benefit : String,
        category : String,
        file : List<Uri>,
        context: Context
        ) : Result<BaseResponse<String>> {
        return try {
            val requestBody = createJsonRequestBody(BusinessCreateRequest(coalitionName, benefit, category))
            val imagePart = createImagePart(file.firstOrNull(), context)

            Log.d("BusinessRepository", "데이터 전송 시작: name=$coalitionName, benefit=$benefit, category=$category, data=$requestBody")

            val response =  ApiConnection.getRetrofitService.postBusinessCreate(
                Authorization = token,
                data = requestBody, // JSON 형식으로 보냄
                file = imagePart
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

    private fun createJsonRequestBody(request: BusinessCreateRequest): RequestBody {
        val json = Gson().toJson(request)
        return RequestBody.create("application/json".toMediaType(), json)
    }

    private fun createImagePart(uri: Uri?, context: Context): MultipartBody.Part? {
        uri ?: return null

        val contentResolver: ContentResolver = context.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "upload_image.jpg") // 임시 파일 생성

        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        val requestFile = RequestBody.create("image/*".toMediaType(), file)
        return MultipartBody.Part.createFormData("file", file.name, requestFile)
    }

    // 제휴 글 삭제
    suspend fun postBusinessDelete (
        token: String,
        coalitionId : Int,
    ) : Result<BaseResponse<String>>{
        return try {
            val request = BusinessDeleteRequest(coalitionId)

            val response = ApiConnection.getRetrofitService.postBusinessDelete(
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
        private var instance: BusinessRepository? = null

        fun getInstance(): BusinessRepository {
            return instance ?: synchronized(this) {
                instance ?: BusinessRepository().also { instance = it }
            }
        }
    }
}
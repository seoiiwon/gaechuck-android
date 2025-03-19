package com.example.gaechuck.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.gaechuck.api.ApiConnection
import com.example.gaechuck.data.request.LoseCreateRequest
import com.example.gaechuck.data.request.LoseDeleteRequest
import com.example.gaechuck.data.request.LosePatchRequest
import com.example.gaechuck.data.request.RentPatchRequest
import com.example.gaechuck.data.response.BaseResponse
import com.example.gaechuck.data.response.GetLoseDataResponse
import com.example.gaechuck.data.response.GetLoseDetailResponse
import com.example.gaechuck.data.response.PatchLoseResponse
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

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

    // 분실물 글쓰기
    suspend fun postLoseCreate(
        token: String,
        title : String,
        lostDate : String,
        description : String,
        lostLocation : String,
        file : List<Uri>,
        context : Context
    ) : Result<BaseResponse<String>> {
        return try {
            val requestBody = createJsonRequestBody(LoseCreateRequest(title, lostDate,description, lostLocation))
            val imageParts = file.mapIndexedNotNull { index, uri ->  createImagePart(uri, context, index) } // 모든 이미지 변환

            Log.d("LoseRepository", "데이터 전송 시작: name: $title, lostDate: $lostDate, description: $description, lostLocation:$lostLocation, data=$requestBody")

            val response =  ApiConnection.getRetrofitService.postLoseCreate(
                Authorization = token,
                data = requestBody, // JSON 형식으로 보냄
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

    private fun createJsonRequestBody(request: LoseCreateRequest): RequestBody {
        val json = Gson().toJson(request)
        return RequestBody.create("application/json".toMediaType(), json)
    }

    private fun createImagePart(uri: Uri?, context: Context, index: Int): MultipartBody.Part? {
        uri ?: return null

        return when{
            uri.toString().startsWith("http") -> {
                try {
                    // 1. URL에서 이미지 다운로드
                    val url = URL(uri.toString())
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.doInput = true
                    connection.connect()


                    // 2. 다운로드한 이미지 데이터를 임시 파일에 저장
                    val file = File(context.cacheDir, "upload_image_$index.jpg")
                    connection.inputStream.use { input ->
                        FileOutputStream(file).use { output ->
                            input.copyTo(output)
                        }
                    }

                    // 3. MultiPart 변환
                    val requestFile = RequestBody.create("image/*".toMediaType(), file)
                    MultipartBody.Part.createFormData("file", file.name, requestFile)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
            uri.toString().startsWith("content") -> {
                val contentResolver: ContentResolver = context.contentResolver
                val inputStream: InputStream? = contentResolver.openInputStream(uri)
                val file = File(context.cacheDir, "upload_image_$index.jpg") // 임시 파일 생성

                inputStream?.use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }

                val requestFile = RequestBody.create("image/*".toMediaType(), file)
                return MultipartBody.Part.createFormData("file", file.name, requestFile)
            }

            else -> null
        }
    }


    // 분실물 글 삭제
    suspend fun postLoseDelete (
        token: String,
        lostItemId : Int,
    ) : Result<BaseResponse<String>>{
        Log.d("LoseRepository", token)
        Log.d("LoseRepository", lostItemId.toString())
        return try {

            val request = LoseDeleteRequest(lostItemId)

            val response = ApiConnection.getRetrofitService.postLoseDelete(
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

    // 분실물 글 수정하기
    suspend fun patchLoseData(
        token: String,
        lostItemId : Int,
        title : String,
        lostDate: String,
        description : String,
        lostLocation : String,
        file : List<Uri>,
        context: Context
    ) : Result<BaseResponse<PatchLoseResponse>> {
        return try {
            val requestBody = LosePatchRequest(lostItemId, title, lostDate, description, lostLocation)
            val imageParts = file.mapIndexedNotNull { index, uri ->  createImagePart(uri, context, index) } // 모든 이미지 변환

            val response =  ApiConnection.getRetrofitService.patchLoseData(
                Authorization = token,
                data = requestBody, // JSON 형식으로 보냄
                file = imageParts
            )

            if (response.isSuccessful && response.body()?.isSuccess == true) {
                Log.d("RentRepository", "서버 응답 성공: ${response.body()}")
                Log.d("RentRepository","${imageParts}")
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
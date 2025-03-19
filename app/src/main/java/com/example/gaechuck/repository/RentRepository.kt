package com.example.gaechuck.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.gaechuck.api.ApiConnection
import com.example.gaechuck.data.request.RentPatchRequest
import com.example.gaechuck.data.request.RentCreateRequest
import com.example.gaechuck.data.request.RentDeleteRequest
import com.example.gaechuck.data.response.BaseResponse
import com.example.gaechuck.data.response.GetRentDataResponse
import com.example.gaechuck.data.response.GetRentDetailResponse
import com.example.gaechuck.data.response.PostRentCreateResponse
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class RentRepository {
    private val apiService = ApiConnection.getRetrofitService

    // 대여 리스트 가져오기
    suspend fun getRentList(page:Int, rentItemName: String): GetRentDataResponse? {
        return try {
            val response = apiService.getRentData(page, 9, rentItemName)
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
        context: Context
    ) : Result<BaseResponse<String>> {
        return try {
            val requestBody = createJsonRequestBody(RentCreateRequest(rentItemName, rentItemCount))
            val imageParts = file.mapIndexedNotNull { index, uri ->  createImagePart(uri, context, index) } // 모든 이미지 변환

            Log.d("RentRepository", "데이터 전송 시작: name=$rentItemName, rentItemCount=$rentItemCount, data=$requestBody")

            val response =  ApiConnection.getRetrofitService.postRentCreate(
                Authorization = token,
                data = requestBody, // JSON 형식으로 보냄
                file = imageParts
            )

            if (response.isSuccessful && response.body()?.isSuccess == true) {
                Log.d("RentRepository", "서버 응답 성공: ${response.body()}")
                Log.d("RentRepository","${imageParts}")
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

    private fun createJsonRequestBody(request: RentCreateRequest): RequestBody {
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

    // 대여 글 수정하기
    suspend fun patchRentData(
        token: String,
        rentItemId : Int,
        rentItemName : String,
        rentItemCount : Int,
        file : List<Uri>,
        context: Context
    ) : Result<BaseResponse<String>> {
        return try {
            val requestBody = RentPatchRequest(rentItemId, rentItemCount, rentItemName)
            val imageParts = file.mapIndexedNotNull { index, uri ->  createImagePart(uri, context, index) } // 모든 이미지 변환

            val response =  ApiConnection.getRetrofitService.patchRentData(
                Authorization = token,
                data = requestBody, // JSON 형식으로 보냄
                file = imageParts
            )

            if (response.isSuccessful && response.body()?.isSuccess == true) {
                Log.d("RentRepository", "서버 응답 성공: ${response.body()}")
                Log.d("RentRepository","${imageParts}")
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
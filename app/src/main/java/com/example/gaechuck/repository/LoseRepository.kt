package com.example.gaechuck.repository

import com.example.gaechuck.api.ApiConnection
import com.example.gaechuck.data.response.GetLoseDataResponse
import com.example.gaechuck.data.response.GetLoseDetailResponse

class LoseRepository {
    private val apiService = ApiConnection.getRetrofitService

    // 분실물 리스트 가져오기
    suspend fun getLoseData(): GetLoseDataResponse? {
        return try {
            val response = apiService.getLoseData()
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
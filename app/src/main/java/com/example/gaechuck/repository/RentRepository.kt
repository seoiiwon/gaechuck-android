package com.example.gaechuck.repository

import com.example.gaechuck.api.ApiConnection
import com.example.gaechuck.data.response.GetRentDataResponse

class RentRepository {
    private val apiService = ApiConnection.getRetrofitService

    // 대여 리스트 가져오기
    suspend fun getRentList(): GetRentDataResponse? {
        return try {
            val response = apiService.getRentData()
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
        private var instance: RentRepository? = null

        fun getInstance(): RentRepository {
            return instance ?: synchronized(this) {
                instance ?: RentRepository().also { instance = it }
            }
        }
    }
}
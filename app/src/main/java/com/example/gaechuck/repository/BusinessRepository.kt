package com.example.gaechuck.repository

import com.example.gaechuck.api.ApiConnection
import com.example.gaechuck.data.response.GetBusinessDataResponse
import com.example.gaechuck.data.response.GetBusinessDetailResponse

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
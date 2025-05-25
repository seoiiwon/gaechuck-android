package com.example.gaechuck.api

import android.content.Context
import com.example.gaechuck.data.request.RefreshRequest
import com.example.gaechuck.data.response.LoginResponse
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TokenAuthenticator(private val context: Context) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // 이미 시도했는데도 401이면 null 리턴
        if (responseCount(response) >= 2) return null

        val accessToken = AuthManager.getToken()
        val refreshToken = AuthManager.getRefreshToken()

        // refreshToken이 없으면 재로그인 유도
        if (refreshToken.isNullOrEmpty()) return null

        val newAccessToken = refreshAccessToken(accessToken, refreshToken) ?: return null

        // 새 토큰 저장
        AuthManager.saveTokens(newAccessToken, refreshToken)

        // 요청 재시도
        return response.request.newBuilder()
            .header("Authorization", "Bearer $newAccessToken")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var res = response
        var count = 1
        while (res.priorResponse != null) {
            count++
            res = res.priorResponse!!
        }
        return count
    }

    private fun refreshAccessToken(accessToken: String?, refreshToken: String): String? {
        return try {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://117.16.152.191:30001") // BASE_URL
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(ReissueService::class.java)
            val request = RefreshRequest(accessToken ?: "", refreshToken)
            val response = service.reissueToken(request).execute()

            if (response.isSuccessful) {
                val body = response.body()
                body?.result?.accessToken
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // 내부에서 사용할 재발급용 API 인터페이스
    interface ReissueService {
        @retrofit2.http.POST("/api/v1/master/token/reissue")
        fun reissueToken(@retrofit2.http.Body request: RefreshRequest):
                retrofit2.Call<com.example.gaechuck.data.response.BaseResponse<LoginResponse>>
    }

}
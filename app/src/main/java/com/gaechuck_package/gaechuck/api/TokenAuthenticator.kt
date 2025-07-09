package com.gaechuck_package.gaechuck.api

import android.content.Context
import android.util.Log
import com.gaechuck_package.gaechuck.data.request.RefreshRequest
import com.gaechuck_package.gaechuck.data.response.LoginResponse
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TokenAuthenticator(private val context: Context) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        Log.d("TokenAuthenticator", "=== 401 에러 발생, 토큰 재발급 시도 ===")

        // 이미 시도했는데도 401이면 null 리턴
        val retryCount = responseCount(response)
        Log.d("TokenAuthenticator", "재시도 횟수: $retryCount")
        if (retryCount >= 2) {
            Log.d("TokenAuthenticator", "재시도 횟수 초과, 포기")
            return null
        }
        val accessToken = AuthManager.getToken()
        val refreshToken = AuthManager.getRefreshToken()

        Log.d("TokenAuthenticator", "AccessToken 존재: ${!accessToken.isNullOrEmpty()}")
        Log.d("TokenAuthenticator", "RefreshToken 존재: ${!refreshToken.isNullOrEmpty()}")
        Log.d("TokenAuthenticator", "AccessToken 존재: ${accessToken}")
        Log.d("TokenAuthenticator", "RefreshToken 존재: ${refreshToken}")


        // refreshToken이 없으면 재로그인 유도
        if (refreshToken.isNullOrEmpty()) {
            Log.d("TokenAuthenticator", "RefreshToken 없음, 재로그인 필요")
            return null
        }

        Log.d("TokenAuthenticator", "토큰 재발급 API 호출 시작")
        val newAccessToken = refreshAccessToken(accessToken, refreshToken) ?: return null

        if (newAccessToken != null) {
            Log.d("TokenAuthenticator", "토큰 재발급 성공")
            // 새 토큰 저장
            AuthManager.saveTokens(newAccessToken, refreshToken)

            // 요청 재시도
            return response.request.newBuilder()
                .header("Authorization", "Bearer $newAccessToken")
                .build()
        } else {
            Log.d("TokenAuthenticator", "토큰 재발급 실패")
            return null
        }
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
                Log.d("TokenAuthenticator", "재발급 응답 body: $body")
                body?.result?.accessToken
            } else {
                Log.d("TokenAuthenticator", "재발급 실패 - code: ${response.code()}, msg: ${response.errorBody()?.string()}")
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
                retrofit2.Call<com.gaechuck_package.gaechuck.data.response.BaseResponse<LoginResponse>>
    }

}
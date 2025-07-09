package com.gaechuck_package.gaechuck.api

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConnection {
    // 서버 주소
    private val BASE_URL = "http://117.16.152.191:30001"

//    private val okHttpClient = OkHttpClient.Builder()
//        .addInterceptor(HttpLoggingInterceptor().apply {
//            level = HttpLoggingInterceptor.Level.BODY
//        })
//        .build()
//
//    private val getRetrofit by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(okHttpClient)
//            .build()
//    }
//
//    val getRetrofitService: ApiService by lazy {
//        getRetrofit.create(ApiService::class.java)
//    }

    fun create(context: Context) {
        AuthManager.init(context)

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val token = AuthManager.getToken()
            val path = originalRequest.url.encodedPath
            val needsAuth = requiresAuth(path)

            // 디버깅 로그 추가
            Log.d("AuthInterceptor", "API Path: $path")
            Log.d("AuthInterceptor", "Needs Auth: $needsAuth")
            Log.d("AuthInterceptor", "Token exists: ${!token.isNullOrEmpty()}")

            val requestBuilder = originalRequest.newBuilder()

            // 토큰이 있고, 로그인 필요 API라면 Authorization 추가
            if (!token.isNullOrEmpty() && requiresAuth(originalRequest.url.encodedPath)) {
                Log.d("AuthInterceptor", "Adding Authorization header")
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }

            chain.proceed(requestBuilder.build())
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(authInterceptor)
            .authenticator(TokenAuthenticator(context)) // 401 발생 시 자동 토큰 재발급
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    private lateinit var retrofit: Retrofit

    val getRetrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    // 로그인 등 인증 없이 접근할 수 있는 API 경로 리스트
    private fun requiresAuth(path: String): Boolean {
        val publicPaths = listOf(
            "/api/v1/lostitems/all",
            "/api/v1/lostitems/detail",
            "/api/v1/rent/list",
            "/api/v1/rent/detailItem",
            "/api/v1/coalition/all",
            "/api/v1/coalition/detail",
            "/api/v1/master/sign-in",
            "/api/v1/master/token/reissue",
            "/api/v1/menus/weeklyMenu",
            "/api/v1/notifications/allNotification",
            "/api/v1/council/show"  // <- 이걸 포함하면 show/106도 걸러짐
        )

        // startsWith 로 처리
        return !publicPaths.any { path.startsWith(it) }
    }
}
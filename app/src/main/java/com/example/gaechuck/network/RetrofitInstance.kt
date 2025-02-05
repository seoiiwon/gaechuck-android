package com.example.gaechuck.network

import com.example.gaechuck.ui.noticeuniv.viewmodel.NoticeUnivApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://203.255.15.32:30001/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: NoticeUnivApi = retrofit.create(NoticeUnivApi::class.java)

}
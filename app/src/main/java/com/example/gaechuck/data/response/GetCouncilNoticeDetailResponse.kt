package com.example.gaechuck.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetCouncilNoticeDetailResponse(
    val id: Int = -1,
    val title: String,
    val body: String,
    val images: List<String>?, // ✅ representationImages → images (API 응답과 일치하도록 수정)
    val time: String
) : Parcelable

package com.example.gaechuck.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetCouncilNoticeDataResponse(
    val id: Int,
    val title: String,
    val body: String,
    val representationImages: String,
    val time: String,
) : Parcelable

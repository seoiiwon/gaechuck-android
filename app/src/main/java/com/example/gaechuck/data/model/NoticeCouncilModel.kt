package com.example.gaechuck.data.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NoticeCouncilModel(
    val title: String,
    val body: String,
    val date: String,
    val imageList: List<String>? = null
) : Parcelable
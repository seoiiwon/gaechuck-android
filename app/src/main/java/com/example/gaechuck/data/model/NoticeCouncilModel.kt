package com.example.gaechuck.data.model

import android.os.Parcel
import android.os.Parcelable

data class NoticeCouncilModel(
    val title: String,
    val body: String,
    val date: String,
    val image: String?
)
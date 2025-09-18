package com.gaechuck_package.gaechuck.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class NoticeCouncilModel(
    val title: String,
    val body: String,
    val date: String,
    val imageList: List<String>? = null
) : Parcelable
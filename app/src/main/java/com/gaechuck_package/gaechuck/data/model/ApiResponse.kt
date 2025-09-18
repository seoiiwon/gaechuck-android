package com.gaechuck_package.gaechuck.data.model

import androidx.annotation.Keep

@Keep
data class ApiResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<NoticeUnivModel>
)

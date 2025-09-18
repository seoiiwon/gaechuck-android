package com.gaechuck_package.gaechuck.data.response
import androidx.annotation.Keep

@Keep
data class BaseListResponse<T>(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<T>
)

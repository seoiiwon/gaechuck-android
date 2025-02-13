package com.example.gaechuck.data.response

data class BaseListResponse<T>(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<T>
)

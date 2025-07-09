package com.gaechuck_package.gaechuck.data.response

data class BaseResponse<T> (
    val result: T?,
    val code: String,
    val message: String,
    val isSuccess: Boolean
)
package com.gaechuck_package.gaechuck.data.model

data class ApiResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<NoticeUnivModel>
)

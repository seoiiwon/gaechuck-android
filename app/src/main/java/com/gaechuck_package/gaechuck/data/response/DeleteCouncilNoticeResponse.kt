package com.gaechuck_package.gaechuck.data.response
import androidx.annotation.Keep

@Keep
data class DeleteCouncilNoticeResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: String
)

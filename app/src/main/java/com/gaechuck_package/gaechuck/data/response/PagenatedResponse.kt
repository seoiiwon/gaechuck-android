package com.gaechuck_package.gaechuck.data.response
import androidx.annotation.Keep

@Keep
data class PagenatedResponse<T>(
    val content: List<T>,
    val first: Boolean,
    val last: Boolean
)

package com.gaechuck_package.gaechuck.data.response

data class PagenatedResponse<T>(
    val content: List<T>,
    val first: Boolean,
    val last: Boolean
)

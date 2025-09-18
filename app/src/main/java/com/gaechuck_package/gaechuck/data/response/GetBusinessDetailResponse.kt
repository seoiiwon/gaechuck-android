package com.gaechuck_package.gaechuck.data.response
import androidx.annotation.Keep

@Keep
data class GetBusinessDetailResponse(
        val benefit: String,
        val category: String,
        val coalitionId: Int,
        val coalitionName: String,
        val image: String?,
        val images: List<String>
    )
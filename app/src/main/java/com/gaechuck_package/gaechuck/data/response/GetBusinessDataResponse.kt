package com.gaechuck_package.gaechuck.data.response
import androidx.annotation.Keep

@Keep
data class GetBusinessDataResponse(
        val content: List<BusinessList>,
        val first: Boolean,
        val last: Boolean
)

@Keep
data class BusinessList(
            val benefit: String,
            val category: String,
            val coalitionId: Int,
            val coalitionName: String,
            val image: String?,
            val images: String
)
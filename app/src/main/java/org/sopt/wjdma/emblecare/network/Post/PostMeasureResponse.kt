package org.sopt.wjdma.emblecare.network.Post

data class PostMeasureResponse(
        val status: Int,
        val message: String,
        val data: measureData
)

data class measureData(
        val hb: Double,
        val date: String
)
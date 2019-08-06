package org.sopt.wjdma.emblecare.network.Get

data class GetMeasureListResponse(
        val message: String,
        val data: ArrayList<MeasureListData>?,
        val dataNum: Int
)

data class MeasureListData(
        val idx: Int,
        val hb: Double,
        val period: Int,
        val date: String
)
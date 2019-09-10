package org.sopt.wjdma.emblecare.network.Get

data class GetMainResponse (
        var status: Int,
        var message: String,
        var data: List<mainData>
)

data class mainData(
        var name: String,
        var risk: Int
)
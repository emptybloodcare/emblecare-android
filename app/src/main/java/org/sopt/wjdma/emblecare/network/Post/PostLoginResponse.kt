package org.sopt.wjdma.emblecare.network.Post

data class PostLoginResponse(
        val message: String,
        val data: idxData?
)

data class idxData(
        val idx: Int
)
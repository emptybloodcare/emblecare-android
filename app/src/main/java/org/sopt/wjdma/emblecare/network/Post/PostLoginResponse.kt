package org.sopt.wjdma.emblecare.network.Post

data class PostLoginResponse(
        val status: Int,
        val message: String,
        val data: idxData?
)

data class idxData(
        val idx: Int
)
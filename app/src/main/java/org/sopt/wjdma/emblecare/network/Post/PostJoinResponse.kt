package org.sopt.wjdma.emblecare.network.Post

data class PostJoinResponse(
        val status: Int,
        val message: String
)

data class JoinData (
        var id: String,
        var pw: String,
        var name: String,
        var gender: Int,
        var birth: String
)
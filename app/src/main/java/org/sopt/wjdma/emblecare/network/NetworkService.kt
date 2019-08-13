package org.sopt.wjdma.emblecare.network

import com.google.gson.JsonObject
import org.sopt.wjdma.emblecare.network.Get.*
import org.sopt.wjdma.emblecare.network.Post.*
import retrofit2.Call
import retrofit2.http.*

interface NetworkService {

    //로그인
    @POST("/api/login")
    fun postLoginResponse(
            @Header("Content-Type") content_type: String,
            @Body body:JsonObject
    ): Call<PostLoginResponse>

    //회원가입
    @POST("/api/join")
    fun postJoinResponse(
            @Header("Content-Type") content_type: String,
            @Body body: JsonObject
    ): Call<PostJoinResponse>

    //측정하기
    @POST("/api/measure")
    fun postMeasureResponse(
            @Header("Content-Type") content_type: String,
            @Header("Authorization") token: String,
            @Body body: JsonObject
    ): Call<PostMeasureResponse>

    //측정리스트
    @GET("/user/{user_idx}/measures")
    fun getMeasureListResponse(
            @Header("Content-Type") content_type: String,
            @Path("user_idx") user_idx: Int?
    ): Call<GetMeasureListResponse>

    //측정하기 클릭
    @POST("/api/measure_flag")
    fun postMeasureflagResponse(
            @Header("Content-Type") content_type: String,
            @Body body: JsonObject
    ): Call<PostMeasureFlagResponse>

    //날씨
    @GET("/api/weather")
    fun getWeatherResponse(
            @Header("Content-Type") content_type: String
    ): Call<GetWeatherResponse>
}
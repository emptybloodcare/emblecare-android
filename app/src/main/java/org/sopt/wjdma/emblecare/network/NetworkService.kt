package org.sopt.wjdma.emblecare.network

import com.google.gson.JsonObject
import okhttp3.MultipartBody
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
    @Multipart
    @POST("/api/measure")
    fun postMeasureResponse(
            @Part("user_idx") user_idx: Int?,
            @Part("period") period: Int?,
            @Part video: MultipartBody.Part?
    ): Call<PostMeasureResponse>

    //측정리스트
    @GET("/api/measures")
    fun getMeasureListResponse(
            @Header("Content-Type") content_type: String,
            @Query ("user_idx") user_idx: Int?
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

    //메인화면
    @GET("/api/main")
    fun getMainResponse(
            @Header("Content-Type") content_type: String,
            @Query ("user_idx") user_idx: Int?
    ): Call<GetMainResponse>
}
package org.sopt.wjdma.emblecare.network

import android.app.Application
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApplicationController: Application() {
    private val baseURL = "http://13.125.154.37"
    lateinit var networkService: NetworkService

    companion object {
        lateinit var instance: ApplicationController
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        buildNetwork()
    }

    fun buildNetwork(){
        instance = this

        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.MINUTES)
                .readTimeout(10,TimeUnit.MINUTES)
                .writeTimeout(10,TimeUnit.MINUTES)
                .build()

        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        networkService = retrofit.create(NetworkService::class.java)
    }
}
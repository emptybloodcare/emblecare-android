package org.sopt.wjdma.emblecare.network.Get

data class GetWeatherResponse(
        val message: String,
        val data: weatherData
)

data class weatherData(
        val temp: Double,
        val reh: Int
)
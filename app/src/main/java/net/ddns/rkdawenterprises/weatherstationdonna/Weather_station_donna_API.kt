@file:Suppress("ClassName",
               "FunctionName",
               "RedundantSemicolon",
               "PrivatePropertyName",
               "LocalVariableName")

package net.ddns.rkdawenterprises.weatherstationdonna

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

private const val s_base_URI = "https://rkdawenterprises.ddns.net"

private val s_retrofit_weather_station_donna = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(s_base_URI)
        .build()

interface Weather_station_donna_API_service
{
    @GET("rkdaweapi/weather_station_data")
    suspend fun get_weather_station_data(): String
}

object Weather_station_donna_API
{
    val m_weather_station_donna_API_service: Weather_station_donna_API_service by lazy {
        s_retrofit_weather_station_donna.create(Weather_station_donna_API_service::class.java)
    }
}


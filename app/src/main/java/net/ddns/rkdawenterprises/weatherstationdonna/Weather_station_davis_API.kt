@file:Suppress("ClassName",
               "FunctionName",
               "RedundantSemicolon",
               "PrivatePropertyName",
               "LocalVariableName")

package net.ddns.rkdawenterprises.weatherstationdonna

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

private const val s_base_URI = "https://www.weatherlink.com"

private val s_retrofit_weather_station_davis = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(s_base_URI)
        .build()

interface Weather_station_davis_API_service
{
    @GET("embeddablePage/getData/f495986504f843dc91b31d956846bd87")
    suspend fun get_weather_station_data(): String

    @GET("embeddablePage/show/f495986504f843dc91b31d956846bd87/wide")
    suspend fun get_weather_station_page(): String
}

object Weather_station_davis_API
{
    val m_weather_station_davis_API_service: Weather_station_davis_API_service by lazy {
        s_retrofit_weather_station_davis.create(Weather_station_davis_API_service::class.java)
    }
}


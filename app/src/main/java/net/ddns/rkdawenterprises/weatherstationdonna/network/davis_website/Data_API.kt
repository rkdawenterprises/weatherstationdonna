@file:Suppress("ClassName",
               "FunctionName",
               "RedundantSemicolon",
               "PrivatePropertyName",
               "LocalVariableName",
               "PropertyName")

package net.ddns.rkdawenterprises.weatherstationdonna.network.davis_website

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

private const val s_base_URI = "https://www.weatherlink.com"

private val s_retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(s_base_URI)
        .build()

interface Davis_API_service
{
    @GET("embeddablePage/getData/f495986504f843dc91b31d956846bd87")
    suspend fun get_weather_station_data(): String

    @GET("embeddablePage/show/f495986504f843dc91b31d956846bd87/wide")
    suspend fun get_weather_station_page(): String
}

public object Davis_API
{
    val m_davis_API_service: Davis_API_service by lazy {
        s_retrofit.create(Davis_API_service::class.java)
    }
}

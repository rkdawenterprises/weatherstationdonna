@file:Suppress("ClassName",
               "FunctionName",
               "RedundantSemicolon",
               "PrivatePropertyName",
               "LocalVariableName",
               "PropertyName")

package net.ddns.rkdawenterprises.rkdawe_api_common

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

private const val s_base_URI = "https://rkdawenterprises.ddns.net"

private val s_retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(s_base_URI)
        .build()

interface RKDAWE_API_service
{
    @GET("rkdaweapi/weather_station_data")
    suspend fun get_weather_station_data(): String
}

object RKDAWE_API
{
    val m_RKDAWE_API_service: RKDAWE_API_service by lazy {
        s_retrofit.create(RKDAWE_API_service::class.java)
    }
}


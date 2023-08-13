/*
 * Copyright (c) 2023 RKDAW Enterprises and Ralph Williamson.
 *       email: rkdawenterprises@gmail.com
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("ClassName",
               "FunctionName",
               "RedundantSemicolon",
               "PrivatePropertyName",
               "LocalVariableName",
               "PropertyName")

package net.ddns.rkdawenterprises.weather_gov_api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

private const val s_base_URI = "https://api.weather.gov"

private val client: OkHttpClient = OkHttpClient.Builder()
    .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
    .build();

private val s_retrofit = Retrofit.Builder()
    .baseUrl(s_base_URI)
//    .client(client)
    .addConverterFactory(ScalarsConverterFactory.create())
    .build();

interface Weather_gov_API_service
{
    @Headers("User-Agent: (Weather Station Donna Android App, rkdawenterprises@gmail.com)")
    @GET("points/{latitude},{longitude}")
    suspend fun get_points(@Path("latitude") latitude: String,
                           @Path("longitude") longitude: String): String

    @Headers("User-Agent: (Weather Station Donna Android App, rkdawenterprises@gmail.com)")
    @GET("gridpoints/{wfo}/{x},{y}")
    suspend fun get_gridpoints(@Path("wfo") wfo: String,
                               @Path("x") x: String,
                               @Path("y") y: String): String

    @Headers("User-Agent: (Weather Station Donna Android App, rkdawenterprises@gmail.com)")
    @GET("gridpoints/{wfo}/{x},{y}/forecast")
    suspend fun get_gridpoints_forecast(@Path("wfo") wfo: String,
                                        @Path("x") x: String,
                                        @Path("y") y: String): String

    @Headers("User-Agent: (Weather Station Donna Android App, rkdawenterprises@gmail.com)")
    @GET("gridpoints/{wfo}/{x},{y}/forecast/hourly")
    suspend fun get_gridpoints_forecast_hourly(@Path("wfo") wfo: String,
                                               @Path("x") x: String,
                                               @Path("y") y: String): String

    @Headers("User-Agent: (Weather Station Donna Android App, rkdawenterprises@gmail.com)")
    @GET("gridpoints/{wfo}/{x},{y}/stations")
    suspend fun get_gridpoints_stations(@Path("wfo") wfo: String,
                                        @Path("x") x: String,
                                        @Path("y") y: String): String

    @Headers("User-Agent: (Weather Station Donna Android App, rkdawenterprises@gmail.com)")
    @GET("stations/{station_id}/observations/latest")
    suspend fun get_stations_observations_latest(@Path("station_id") wfo: String): String

    @Headers("User-Agent: (Weather Station Donna Android App, rkdawenterprises@gmail.com)")
    @GET("alerts/active")
    suspend fun get_alerts_active(@Query("point") point: String): String
}

object Weather_gov_API
{
    val m_weather_gov_API_service: Weather_gov_API_service by lazy {
        s_retrofit.create(Weather_gov_API_service::class.java)
    }
}



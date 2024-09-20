/*
 * Copyright (c) 2019-2023 RKDAW Enterprises and Ralph Williamson.
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

package net.ddns.rkdawenterprises.rkdawe_api_common

//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val s_base_URI = "https://rkdawenterprises.ddns.net"

//private val client: OkHttpClient = OkHttpClient.Builder()
//    .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
//    .build();

private val s_retrofit = Retrofit.Builder()
        .baseUrl(s_base_URI)
//        .client(client)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

interface RKDAWE_API_service
{
    @GET("rkdaweapi/v1")
    suspend fun update_paths(): String

    @GET("rkdaweapi/{path}")
    suspend fun get_weather_station_data(@Path(value = "path", encoded = true) path: String,
                                         @Query("forecast_location") forecast_location: String): String

    @GET("rkdaweapi/{path}")
    suspend fun get_weather_station_data(@Path(value = "path", encoded = true) path: String): String
}

object RKDAWE_API
{
    val m_RKDAWE_API_service: RKDAWE_API_service by lazy {
        s_retrofit.create(RKDAWE_API_service::class.java)
    }
}

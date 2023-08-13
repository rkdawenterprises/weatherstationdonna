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
               "PropertyName",
               "PackageName")

package net.ddns.rkdawenterprises.weather_gov_api

import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException

@SuppressWarnings("unused")
class Weather_gov_data
{
    // TODO: Flesh out Weather_gov_data with what is needed in the UI.
    
    companion object
    {
        private val m_GSON = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()

        fun serialize_to_JSON(`object`: Weather_gov_data): String
        {
            return m_GSON.toJson(`object`)
        }

        fun deserialize_from_JSON(string_JSON: String?): Weather_gov_data?
        {
            var `object`: Weather_gov_data? = null
            try
            {
                `object` = m_GSON.fromJson(string_JSON,
                                           Weather_gov_data::class.java)
            }
            catch(exception: JsonSyntaxException)
            {
                println("Bad data format for Weather_gov_data: $exception")
                println(">>>$string_JSON<<<")
            }

            return `object`
        }
    }

    fun serialize_to_JSON(): String
    {
        return serialize_to_JSON(this)
    }
}

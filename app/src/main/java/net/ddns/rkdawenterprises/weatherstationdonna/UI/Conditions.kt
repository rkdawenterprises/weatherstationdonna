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
               "PropertyName",
               "PackageName",
               "UnnecessaryVariable")

package net.ddns.rkdawenterprises.weatherstationdonna.UI

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.ddns.rkdawenterprises.rkdawe_api_common.Weather_data
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.material_colors_extended

@Suppress("unused")
private const val LOG_TAG = "Conditions_composable";

@Composable
fun Conditions(weather_data_RKDAWE: Weather_data?,
               weather_data_davis: net.ddns.rkdawenterprises.davis_website.Weather_data?)
{
    val spaced_by = 5.dp;
    val column_weights = floatArrayOf(0.3f, 0.2f, 0.5f);
    val icon_size = arrayOf(45.dp,50.dp);

    Column(modifier = Modifier.fillMaxSize(),
           verticalArrangement = Arrangement.spacedBy(10.dp))
    {
        Humidity(weather_data_RKDAWE,
                 weather_data_davis,
                 spaced_by,
                 column_weights,
                 icon_size);

        Divider(color = MaterialTheme.material_colors_extended.view_divider,
                modifier = Modifier.fillMaxWidth().height(2.dp));

        Wind(weather_data_RKDAWE,
             weather_data_davis,
             spaced_by,
             column_weights,
             icon_size);

        Divider(color = MaterialTheme.material_colors_extended.view_divider,
                modifier = Modifier.fillMaxWidth().height(2.dp));

        Rain(weather_data_RKDAWE,
             weather_data_davis,
             spaced_by,
             column_weights,
             icon_size);
        
        Divider(color = MaterialTheme.material_colors_extended.view_divider,
                modifier = Modifier.fillMaxWidth().height(2.dp));

        Barometer(weather_data_RKDAWE,
                  weather_data_davis,
                  spaced_by,
                  column_weights,
                  icon_size);
    }
}

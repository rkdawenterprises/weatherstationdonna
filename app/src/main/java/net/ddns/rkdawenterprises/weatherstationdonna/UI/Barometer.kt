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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.ddns.rkdawenterprises.rkdawe_api_common.Utilities
import net.ddns.rkdawenterprises.rkdawe_api_common.Weather_data
import net.ddns.rkdawenterprises.weatherstationdonna.R
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.Main_typography

@Suppress("unused")
private const val LOG_TAG = "Barometer_composable";

@Composable
fun Barometer(weather_data_RKDAWE: Weather_data?,
              weather_data_davis: net.ddns.rkdawenterprises.davis_website.Weather_data?,
              spaced_by: Dp,
              column_weights: FloatArray,
              icon_size: Array<Dp>)
{
    Row(modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(space = spaced_by,
                                                     alignment = Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically)
    {
        Image(painterResource(R.drawable.barometric_pressure_48),
              contentDescription = stringResource(id = R.string.humidity_icon),
              modifier = Modifier
                  .width(icon_size[0])
                  .height(icon_size[1]));

        Text(stringResource(R.string.barometer_colon),
             modifier = Modifier
                 .weight(column_weights[0],
                         fill = true),
             style = Main_typography.h6);

        val barometric_pressure_text: String? =
            if(weather_data_RKDAWE != null)
            {
                "${String.format("%.2f ", weather_data_RKDAWE.barometer)}\n${weather_data_RKDAWE.barometer_units}";
            }
            else if(weather_data_davis != null)
            {
                "${weather_data_davis.barometer} ${weather_data_davis.barometerUnits}"
            }
            else null;

        if(barometric_pressure_text != null)
        {
            Text(barometric_pressure_text,
                 modifier = Modifier.weight(column_weights[1],
                                            fill = true),
                 style = Main_typography.h6)
        };

        Column(modifier = Modifier
            .weight(column_weights[2],
                    fill = true),
               verticalArrangement = Arrangement.spacedBy(5.dp))
        {
            val barometer_trend_text: String? =
                if(weather_data_RKDAWE != null)
                {
                    "${stringResource(id = R.string.barometer_trend)} ${weather_data_RKDAWE.bar_trend}";
                }
                else if(weather_data_davis != null)
                {
                    "${stringResource(id = R.string.barometer_trend)} ${weather_data_davis.barometerTrend}";
                }
                else null;

            if(barometer_trend_text != null)
            {
                Text(barometer_trend_text,
                     style = Main_typography.subtitle1)
            };

            if(weather_data_RKDAWE != null)
            {
                Text("${stringResource(R.string.high_colon)} ${
                    String.format("%.2f ", weather_data_RKDAWE.daily_high_barometer)} ${
                    weather_data_RKDAWE.barometer_units} ${stringResource(id = R.string.at)} ${
                    Utilities.convert_time_UTC_to_local(weather_data_RKDAWE.time_of_day_high_bar,
                                                        "h:mm a")}",
                     style = Main_typography.subtitle1)
            };

            if(weather_data_RKDAWE != null)
            {
                Text("${stringResource(R.string.low_colon)} ${
                    String.format("%.2f ", weather_data_RKDAWE.daily_low_barometer)} ${
                    weather_data_RKDAWE.barometer_units} ${stringResource(id = R.string.at)} ${
                    Utilities.convert_time_UTC_to_local(weather_data_RKDAWE.time_of_day_low_bar,
                                                        "h:mm a")}",
                     style = Main_typography.subtitle1)
            };
        }
    }
}

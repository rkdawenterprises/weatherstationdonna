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

import android.text.Html
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
import net.ddns.rkdawenterprises.rkdawe_api_common.Weather_data
import net.ddns.rkdawenterprises.weatherstationdonna.R
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.Main_typography

@Suppress("unused")
private const val LOG_TAG = "Humidity_composable";

@Composable
fun Humidity(weather_data_RKDAWE: Weather_data?,
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
        Image(painterResource(R.drawable.humidity_percentage_48),
              contentDescription = stringResource(id = R.string.humidity_icon),
              modifier = Modifier
                  .width(icon_size[0])
                  .height(icon_size[1]));

        Text(stringResource(R.string.humidity_colon),
             modifier = Modifier
                 .weight(column_weights[0],
                         fill = true),
             style = Main_typography.h6);

        val current_humidity_text: String? =
            if(weather_data_RKDAWE != null)
            {
                Html.fromHtml("${weather_data_RKDAWE.outside_humidity} ${
                    weather_data_RKDAWE.humidity_units}", Html.FROM_HTML_MODE_COMPACT).toString();
            }
            else if(weather_data_davis != null)
            {
                "${weather_data_davis.humidity} %"
            }
            else null;

        if(current_humidity_text != null)
        {
            Text(current_humidity_text,
                 modifier = Modifier.weight(column_weights[1],
                                            fill = true),
                 style = Main_typography.h6)
        };

        Column(modifier = Modifier
            .weight(column_weights[2],
                    fill = true),
               verticalArrangement = Arrangement.spacedBy(5.dp))
        {
            val feels_like_temperature_text: String? =
                if(weather_data_RKDAWE != null)
                {
                    Html.fromHtml("${stringResource(id = R.string.feels_like)} ${
                        String.format("%.1f ", weather_data_RKDAWE.heat_index_derived)} ${
                        weather_data_RKDAWE.temperature_units}",
                                  Html.FROM_HTML_MODE_COMPACT).toString();
                }
                else if(weather_data_davis != null)
                {
                    Html.fromHtml("${stringResource(id = R.string.feels_like)} ${
                        weather_data_davis.temperatureFeelLike} ${weather_data_davis.tempUnits}",
                                  Html.FROM_HTML_MODE_COMPACT).toString();
                }
                else null;

            if(feels_like_temperature_text != null)
            {
                Text(feels_like_temperature_text,
                     style = Main_typography.subtitle1)
            };

            val wind_chill_temperature_text: String? =
                if(weather_data_RKDAWE != null)
                {
                    Html.fromHtml("${stringResource(id = R.string.wind_chill)} ${
                        String.format("%.1f ", weather_data_RKDAWE.wind_chill_derived)} ${
                        weather_data_RKDAWE.temperature_units}",
                                  Html.FROM_HTML_MODE_COMPACT).toString();
                }
                else if(weather_data_davis != null)
                {
                    Html.fromHtml("${stringResource(id = R.string.wind_chill)} ${
                        String.format("%.1f ", Weather_data.calculate_wind_chill(weather_data_davis.temperature.toDouble(),
                                                                                 weather_data_davis.wind.toDouble()))} ${weather_data_davis.tempUnits}",
                                  Html.FROM_HTML_MODE_COMPACT).toString();
                }
                else null;

            if(wind_chill_temperature_text != null)
            {
                Text(wind_chill_temperature_text,
                     style = Main_typography.subtitle1)
            };
        }
    }
}

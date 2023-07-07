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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.ddns.rkdawenterprises.rkdawe_api_common.Utilities.convert_time_UTC_to_local
import net.ddns.rkdawenterprises.rkdawe_api_common.Utilities.convert_timestamp_to_local
import net.ddns.rkdawenterprises.rkdawe_api_common.Weather_data
import net.ddns.rkdawenterprises.weatherstationdonna.R
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.Main_typography
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.material_colors_extended
import java.time.format.DateTimeFormatter

@Suppress("unused")
private const val LOG_TAG = "Wind_composable";

@Composable
fun Wind(weather_data_RKDAWE: Weather_data?,
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
        Image(painterResource(R.drawable.air_48),
              contentDescription = stringResource(id = R.string.wind_icon),
              modifier = Modifier
                  .width(icon_size[0])
                  .height(icon_size[1]));

        Text(stringResource(R.string.wind_colon),
             modifier = Modifier
                 .weight(column_weights[0],
                         fill = true)
                 .padding(start = 5.dp),
             style = Main_typography.h6);

        val current_wind_text: String? =
            if(weather_data_RKDAWE != null)
            {
                "${weather_data_RKDAWE.wind_speed} ${weather_data_RKDAWE.wind_speed_units}"
            }
            else if(weather_data_davis != null)
            {
                "${weather_data_davis.wind} ${weather_data_davis.windUnits}"
            }
            else null;

        if(current_wind_text != null)
        {
            Text(current_wind_text,
                 modifier = Modifier.weight(column_weights[1],
                                            fill = true),
                 style = Main_typography.h6)
        };

        val day_high_text: String? =
            if(weather_data_RKDAWE != null)
            {
                "${stringResource(id = R.string.peak)} ${weather_data_RKDAWE.daily_hi_wind_speed} ${
                    weather_data_RKDAWE.wind_speed_units} ${stringResource(id = R.string.at)} ${
                    convert_time_UTC_to_local(weather_data_RKDAWE.time_of_hi_speed,
                                              "h:mm a")}"
            }
            else if(weather_data_davis != null)
            {
                "${stringResource(id = R.string.peak)} ${weather_data_davis.gust} ${
                    weather_data_davis.windUnits} ${stringResource(id = R.string.at)} ${
                    DateTimeFormatter.ofPattern("h:mm a")
                        .format(convert_timestamp_to_local(weather_data_davis.gustAt,
                                                           weather_data_davis.timeZoneId))}"
            }
            else null;

        if(day_high_text != null)
        {
            Text(day_high_text,
                 modifier = Modifier.weight(column_weights[2],
                                            fill = true),
                 style = Main_typography.subtitle1)
        };
    }

    Row(modifier = Modifier.padding(top = 10.dp).fillMaxWidth().fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically)
    {
        val wind_direction: Int = weather_data_RKDAWE?.wind_direction
            ?: (weather_data_davis?.windDirection
                ?: 0);

        Compass(modifier = Modifier.size(70.dp),
                angle = wind_direction,
                marker_degrees_step = 30,
                outline_color = MaterialTheme.material_colors_extended.icon_tint);

        if(weather_data_RKDAWE != null)
        {
            Column(modifier = Modifier,
                   verticalArrangement = Arrangement.spacedBy(5.dp))
            {

                Text("${stringResource(id = R.string.two_minute_average)} ${
                    weather_data_RKDAWE.two_min_avg_wind_speed
                } ${weather_data_RKDAWE.wind_speed_units}",
                     style = Main_typography.subtitle1);

                Text("${stringResource(id = R.string.ten_minute_average)} ${
                    weather_data_RKDAWE.ten_min_avg_wind_speed
                } ${weather_data_RKDAWE.wind_speed_units}",
                     style = Main_typography.subtitle1);

                Text(Html.fromHtml("${stringResource(id = R.string.ten_minute_gust)} ${
                    weather_data_RKDAWE.ten_min_wind_gust
                } ${weather_data_RKDAWE.wind_speed_units} ${
                    stringResource(id = R.string.at)
                } ${weather_data_RKDAWE.wind_direction_of_ten_min_wind_gust} ${
                    weather_data_RKDAWE.wind_direction_units
                }",
                                   Html.FROM_HTML_MODE_COMPACT).toString(),
                     style = Main_typography.subtitle1);
            }
        }
    }
}

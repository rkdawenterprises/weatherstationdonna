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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import net.ddns.rkdawenterprises.rkdawe_api_common.Utilities.convert_time_UTC_to_local
import net.ddns.rkdawenterprises.rkdawe_api_common.Weather_data
import net.ddns.rkdawenterprises.weatherstationdonna.R
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.Typography

@Suppress("unused")
private const val LOG_TAG = "Wind_composable";

@Suppress("UNUSED_PARAMETER")
@Composable
fun Wind(weather_data: Weather_data,
         is_larger_window: Boolean,
         divider_thickness: Dp,
         icon_height: Dp,
         icon_width: Dp,
         horizontal_padding: Dp,
         vertical_padding: Dp)
{
    val current_wind_text: String = "${weather_data.wind_speed} ${weather_data.wind_speed_units}"

    val local_time_of_hi_speed = try
    {
        convert_time_UTC_to_local(weather_data.time_of_hi_speed,
                                  "h:mm a")
    }
    catch(exception: java.time.format.DateTimeParseException)
    {
        stringResource(id = R.string.NA)
    }

    val column_weights = floatArrayOf(0.4f,
                                      0.6f);
    var column_index = 0

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(top = vertical_padding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically)
    {
        Column(modifier = Modifier
            .weight(column_weights[column_index++],
                    fill = true)
            .padding(start = horizontal_padding,
                     end = horizontal_padding,
                     bottom = vertical_padding),
               verticalArrangement = Arrangement.spacedBy(vertical_padding),
               horizontalAlignment = Alignment.CenterHorizontally)
        {
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(space = horizontal_padding,
                                                             alignment = Alignment.Start),
                verticalAlignment = Alignment.CenterVertically)
            {
                Image(modifier = Modifier.height(icon_height).width(icon_width).padding(start = horizontal_padding),
                      painter = painterResource(R.drawable.outline_air_32),
                      contentDescription = stringResource(id = R.string.wind_icon))

                Text(modifier = Modifier.padding(start = horizontal_padding),
                     text = current_wind_text,
                     style = Typography.headlineSmall,
                     textAlign = TextAlign.Center)
            }

            Compass(modifier = Modifier.size(icon_height),
                    angle = weather_data.wind_direction,
                    marker_degrees_step = 30,
                    outline_color = MaterialTheme.colorScheme.inverseSurface);
        }

        Column(modifier = Modifier
            .weight(column_weights[column_index++],
                    fill = true)
            .padding(start = horizontal_padding),
               verticalArrangement = Arrangement.spacedBy(vertical_padding)) {
            Text(text = "${stringResource(id = R.string.peak)} ${weather_data.daily_hi_wind_speed} ${weather_data.wind_speed_units} ${stringResource(id = R.string.at)} $local_time_of_hi_speed",
                 style = Typography.titleMedium,
                 textAlign = TextAlign.Left)

            Text(text = "${stringResource(id = R.string.two_minute_average)}: ${weather_data.two_min_avg_wind_speed} ${weather_data.wind_speed_units}",
                 style = Typography.titleMedium,
                 textAlign = TextAlign.Left)

            Text(text = "${stringResource(id = R.string.ten_minute_average)}: ${weather_data.ten_min_avg_wind_speed} ${weather_data.wind_speed_units}",
                 style = Typography.titleMedium,
                 textAlign = TextAlign.Left)

            Text(text = Html.fromHtml("${stringResource(id = R.string.ten_minute_gust)} ${weather_data.ten_min_wind_gust} ${weather_data.wind_speed_units} ${stringResource(id = R.string.at)} ${weather_data.wind_direction_of_ten_min_wind_gust} ${weather_data.wind_direction_units}",
                                      Html.FROM_HTML_MODE_COMPACT).toString(),
                 style = Typography.titleMedium,
                 textAlign = TextAlign.Left)
        }
    }
}

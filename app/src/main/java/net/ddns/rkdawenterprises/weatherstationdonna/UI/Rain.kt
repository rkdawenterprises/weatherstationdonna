/*
 * Copyright (c) 2019-2024 RKDAW Enterprises and Ralph Williamson.
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.ddns.rkdawenterprises.rkdawe_api_common.Weather_data
import net.ddns.rkdawenterprises.weatherstationdonna.R
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.Typography

@Suppress("unused")
private const val LOG_TAG = "Rain_composable";

@Composable
fun Rain(weather_data: Weather_data,
         is_larger_window: Boolean,
         divider_thickness: Dp,
         icon_height: Dp,
         icon_width: Dp,
         horizontal_padding: Dp,
         vertical_padding: Dp)
{
    val column_weights = floatArrayOf(0.3f,
                                      0.7f);
    var column_index = 0

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(top = vertical_padding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Image(modifier = Modifier
            .height(icon_height)
            .width(icon_width)
            .padding(start = horizontal_padding),
              painter = painterResource(R.drawable.outline_rainy_32),
              contentDescription = stringResource(id = R.string.rain_icon))

        Text(text = "${weather_data.storm_rain} ${weather_data.rain_units}",
             modifier = Modifier
                 .weight(column_weights[column_index++],
                         fill = true)
                 .padding(start = horizontal_padding,
                          end = horizontal_padding),
             style = Typography.headlineSmall,
             textAlign = TextAlign.Center)

        Column(modifier = Modifier
            .weight(column_weights[column_index++],
                    fill = true)
            .padding(start = horizontal_padding,
                     top = vertical_padding),
               verticalArrangement = Arrangement.SpaceAround) {
            Text(text = "${stringResource(R.string.rain_rate)} ${weather_data.rain_rate} ${weather_data.rain_rate_units}",
                 style = Typography.titleMedium,
                 textAlign = TextAlign.Left)

            Text(text = "${stringResource(R.string.last_fifteen_minute)} ${weather_data.last_fifteen_min_rain} ${weather_data.rain_units}",
                 style = Typography.titleMedium,
                 textAlign = TextAlign.Left)

            Text(text = "${stringResource(R.string.last_hour)} ${weather_data.last_hour_rain} ${weather_data.rain_units}",
                 style = Typography.titleMedium,
                 textAlign = TextAlign.Left)

            Text(text = "${stringResource(R.string.day_total)} ${weather_data.daily_rain} ${weather_data.rain_units}",
                 style = Typography.titleMedium,
                 textAlign = TextAlign.Left)

            Text(text = "${stringResource(R.string.last_twenty_four_hour)} ${weather_data.last_twenty_four_hour_rain} ${weather_data.rain_units}",
                 style = Typography.titleMedium,
                 textAlign = TextAlign.Left)

            Text(text = "${stringResource(R.string.day_high_rate)} ${weather_data.day_high_rain_rate} ${weather_data.rain_rate_units}",
                 style = Typography.titleMedium,
                 textAlign = TextAlign.Left)

            Text(text = "${stringResource(R.string.month_total)} ${weather_data.month_rain} ${weather_data.rain_units}",
                 style = Typography.titleMedium,
                 textAlign = TextAlign.Left)

            Text(text = "${stringResource(id = R.string.seasonal_total)} ${weather_data.year_rain} ${weather_data.rain_units}",
                 style = Typography.titleMedium,
                 textAlign = TextAlign.Left)
        }
    }
}

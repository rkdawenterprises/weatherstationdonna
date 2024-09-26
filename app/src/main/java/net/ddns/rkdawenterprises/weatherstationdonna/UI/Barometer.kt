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
import net.ddns.rkdawenterprises.rkdawe_api_common.Utilities
import net.ddns.rkdawenterprises.rkdawe_api_common.Weather_data
import net.ddns.rkdawenterprises.weatherstationdonna.R
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.Typography
import java.util.Locale

@Suppress("unused")
private const val LOG_TAG = "Barometer_composable";

@Suppress("UNUSED_PARAMETER")
@Composable
fun Barometer(weather_data: Weather_data,
              is_larger_window: Boolean,
              divider_thickness: Dp,
              icon_height: Dp,
              icon_width: Dp,
              horizontal_padding: Dp,
              vertical_padding: Dp)
{
    val time_of_day_high_bar = try
    {
        Utilities.convert_time_UTC_to_local(weather_data.time_of_day_high_bar,
                                            "h:mm a")
    }
    catch(exception: java.time.format.DateTimeParseException)
    {
        stringResource(id = R.string.NA)
    }

    val time_of_day_low_bar = try
    {
        Utilities.convert_time_UTC_to_local(weather_data.time_of_day_low_bar,
                                            "h:mm a")
    }
    catch(exception: java.time.format.DateTimeParseException)
    {
        stringResource(id = R.string.NA)
    }

    val barameter: String = if(!is_larger_window)
    {
        "${String.format(Locale.getDefault(Locale.Category.FORMAT),
                          "%.2f ",
                          weather_data.barometer)}\n${weather_data.barometer_units}"
    }
    else
    {
        "${String.format(Locale.getDefault(Locale.Category.FORMAT),
                         "%.2f ",
                         weather_data.barometer)} ${weather_data.barometer_units}"
    }

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
              painter = painterResource(R.drawable.barometric_pressure_32),
              contentDescription = stringResource(id = R.string.barometric_pressure_icon))

        Text(text = barameter,
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
            .padding(start = horizontal_padding),
               verticalArrangement = Arrangement.SpaceAround) {
            Text(text = "${stringResource(id = R.string.barometer_trend)} ${weather_data.bar_trend}",
                 style = Typography.titleMedium,
                 textAlign = TextAlign.Left)

            Text(text = "${stringResource(R.string.high_colon)} ${
                String.format(Locale.getDefault(Locale.Category.FORMAT),
                              "%.2f ",
                              weather_data.daily_high_barometer)
            } ${weather_data.barometer_units} ${stringResource(id = R.string.at)} ${time_of_day_high_bar}",
                 style = Typography.titleSmall,
                 textAlign = TextAlign.Left)

            Text(text = "${stringResource(R.string.low_colon)} ${
                String.format(Locale.getDefault(Locale.Category.FORMAT),
                              "%.2f ",
                              weather_data.daily_low_barometer)
            } ${weather_data.barometer_units} ${stringResource(id = R.string.at)} ${time_of_day_low_bar}",
                 style = Typography.titleSmall,
                 textAlign = TextAlign.Left)
        }
    }
}

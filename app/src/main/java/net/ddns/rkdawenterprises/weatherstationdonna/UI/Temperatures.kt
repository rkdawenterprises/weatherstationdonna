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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import net.ddns.rkdawenterprises.rkdawe_api_common.Utilities
import net.ddns.rkdawenterprises.rkdawe_api_common.Weather_data
import net.ddns.rkdawenterprises.weatherstationdonna.R
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.Typography
import java.util.Locale

@Suppress("unused")
private const val LOG_TAG = "Temperatures_composable";

@Suppress("UNUSED_PARAMETER")
@Composable
fun Temperatures(weather_data: Weather_data,
                 is_larger_window: Boolean,
                 divider_thickness: Dp,
                 icon_height: Dp,
                 icon_width: Dp,
                 horizontal_padding: Dp,
                 vertical_padding: Dp)
{
    val current_temperature_text: String = Html.fromHtml("${
        String.format(Locale.getDefault(Locale.Category.FORMAT),
                      "%.1f ",
                      weather_data.outside_temperature)
    } ${weather_data.temperature_units}",
                                                         Html.FROM_HTML_MODE_COMPACT).toString()

    val todays_temperature_high_text: String = Html.fromHtml("${
        String.format(Locale.getDefault(Locale.Category.FORMAT),
                      "%.1f ",
                      weather_data.day_hi_out_temp)
    } ${weather_data.temperature_units}",
                                                             Html.FROM_HTML_MODE_COMPACT).toString()

    val todays_temperature_high_time_text: String = "${stringResource(id = R.string.at)} ${
        try
        {
            Utilities.convert_time_UTC_to_local(weather_data.time_day_hi_out_temp,
                                                "h:mm a")
        }
        catch(exception: java.time.format.DateTimeParseException)
        {
            stringResource(id = R.string.NA)
        }
    }"

    val todays_temperature_low_text: String = Html.fromHtml("${
        String.format(Locale.getDefault(Locale.Category.FORMAT),
                      "%.1f ",
                      weather_data.day_low_out_temp)
    } ${weather_data.temperature_units}",
                                                            Html.FROM_HTML_MODE_COMPACT).toString()

    val todays_temperature_low_time_text: String = "${stringResource(id = R.string.at)} ${
        try
        {
            Utilities.convert_time_UTC_to_local(weather_data.time_day_low_out_temp,
                                                "h:mm a")
        }
        catch(exception: java.time.format.DateTimeParseException)
        {
            stringResource(id = R.string.NA)
        }
    }"

    val column_weights = floatArrayOf(0.3f,
                                      0.2f,
                                      0.2f,
                                      0.3f);
    var column_index = 0

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(top = vertical_padding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically)
    {
//        Image(modifier = Modifier
//            .height(icon_height)
//            .width(icon_width)
//            .padding(start = horizontal_padding),
//              painter = painterResource(R.drawable.outline_sunny_32),
//              contentDescription = stringResource(id = R.string.dynamic_forecast_unavailable_icon))
        AsyncImage(model = weather_data.period_1_forecast_icon,
                   contentDescription = stringResource(id = R.string.dynamic_forecast_icon),
                   modifier = Modifier
                        .height(icon_height)
                        .width(icon_width)
                        .padding(start = horizontal_padding),
                   contentScale = ContentScale.Fit,
                   alignment = Alignment.Center)

        Text(text = current_temperature_text,
             modifier = Modifier
                 .weight(column_weights[column_index++],
                         fill = true)
                 .padding(start = horizontal_padding,
                          end = horizontal_padding),
             style = Typography.headlineSmall,
             textAlign = TextAlign.Center)

//        VerticalDivider(modifier = Modifier
//            .height(icon_height)
//            .width(divider_thickness))

        Column(modifier = Modifier
            .weight(column_weights[column_index++],
                    fill = true)
            .height(icon_height)
            .padding(start = horizontal_padding),
               verticalArrangement = Arrangement.SpaceAround)
        {
            Text(text = "${stringResource(id = R.string.high_colon)}",
                 style = Typography.titleMedium,
                 textAlign = TextAlign.Left)

            Text(text = "${stringResource(id = R.string.low_colon)}",
                 style = Typography.titleMedium,
                 textAlign = TextAlign.Left)
        }

        Column(modifier = Modifier
            .weight(column_weights[column_index++],
                    fill = true)
            .height(icon_height)
            .padding(start = horizontal_padding),
               verticalArrangement = Arrangement.SpaceAround)
        {
            Text(text = todays_temperature_high_text,
                 style = Typography.titleMedium,
                 textAlign = TextAlign.Left)

            Text(text = todays_temperature_low_text,
                 style = Typography.titleMedium,
                 textAlign = TextAlign.Left)
        }

        Column(modifier = Modifier
            .weight(column_weights[column_index++],
                    fill = true)
            .height(icon_height)
            .padding(start = horizontal_padding),
               verticalArrangement = Arrangement.SpaceAround)
        {
            Text(text = todays_temperature_high_time_text,
                 style = Typography.titleSmall,
                 textAlign = TextAlign.Left)

            Text(text = todays_temperature_low_time_text,
                 style = Typography.titleSmall,
                 textAlign = TextAlign.Left)
        }
    }
}

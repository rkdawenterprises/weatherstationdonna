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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
import java.util.Locale

@Suppress("unused")
private const val LOG_TAG = "Humidity_composable";

@Composable
fun Humidity(weather_data: Weather_data,
             is_larger_window: Boolean,
             divider_thickness: Dp,
             icon_height: Dp,
             icon_width: Dp,
             horizontal_padding: Dp,
             vertical_padding: Dp)
{
    val current_humidity_text: String = Html.fromHtml("${weather_data.outside_humidity} ${weather_data.humidity_units}",
                                                      Html.FROM_HTML_MODE_COMPACT).toString()

    val feels_like_temperature_text: String =
            Html.fromHtml("${stringResource(id = R.string.feels_like)} ${
                String.format("%.1f ", weather_data.heat_index_derived)} ${
                weather_data.temperature_units}",
                          Html.FROM_HTML_MODE_COMPACT).toString()

    val wind_chill_temperature_text: String =
            Html.fromHtml("${stringResource(id = R.string.wind_chill)} ${
                String.format("%.1f ", weather_data.wind_chill_derived)} ${
                weather_data.temperature_units}",
                          Html.FROM_HTML_MODE_COMPACT).toString()

    val column_weights = floatArrayOf(0.3f,
                                      0.7f);
    var column_index = 0

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(top = vertical_padding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically)
    {
        Image(modifier = Modifier
            .height(icon_height)
            .width(icon_width)
            .padding(start = horizontal_padding),
              painter = painterResource(R.drawable.outline_humidity_percentage_32),
              contentDescription = stringResource(id = R.string.humidity_icon))

        Text(text = current_humidity_text,
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
            Text(text = feels_like_temperature_text,
                 style = Typography.titleMedium,
                 textAlign = TextAlign.Left)

            Text(text = wind_chill_temperature_text,
                 style = Typography.titleMedium,
                 textAlign = TextAlign.Left)
        }
    }
}

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
               "PackageName")

package net.ddns.rkdawenterprises.weatherstationdonna.UI

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.ddns.rkdawenterprises.rkdawe_api_common.Weather_data
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun Current_conditions(weather_data: Weather_data,
                       is_larger_window: Boolean,
                       divider_thickness: Dp)
{
    val icon_height = if(!is_larger_window)
    {
        75.dp
    }
    else
    {
        150.dp
    }

    val icon_width = if(!is_larger_window)
    {
        60.dp
    }
    else
    {
        120.dp
    }

    val horizontal_padding = if(!is_larger_window)
    {
        5.dp
    }
    else
    {
        30.dp
    }

    val vertical_padding = if(!is_larger_window)
    {
        5.dp
    }
    else
    {
        30.dp
    }

    Temperatures(weather_data = weather_data,
                 is_larger_window = is_larger_window,
                 divider_thickness = divider_thickness,
                 icon_height = icon_height,
                 icon_width = icon_width,
                 horizontal_padding = horizontal_padding,
                 vertical_padding = vertical_padding)

    HorizontalDivider(modifier = Modifier
        .fillMaxWidth()
        .height(divider_thickness)
        .padding(top = vertical_padding))

    Humidity(weather_data = weather_data,
             is_larger_window = is_larger_window,
             divider_thickness = divider_thickness,
             icon_height = icon_height,
             icon_width = icon_width,
             horizontal_padding = horizontal_padding,
             vertical_padding = vertical_padding)

    HorizontalDivider(modifier = Modifier
        .fillMaxWidth()
        .height(divider_thickness)
        .padding(top = vertical_padding))

    Wind(weather_data = weather_data,
         is_larger_window = is_larger_window,
         divider_thickness = divider_thickness,
         icon_height = icon_height,
         icon_width = icon_width,
         horizontal_padding = horizontal_padding,
         vertical_padding = vertical_padding)

    HorizontalDivider(modifier = Modifier
        .fillMaxWidth()
        .height(divider_thickness)
        .padding(top = vertical_padding))

    Rain(weather_data = weather_data,
         is_larger_window = is_larger_window,
         divider_thickness = divider_thickness,
         icon_height = icon_height,
         icon_width = icon_width,
         horizontal_padding = horizontal_padding,
         vertical_padding = vertical_padding)

    HorizontalDivider(modifier = Modifier
        .fillMaxWidth()
        .height(divider_thickness)
        .padding(top = vertical_padding))

    Barometer(weather_data = weather_data,
              is_larger_window = is_larger_window,
         divider_thickness = divider_thickness,
         icon_height = icon_height,
         icon_width = icon_width,
         horizontal_padding = horizontal_padding,
         vertical_padding = vertical_padding)
}

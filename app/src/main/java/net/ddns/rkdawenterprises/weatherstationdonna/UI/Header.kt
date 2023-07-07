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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.ddns.rkdawenterprises.davis_website.Weather_page
import net.ddns.rkdawenterprises.rkdawe_api_common.Utilities
import net.ddns.rkdawenterprises.rkdawe_api_common.Weather_data
import net.ddns.rkdawenterprises.weatherstationdonna.R
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.Main_typography
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.material_colors_extended
import java.time.LocalDateTime
import java.time.ZoneId

@Suppress("unused")
private const val LOG_TAG = "Header_composable";

@Composable
fun Header(weather_data_RKDAWE: Weather_data?,
           weather_data_davis: net.ddns.rkdawenterprises.davis_website.Weather_data?,
           weather_page: Weather_page?,
           modifier: Modifier = Modifier)
{
    val system_name = if(weather_data_RKDAWE != null)
    {
        weather_data_RKDAWE.system_name;
    }
    else if(weather_page != null)
    {
        weather_page.systemName;
    }
    else
    {
        stringResource(id = R.string.system_name_default);
    }

    val as_of = if(weather_data_RKDAWE != null)
    {
        "${stringResource(id = R.string.conditions_as_of_colon)} ${
            Utilities.convert_time_UTC_to_local(weather_data_RKDAWE.time,
                                                "h:mm a EEEE, MMM d, yyyy")
        }"
    }
    else if(weather_data_davis != null)
    {
        "${stringResource(id = R.string.conditions_as_of_colon)} ${
            Utilities.convert_timestamp_to_local(weather_data_davis.lastReceived,
                                                 weather_data_davis.timeZoneId,
                                                 "h:mm a EEEE, MMM d, yyyy")
        }"
    }
    else
    {
        "${stringResource(id = R.string.conditions_as_of_colon)} ${
            Utilities.convert_time_UTC_to_local(LocalDateTime.now().atZone(ZoneId.of("UTC")).toString(),
                                                "h:mm a EEEE, MMM d, yyyy")
        }"
    }

    Column(modifier = modifier) {
        BasicTextField(modifier = modifier.background(MaterialTheme.material_colors_extended.primaryVariant,
                                                      RectangleShape).fillMaxWidth().padding(top = 16.dp),
                       value = system_name,
                       onValueChange = {},
                       singleLine = true,
                       enabled = false,
                       textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center,
                                                               fontFamily = Main_typography.h6.fontFamily,
                                                               fontWeight = Main_typography.h6.fontWeight,
                                                               fontSize = Main_typography.h6.fontSize,
                                                               color = MaterialTheme.material_colors_extended.onPrimary));

        BasicTextField(modifier = modifier.background(MaterialTheme.material_colors_extended.primaryVariant,
                                                      RectangleShape).fillMaxWidth().padding(bottom = 16.dp),
                       value = as_of,
                       onValueChange = {},
                       singleLine = true,
                       enabled = false,
                       textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center,
                                                               fontFamily = Main_typography.subtitle1.fontFamily,
                                                               fontWeight = Main_typography.subtitle1.fontWeight,
                                                               fontSize = Main_typography.subtitle1.fontSize,
                                                               color = MaterialTheme.material_colors_extended.onPrimary));
    }
}
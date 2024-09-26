/*
 * Copyright (c) 2024 RKDAW Enterprises and Ralph Williamson.
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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.ddns.rkdawenterprises.rkdawe_api_common.Weather_data
import net.ddns.rkdawenterprises.weatherstationdonna.R
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.Typography

@Suppress("UNUSED_PARAMETER")
@Composable
fun Forecast(weather_data: Weather_data,
             is_larger_window: Boolean)
{
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

    // TODO: Fetch current location setting...
    val forecast_for_location_string = "Forecast for ${stringResource(id = R.string.forecast_location_setting_default)}"
        /*stringResource(R.string.forecast_for_location_template,
                                latitude,
                                longitude)*/

    Column(modifier = Modifier
        .padding(top = vertical_padding, start = horizontal_padding, end = horizontal_padding),
           verticalArrangement = Arrangement.spacedBy(vertical_padding))
    {
        BasicTextField(modifier = Modifier
            .background(MaterialTheme.colorScheme.tertiary,
                        RectangleShape)
            .fillMaxWidth()
            .padding(top = 5.dp,
                     bottom = 5.dp),
                       value = forecast_for_location_string,
                       onValueChange = {},
                       singleLine = true,
                       enabled = false,
                       textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center,
                                                               fontFamily = Typography.headlineSmall.fontFamily,
                                                               fontWeight = Typography.headlineSmall.fontWeight,
                                                               fontSize = Typography.headlineSmall.fontSize));
    }
}
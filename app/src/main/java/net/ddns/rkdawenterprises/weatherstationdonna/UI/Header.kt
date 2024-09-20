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
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.ddns.rkdawenterprises.rkdawe_api_common.Utilities
import net.ddns.rkdawenterprises.rkdawe_api_common.Weather_data
import net.ddns.rkdawenterprises.weatherstationdonna.R
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.Typography
import java.time.LocalDateTime
import java.time.ZoneId

@Suppress("unused")
private const val LOG_TAG = "Header_composable";

@Composable
fun Header(modifier: Modifier = Modifier,
           weather_data: Weather_data?,
           is_larger_window: Boolean)
{
    val system_name = if(weather_data != null)
    {
        weather_data.system_name;
    }
    else
    {
        stringResource(id = R.string.system_name_default);
    }

    val as_of = if(weather_data != null)
    {
        "${stringResource(id = R.string.conditions_as_of_colon)} ${
            try
            {
                Utilities.convert_time_UTC_to_local(weather_data.time,
                                                    "h:mm a EEEE, MMM d, yyyy")
            }
            catch(exception: java.time.format.DateTimeParseException)
            {
                stringResource(id = R.string.NA)
            }
        }"
    }
    else
    {
        "${stringResource(id = R.string.conditions_as_of_colon)} ${
            Utilities.convert_time_UTC_to_local(LocalDateTime.now()
                                                        .atZone(ZoneId.of("UTC"))
                                                        .toString(),
                                                "h:mm a EEEE, MMM d, yyyy")
        }"
    }

    val text_field_state = rememberTextFieldState(system_name)

    Column(modifier = modifier) {
        BasicTextField(modifier = modifier
                .background(MaterialTheme.colorScheme.secondary,
                            RectangleShape)
                .fillMaxWidth()
                .padding(top = 16.dp),
                       value = system_name,
                       onValueChange = {},
                       enabled = false,
                       textStyle =
                            if(!is_larger_window)
                            {
                                LocalTextStyle.current.copy(textAlign = TextAlign.Center,
                                                            fontFamily = Typography.titleMedium.fontFamily,
                                                            fontWeight = Typography.titleMedium.fontWeight,
                                                            fontSize = Typography.titleMedium.fontSize,
                                                            color = MaterialTheme.colorScheme.onPrimary)
                            }
                            else
                            {
                                LocalTextStyle.current.copy(textAlign = TextAlign.Center,
                                                            fontFamily = Typography.titleLarge.fontFamily,
                                                            fontWeight = Typography.titleLarge.fontWeight,
                                                            fontSize = Typography.titleLarge.fontSize,
                                                            color = MaterialTheme.colorScheme.onPrimary)
                            },
                       onTextLayout = { result: TextLayoutResult ->
                           // If the text is wrapping, then wrap it at the '@' character.
                           if((result.lineCount > 1) && text_field_state.text.contains('@') && !text_field_state.text.contains('\n'))
                           {
                               val start = text_field_state.text.indexOf('@')
                               text_field_state.edit {
                                   replace(start,
                                           start + 1,
                                           "\n@")
                               }
                           }
                       })

        BasicTextField(modifier = modifier
                .background(MaterialTheme.colorScheme.secondary,
                            RectangleShape)
                .fillMaxWidth()
                .padding(bottom = 16.dp),
                       value = as_of,
                       onValueChange = {},
                       singleLine = true,
                       enabled = false,
                       textStyle =
                           if(!is_larger_window)
                           {
                               LocalTextStyle.current.copy(textAlign = TextAlign.Center,
                                                           fontFamily = Typography.titleSmall.fontFamily,
                                                           fontWeight = Typography.titleSmall.fontWeight,
                                                           fontSize = Typography.titleSmall.fontSize,
                                                           color = MaterialTheme.colorScheme.onPrimary)
                           }
                           else
                           {
                               LocalTextStyle.current.copy(textAlign = TextAlign.Center,
                                                           fontFamily = Typography.titleMedium.fontFamily,
                                                           fontWeight = Typography.titleMedium.fontWeight,
                                                           fontSize = Typography.titleMedium.fontSize,
                                                           color = MaterialTheme.colorScheme.onPrimary)
                           }
                      );
    }
}
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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Divider
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.ddns.rkdawenterprises.rkdawe_api_common.Weather_data
import net.ddns.rkdawenterprises.weatherstationdonna.R
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.Main_typography
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.material_colors_extended

@Suppress("unused")
private const val LOG_TAG = "All_data_composable";

@Composable
fun All_data(weather_data_RKDAWE: Weather_data?)
{
    Column(modifier = Modifier,
           verticalArrangement = Arrangement.spacedBy(10.dp))
    {
        BasicTextField(modifier = Modifier
            .background(MaterialTheme.material_colors_extended.view_divider,
                        RectangleShape).fillMaxWidth(),
                       value = stringResource(id = R.string.all_data),
                       onValueChange = {},
                       singleLine = true,
                       enabled = false,
                       textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center,
                                                               fontFamily = Main_typography.h6.fontFamily,
                                                               fontWeight = Main_typography.h6.fontWeight,
                                                               fontSize = Main_typography.h6.fontSize,
                                                               color = MaterialTheme.material_colors_extended.text_default));

        if(weather_data_RKDAWE != null)
        {
            val array_of_strings = weather_data_RKDAWE.to_display_TSV_string()
                .split("\n").filter { it.isNotBlank() }

            val all_data = array_of_strings.map()
            { row ->
                row.split("\t");
            }

            Simple_table(columns = listOf(Data_column(weight = 0.55f),
                                          Data_column(weight = 0.45f)),
                         modifier = Modifier.fillMaxSize(),
                         separator = { Divider() },
                         space_between = 5.dp,
                         rows = all_data)
        }
        else
        {
            Text("Can't download data right now...")
        }
    }
}

@Composable
fun Simple_table(columns: List<Data_column>,
                 modifier: Modifier = Modifier,
                 separator: @Composable () -> Unit = {},
                 space_between: Dp = 5.dp,
                 rows: List<List<String>>)
{
    rows.forEachIndexed()
    { _, cells ->
        if(cells.isNotEmpty())
        {
            if(columns.size == cells.size)
            {
                Table_row(columns,
                          cells,
                          modifier,
                          space_between)
                separator()
            }
            else
            {
                if(cells.size == 1)
                {
                    Text(cells[0],
                         modifier,
                         style = Main_typography.h6,
                         textAlign = TextAlign.Center);
                }
            }
        }
    }
}

@Composable
fun Table_row(columns: List<Data_column>,
              cells: List<String>,
              modifier: Modifier = Modifier,
              space_between: Dp = 5.dp)
{
    Row(modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(space = space_between,
                                                     alignment = Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically)
    {
        columns.forEachIndexed()
        { index, data_column ->
            Text(Html.fromHtml(cells[index],
                               Html.FROM_HTML_MODE_COMPACT).toString(),
                 modifier = Modifier.weight(data_column.weight,
                                            fill = true),
                 style = data_column.style,
                 textAlign = data_column.alignment);
        }
    }
}

data class Data_column(val alignment: TextAlign = TextAlign.Start,
                       val weight: Float = 1f,
                       val style: TextStyle = Main_typography.subtitle1)

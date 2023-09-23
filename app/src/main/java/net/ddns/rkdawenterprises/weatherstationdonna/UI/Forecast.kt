/*
 * Copyright (c) 2023 RKDAW Enterprises and Ralph Williamson.
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

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.ddns.rkdawenterprises.weather_gov_api.Weather_gov_data
import net.ddns.rkdawenterprises.weatherstationdonna.R
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.Main_typography
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.material_colors_extended
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@Suppress("unused")
private const val LOG_TAG = "Forecast_composable";

@Composable
fun Forecast(weather_forecast: Weather_gov_data)
{
    val message = "${stringResource(id = R.string.forecast)} for ${weather_forecast.city}, ${weather_forecast.state}";
    Column(modifier = Modifier,
           verticalArrangement = Arrangement.spacedBy(10.dp)) {
        BasicTextField(modifier = Modifier
            .background(MaterialTheme.material_colors_extended.view_divider,
                        RectangleShape)
            .fillMaxWidth(),
                       value = message,
                       onValueChange = {},
                       singleLine = true,
                       enabled = false,
                       textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center,
                                                               fontFamily = Main_typography.h6.fontFamily,
                                                               fontWeight = Main_typography.h6.fontWeight,
                                                               fontSize = Main_typography.h6.fontSize,
                                                               color = MaterialTheme.material_colors_extended.text_default));

        Daily_forecast(modifier = Modifier,
                       weather_forecast = weather_forecast)
    }
}

@Composable
fun Daily_forecast(modifier: Modifier,
                   weather_forecast: Weather_gov_data)
{
    val temperature_unit = weather_forecast.gridpoints?.properties?.temperature?.uom;
    val temperature_values = weather_forecast.gridpoints?.properties?.temperature?.values;

    if((temperature_unit == null) || (temperature_values == null) || (temperature_values.size < 10)) return;

    val points: MutableList<Point_and_time> = mutableListOf();
    val convert_to_F = (temperature_unit == "wmoUnit:degC");
    var temperature_min: BigDecimal? = null;
    var temperature_max: BigDecimal? = null;
    var date_time_min: ZonedDateTime? = null;
    var date_time_max: ZonedDateTime? = null;
    for(temperature_value in temperature_values)
    {
        val temperature = temperature_value.value?.toBigDecimal();
        val data_time_string = temperature_value.validTime;
        if((temperature != null) && (data_time_string != null))
        {
            val converted: BigDecimal =
                if(convert_to_F)
                {
                    convert_Celsius_to_Fahrenheit(temperature).setScale(4, RoundingMode.HALF_EVEN);
                }
                else
                {
                    temperature.setScale(4, RoundingMode.HALF_EVEN);
                }

            val date_time = if(data_time_string.contains('/'))
            {
                ZonedDateTime.parse(data_time_string.split('/')[0])
            }
            else
            {
                ZonedDateTime.parse(data_time_string);
            }

            points.add(Point_and_time(converted,
                                      date_time));

            if((temperature_min == null) || (converted < temperature_min)) temperature_min = converted;
            if((temperature_max == null) || (converted > temperature_max)) temperature_max = converted;
            if((date_time_min == null) || (date_time < date_time_min)) date_time_min = date_time;
            if((date_time_max == null) || (date_time > date_time_max)) date_time_max = date_time;
        }
    }

    if((temperature_min != null) && (temperature_max != null) && (date_time_min != null) && (date_time_max != null))
    {
        Log.d(LOG_TAG, ">>>$temperature_min - $temperature_max, $date_time_min to $date_time_max");

        val x_axis_resolution = ChronoUnit.DAYS;
        val y_axis_resolution = 5;
        val line_graph_data = Line_time_graph_data(points,
                                                   date_time_min,
                                                   date_time_max,
                                                   temperature_min,
                                                   temperature_max);
        Line_time_graph(modifier = modifier,
                        line_graph_data = line_graph_data,
                        x_axis_resolution = x_axis_resolution,
                        y_axis_resolution = y_axis_resolution,
                        y_axis_number_increments_minimum = 5,
                        x_axis_increment_size = 30.dp,
                        y_axis_padding = 10.dp,
                        y_height = 150.dp)
    }
}

fun convert_Celsius_to_Fahrenheit(c: BigDecimal): BigDecimal
{
    val conversion_factor = BigDecimal("9.0").divide(BigDecimal("5.0"),16, RoundingMode.HALF_EVEN);
    val intermediate = c.multiply(conversion_factor);
    val converted = intermediate.add(BigDecimal("32.0"));
    return converted;
}
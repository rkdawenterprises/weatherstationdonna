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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Suppress("unused")
private const val LOG_TAG = "Line_time_graph_composable";

data class Line_time_graph_data(val points: List<Point_and_time>,
                                val time_min: ZonedDateTime,
                                val time_max: ZonedDateTime,
                                val y_min: BigDecimal,
                                val y_max: BigDecimal)

data class Point_and_time(val value: BigDecimal,
                          val date_time: ZonedDateTime)

@Composable
fun Line_time_graph(modifier: Modifier,
                    line_graph_data: Line_time_graph_data,
                    x_axis_resolution: ChronoUnit,
                    x_axis_increment_size: Dp,
                    y_axis_resolution: Int,
                    y_axis_number_increments_minimum: Int,
                    y_height: Dp,
                    y_axis_padding: Dp,
                    background_color: Color = Color.White)
{
    if((x_axis_resolution != ChronoUnit.DAYS) && (x_axis_resolution != ChronoUnit.HOURS)) return;

    val scope = rememberCoroutineScope();

    Surface(modifier = modifier)
    {
        with(line_graph_data) {
            val scroll_offset = remember { mutableFloatStateOf(0f) }
            val maximum_scroll_offset = remember { mutableFloatStateOf(0f) }
            val scrollable_state = rememberScrollableState()
            { delta ->
                scroll_offset.value -= delta
                scroll_offset.value = when
                {
                    (scroll_offset.value < 0f) -> 0f
                    (scroll_offset.value > maximum_scroll_offset.value) -> maximum_scroll_offset.value
                    else -> scroll_offset.value
                }
                delta
            }

            var y_axis_labels_area_width by remember { mutableStateOf(0.dp) }
            var y_axis_line_area_width by remember { mutableStateOf(0.dp) }

            Box(modifier = modifier.clipToBounds())
            {
                // Make a list of x-axis and y-axis labels.
                val y_axis_label_list = y_axis_labels(line_graph_data.y_min,
                                                      line_graph_data.y_max,
                                                      y_axis_resolution,
                                                      y_axis_number_increments_minimum)

                val x_axis_label_list = x_axis_labels(line_graph_data.time_min,
                                                      line_graph_data.time_max,
                                                      x_axis_resolution)


                // Need to determine y-axis width and x-axis height to know how large the unused area in the lower left corner is.

                // Draw y-axis.
                Column(modifier = modifier.clipToBounds()) {
                    Canvas(modifier = modifier
                        .clipToBounds()
                        .width(y_axis_labels_area_width + y_axis_line_area_width + (y_axis_padding * 2))
                        .height(y_height)
                        .background(background_color)) {

                    }
                }


                // Draw line graph.
            }
        }
    }
}

fun x_axis_labels(minimum: ZonedDateTime,
                  maximum: ZonedDateTime,
                  resolution: ChronoUnit): Array<String>
{
    val increments = (resolution.between(minimum,
                                         maximum) + 1).toInt();
    val start = minimum.truncatedTo(resolution);
    val array = ArrayList<String>();
    val hour_formater = DateTimeFormatter.ofPattern("hh a");
    val day_formater = DateTimeFormatter.ofPattern("EE");
    var value = start;
    for(i in 0 until increments)
    {
        val value_string = if((i == 0) && (resolution == ChronoUnit.DAYS))
        {
            "TODAY ${value.dayOfMonth}";
        }
        else if(resolution == ChronoUnit.DAYS)
        {
            "${day_formater.format(value).uppercase()} ${value.dayOfMonth}";
        }
        else
        {
            hour_formater.format(value);
        }

        array.add(value_string);
        value = value.plus(1,
                           resolution);
    }

    return array.toTypedArray();
}

fun y_axis_labels(minimum: BigDecimal,
                  maximum: BigDecimal,
                  resolution: Int,
                  increments_minimum: Int): Array<String>
{
    val minimum_rounded = round_to_multiple_of(minimum,
                                               resolution,
                                               RoundingMode.FLOOR);
    val maximum_rounded = round_to_multiple_of(maximum,
                                               resolution,
                                               RoundingMode.CEILING);
    val delta = maximum_rounded - minimum_rounded;
    val increments = ((delta) / resolution) + 1;
    val array = ArrayList<String>();
    return if(increments >= increments_minimum)
    {
        for(value in minimum_rounded..maximum_rounded step resolution)
        {
            array.add(value.toString());
        }

        array.toTypedArray();
    }
    else
    {
        val increment = BigDecimal(delta).divide(BigDecimal(increments_minimum),
                                                 4,
                                                 RoundingMode.HALF_EVEN);
        var value = BigDecimal(minimum_rounded);
        for(i in 0..increments_minimum)
        {
            val value_string = value.setScale(1,
                                              RoundingMode.HALF_EVEN).stripTrailingZeros().toPlainString();
            array.add(value_string);
            value = value.add(increment);
        }

        array.toTypedArray();
    }
}

fun round_to_multiple_of(value: BigDecimal,
                         multiple: Int,
                         mode: RoundingMode = RoundingMode.UP): Int
{
    val big_multiple = BigDecimal(multiple);
    val half_big_multiple = big_multiple.divide(BigDecimal("2.0"),
                                                4,
                                                RoundingMode.HALF_EVEN);
    val modulus = value.remainder(big_multiple).abs();
    val big_muliple_minus_modulus = big_multiple.minus(modulus);

    if(modulus.compareTo(BigDecimal.ZERO) == 0)
    {
        return value.toInt();
    }

    return if(value.signum() > 0)
    {
        when(mode)
        {
            //  Away from zero, or towards positive infinity.
            RoundingMode.UP, RoundingMode.CEILING -> (value.plus(big_muliple_minus_modulus)).toInt();

            // Towards zero, or towards negative infinity.
            RoundingMode.DOWN, RoundingMode.FLOOR -> (value.minus(modulus)).toInt();

            // Towards "nearest neighbor" unless both neighbors are equidistant, in which case round away from zero.
            RoundingMode.HALF_UP ->
            {
                val x = if(modulus < half_big_multiple)
                {
                    value.minus(modulus).toInt();
                }
                else
                {
                    value.plus(big_multiple.minus(modulus)).toInt();
                }

                x;
            }

            // Towards "nearest neighbor" unless both neighbors are equidistant, in which case round towards zero.
            RoundingMode.HALF_DOWN ->
            {
                val x = if(modulus <= half_big_multiple)
                {
                    value.minus(modulus).toInt();
                }
                else
                {
                    value.plus(big_multiple.minus(modulus)).toInt();
                }

                x;
            }

            // Towards the "nearest neighbor" unless both neighbors are equidistant, in which case round towards the even neighbor.
            RoundingMode.HALF_EVEN ->
            {
                val rounded_up = value.plus(big_multiple.minus(modulus)).toInt();
                val rounded_down = value.minus(modulus).toInt();
                val round_down_is_even = rounded_down % 2 == 0;
                val round_up_is_even = rounded_up % 2 == 0;

                val x = if((modulus.compareTo(half_big_multiple) == 0) && (round_down_is_even))
                {
                    rounded_down;
                }
                else if((modulus.compareTo(half_big_multiple) == 0) && (round_up_is_even))
                {
                    rounded_up;
                }
                else if(modulus < half_big_multiple)
                {
                    rounded_down;
                }
                else
                {
                    rounded_up;
                }

                x;
            }

            else ->
            {
                throw (ArithmeticException());
            }
        }
    }
    else
    {
        when(mode)
        {
            //  Towards zero, or towards positive infinity.
            RoundingMode.DOWN, RoundingMode.CEILING -> (value.plus(modulus)).toInt();

            // Away from zero, or towards negative infinity.
            RoundingMode.UP, RoundingMode.FLOOR -> (value.minus(big_muliple_minus_modulus)).toInt();

            // Towards "nearest neighbor" unless both neighbors are equidistant, in which case round away from zero.
            RoundingMode.HALF_UP ->
            {
                val x = if(modulus < half_big_multiple)
                {
                    value.plus(modulus).toInt();
                }
                else
                {
                    value.minus(big_multiple.minus(modulus)).toInt();
                }

                x;
            }

            // Towards "nearest neighbor" unless both neighbors are equidistant, in which case round towards zero.
            RoundingMode.HALF_DOWN ->
            {
                val x = if(modulus <= half_big_multiple)
                {
                    value.plus(modulus).toInt();
                }
                else
                {
                    value.minus(big_multiple.minus(modulus)).toInt();
                }

                x;
            }

            // Towards the "nearest neighbor" unless both neighbors are equidistant, in which case round towards the even neighbor.
            RoundingMode.HALF_EVEN ->
            {
                val rounded_down = value.plus(modulus).toInt();
                val rounded_up = value.minus(big_multiple.minus(modulus)).toInt();
                val round_down_is_even = rounded_down % 2 == 0;
                val round_up_is_even = rounded_up % 2 == 0;

                val x = if((modulus.compareTo(half_big_multiple) == 0) && (round_down_is_even))
                {
                    rounded_down;
                }
                else if((modulus.compareTo(half_big_multiple) == 0) && (round_up_is_even))
                {
                    rounded_up;
                }
                else if(modulus < half_big_multiple)
                {
                    rounded_down;
                }
                else
                {
                    rounded_up;
                }

                x;
            }

            else ->
            {
                throw (ArithmeticException());
            }
        }
    }
}

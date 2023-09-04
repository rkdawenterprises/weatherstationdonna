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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.math.BigDecimal
import java.time.Duration
import java.time.ZonedDateTime

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
                    x_axis_resolution: Duration,
                    y_axis_resolution: BigDecimal,
                    x_axis_increments: Int,
                    y_axis_increments: Int,
                    x_axis_increment_size: Dp,
                    y_height: Dp,
                    y_axis_padding: Dp,
                    background_color: Color = Color.White)
{
    val scope = rememberCoroutineScope();

    Surface(modifier = modifier) {
        with(line_graph_data) {
            val scrollOffset = remember { mutableFloatStateOf(0f) }
            val maxScrollOffset = remember { mutableFloatStateOf(0f) }
            val scrollState = rememberScrollableState() { delta ->
                scrollOffset.value -= delta
                scrollOffset.value = when
                {
                    (scrollOffset.value < 0f)                    -> 0f
                    (scrollOffset.value > maxScrollOffset.value) -> maxScrollOffset.value
                    else                                         -> scrollOffset.value
                }
                delta
            }

            var columnWidth by remember { mutableFloatStateOf(0f) }

            var y_axis_labels_area_width by remember { mutableStateOf(0.dp) }
            var y_axis_line_area_width by remember { mutableStateOf(0.dp) }

            Box(modifier = modifier.clipToBounds())
            {
                // Make a list of x-axis and y-axis labels.
                var y_value = line_graph_data.y_min;
                val y_axis_label_list: MutableList<BigDecimal> = mutableListOf<BigDecimal>();
                for(i in 0..y_axis_increments - 1)
                {
                    y_axis_label_list.add(y_value);
                    y_value += y_axis_resolution;
                }

                Log.d(LOG_TAG, "${y_axis_label_list.size}")
                
                // Need to determine y-axis width and x-axis height to know how large the unused area in the lower left corner is.

                // Draw y-axis.
                Column(modifier = modifier.clipToBounds())
                {
                    Canvas(modifier = modifier.clipToBounds()
                        .width(y_axis_labels_area_width + y_axis_line_area_width + (y_axis_padding * 2))
                        .height(y_height)
                        .background(background_color))
                    {

                    }
                }





                // Draw line graph.
            }
        }
    }
}

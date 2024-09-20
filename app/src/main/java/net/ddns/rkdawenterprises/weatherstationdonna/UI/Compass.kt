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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import net.ddns.rkdawenterprises.weatherstationdonna.R
import kotlin.math.cos
import kotlin.math.sin

@Suppress("unused")
private const val LOG_TAG = "Compass_composable";

@Composable
fun Compass(modifier: Modifier,
            angle: Int,
            show_outline: Boolean = true,
            outline_color: Color = Color.Black,
            show_markers: Boolean = true,
            marker_color: Color = outline_color,
            marker_degrees_step: Int = 15,
            show_labels: Boolean = true,
            label_color: Color = outline_color)
{
    Box(modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
        val text_measurer = rememberTextMeasurer();

        val n = stringResource(id = R.string.n);
        val s = stringResource(id = R.string.s);
        val e = stringResource(id = R.string.e);
        val w = stringResource(id = R.string.w);

        Canvas(modifier.fillMaxSize()) {
            val diameter = if(size.height > size.width) size.width else size.height;
            val radius = diameter / 2;

            val stroke_width = diameter * 0.02f;

            val center_x = size.width / 2;
            val center_y = size.height / 2;

            if(show_outline)
            {
                drawCircle(color = outline_color,
                           radius = radius,
                           center = Offset(center_x,
                                           center_y),
                           style = Stroke(width = stroke_width))
            }

            val marker_end_radius = radius;

            for(i in 0..359 step marker_degrees_step)
            {
                val angle_in_radians = Math.toRadians(i.toDouble());
                val sine_of_angle = sin(angle_in_radians);
                val cosine_of_angle = cos(angle_in_radians);

                val marker_end_x = center_x + (marker_end_radius * sine_of_angle);
                val marker_end_y = center_y + (marker_end_radius * cosine_of_angle);

                val marker_start_radius = if((i % 90) == 0)
                {
                    marker_end_radius * 0.8f;
                }
                else
                {
                    marker_end_radius * 0.9f;
                }

                val marker_start_x = center_x + (marker_start_radius * sine_of_angle);
                val marker_start_y = center_y + (marker_start_radius * cosine_of_angle);

                if(show_markers)
                {
                    drawLine(start = Offset(x = marker_start_x.toFloat(),
                                            y = marker_start_y.toFloat()),
                             end = Offset(x = marker_end_x.toFloat(),
                                          y = marker_end_y.toFloat()),
                             color = marker_color,
                             strokeWidth = stroke_width);
                }

                if(show_labels)
                {
                    if((i % 90) == 0)
                    {
                        val label_radius = marker_end_radius * 0.65f;
                        val label_x = center_x + (label_radius * sine_of_angle);
                        val label_y = center_y + (label_radius * cosine_of_angle);

                        val label = when(i)
                        {
                            0 -> s;
                            90 -> e;
                            180 -> n;
                            270 -> w;
                            else -> "";
                        }

                        val measured_text = text_measurer.measure(AnnotatedString(label),
                                                                  style = TextStyle(fontSize = (diameter * 0.04f).sp,
                                                                                    fontWeight = FontWeight.Bold));
                        val label_size = measured_text.size.toSize();

                        drawText(measured_text,
                                 color = label_color,
                                 topLeft = Offset(x = (label_x - (label_size.width / 2)).toFloat(),
                                                  y = (label_y - (label_size.height / 2)).toFloat()))
                    }
                }
            }
        }

        Image(painter = painterResource(id = R.drawable.compass_needle),
              contentDescription = "Compass needle",
              modifier = Modifier.fillMaxSize(fraction = 0.9f)
                  .graphicsLayer(rotationZ = angle.toFloat()),
              contentScale = ContentScale.Fit)
    }
}

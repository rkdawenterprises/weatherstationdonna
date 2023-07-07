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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Suppress("unused")
private const val LOG_TAG = "Circular_progress_bar_composable";

@Suppress("unused")
@Composable
fun Circular_progress_bar(modifier: Modifier = Modifier,
                          percentage: Float,
                          fillColor: Color,
                          backgroundColor: Color,
                          strokeWidth: Dp)
{
    Canvas(modifier = modifier
        .size(150.dp)
        .padding(10.dp))
    {
        drawArc(color = backgroundColor,
                140f,
                260f,
                false,
                style = Stroke(strokeWidth.toPx(),
                               cap = StrokeCap.Round),
                size = Size(size.width,
                            size.height))

        drawArc(color = fillColor,
                140f,
                percentage * 260f,
                false,
                style = Stroke(strokeWidth.toPx(),
                               cap = StrokeCap.Round),
                size = Size(size.width,
                            size.height))


        val angleInDegrees = (percentage * 260.0) + 50.0
        val radius = (size.height / 2)
        val x = -(radius * sin(Math.toRadians(angleInDegrees))).toFloat() + (size.width / 2)
        val y = (radius * cos(Math.toRadians(angleInDegrees))).toFloat() + (size.height / 2)

        drawCircle(color = Color.White,
                   radius = 5f,
                   center = Offset(x,
                                   y))
    }
}

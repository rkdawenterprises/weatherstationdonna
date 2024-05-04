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
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import net.ddns.rkdawenterprises.weatherstationdonna.UI.ycharts.AxisData
import net.ddns.rkdawenterprises.weatherstationdonna.UI.ycharts.GridLines
import net.ddns.rkdawenterprises.weatherstationdonna.UI.ycharts.Line
import net.ddns.rkdawenterprises.weatherstationdonna.UI.ycharts.LineChartData
import net.ddns.rkdawenterprises.weatherstationdonna.UI.ycharts.LinePlotData
import net.ddns.rkdawenterprises.weatherstationdonna.UI.ycharts.LineStyle
import net.ddns.rkdawenterprises.weatherstationdonna.UI.ycharts.LineType
import net.ddns.rkdawenterprises.weatherstationdonna.UI.ycharts.Point
import net.ddns.rkdawenterprises.weatherstationdonna.UI.ycharts.RowClip
import net.ddns.rkdawenterprises.weatherstationdonna.UI.ycharts.XAxis
import net.ddns.rkdawenterprises.weatherstationdonna.UI.ycharts.YAxis
import net.ddns.rkdawenterprises.weatherstationdonna.UI.ycharts.drawGridLines
import net.ddns.rkdawenterprises.weatherstationdonna.UI.ycharts.getMaxElementInYAxis2
import net.ddns.rkdawenterprises.weatherstationdonna.UI.ycharts.getXAxisScale
import net.ddns.rkdawenterprises.weatherstationdonna.UI.ycharts.getYAxisScale2
import net.ddns.rkdawenterprises.weatherstationdonna.UI.ycharts.y_axis_labels
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@Suppress("unused")
private const val LOG_TAG = "Line_time_graph_composable";

data class Point_and_time(val value: BigDecimal,
                          val date_time: ZonedDateTime)

data class Line_time_graph_data(val points: List<Point_and_time>,
                                val time_min: ZonedDateTime,
                                val time_max: ZonedDateTime,
                                val y_min: BigDecimal,
                                val y_max: BigDecimal)

@Composable
fun Line_time_graph(modifier: Modifier,
                    temperature_data: List<Point>,
                    line_graph_data: Line_time_graph_data,
                    x_axis_resolution: ChronoUnit,
                    x_axis_increment_size: Dp = 30.dp,
                    y_axis_resolution: Int,
                    y_axis_number_increments_minimum: Int = 5,
                    y_axis_padding: Dp = 10.dp,
                    background_color: Color = Color.White)
{
    val (y_axis_label_list, y_axis_label_minimum, y_axis_label_maximum, y_axis_increments) =
        y_axis_labels(line_graph_data.y_min,
                      line_graph_data.y_max,
                      y_axis_resolution,
                      y_axis_number_increments_minimum);

    val xAxisData1 = AxisData.Builder().axisStepSize(30.dp).steps(temperature_data.size - 1).labelData { i -> i.toString() }
        .labelAndAxisLinePadding(15.dp).build()
    val yAxisData1 = AxisData.Builder().steps(y_axis_increments - 1).labelAndAxisLinePadding(20.dp).build()
    val lineChartData = LineChartData(linePlotData = LinePlotData(lines = listOf(
        Line(dataPoints = temperature_data,
             lineStyle = LineStyle(lineType = LineType.CUBIC,
                                   color = Color.Blue)))),
                             xAxisData = xAxisData1,
                             yAxisData = yAxisData1,
                             gridLines = GridLines())

    Surface(modifier = modifier) {
        with(lineChartData) {
            var columnWidth by remember { mutableStateOf(0f) }
            var rowHeight by remember { mutableStateOf(0f) }
            var xOffset by remember { mutableStateOf(0f) }
            val bgColor = MaterialTheme.colorScheme.surface
            val linePoints: List<Point> = linePlotData.lines.flatMap { line -> line.dataPoints.map { it } }

            val (xMin, xMax, xAxisScale) = getXAxisScale(linePoints,
                                                         xAxisData.steps)
            val (yMin, _, yAxisScale) = getYAxisScale2(linePoints,
                                                       yAxisData.steps)
            val maxElementInYAxis = getMaxElementInYAxis2(yAxisScale,
                                                          yAxisData.steps)
            val xAxisData = xAxisData.copy(axisBottomPadding = bottomPadding)
            val yAxisData = yAxisData.copy(axisBottomPadding = LocalDensity.current.run { rowHeight.toDp() },
                                           axisTopPadding = paddingTop)

            val scrollOrientation: Orientation = Orientation.Horizontal
            val scrollOffset = remember { mutableStateOf(0f) }
            val maxScrollOffset = remember { mutableStateOf(0f) }
            val scrollState = rememberScrollableState { delta ->
                scrollOffset.value -= delta
                scrollOffset.value = checkAndGetMaxScrollOffset(scrollOffset.value,
                                                                maxScrollOffset.value)
                delta
            }

            Box(modifier = modifier.clipToBounds()) {
                Canvas(modifier = modifier
                    .align(Alignment.Center)
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .scrollable(state = scrollState,
                                scrollOrientation,
                                enabled = true),
                       onDraw = {
                           xOffset = xAxisData.axisStepSize.toPx()
                           maxScrollOffset.value = getMaxScrollDistance(columnWidth,
                                                                        xMax,
                                                                        xMin,
                                                                        xOffset,
                                                                        paddingRight.toPx(),
                                                                        size.width,
                                                                        containerPaddingEnd.toPx())

                           linePlotData.lines.forEach { line ->
                               val yBottom = size.height - rowHeight
                               val yOffset = ((yBottom - paddingTop.toPx()) / maxElementInYAxis)
                               xOffset = xAxisData.axisStepSize.toPx()
                               val xLeft = columnWidth // To add extra space if needed
                               val pointsData = getMappingPointsToGraph(line.dataPoints,
                                                                        xMin,
                                                                        xOffset,
                                                                        xLeft,
                                                                        scrollOffset.value,
                                                                        yBottom,
                                                                        yMin,
                                                                        yOffset)
                               val (cubicPoints1, cubicPoints2) = getCubicPoints(pointsData)

                               // Draw guide lines
                               gridLines?.let {
                                   drawGridLines(yBottom,
                                                 yAxisData.axisTopPadding.toPx(),
                                                 xLeft,
                                                 paddingRight,
                                                 scrollOffset.value,
                                                 pointsData.size,
                                                 xAxisScale,
                                                 yAxisData.steps,
                                                 xAxisData.axisStepSize,
                                                 it)
                               }

                               // Draw cubic line using the points and form a line graph
                               val cubicPath = drawStraightOrCubicLine(pointsData,
                                                                       cubicPoints1,
                                                                       cubicPoints2,
                                                                       line.lineStyle)

                               // Draw column to make graph look scrollable under Yaxis
                               drawUnderScrollMask(columnWidth,
                                                   paddingRight,
                                                   bgColor)
                           }
                       })

                YAxis(modifier = Modifier
                    .fillMaxHeight()
                    .onGloballyPositioned {
                        columnWidth = it.size.width.toFloat()
                    },
                      yAxisData = yAxisData,
                      label_list = y_axis_label_list)

                XAxis(xAxisData = xAxisData,
                      modifier = Modifier
                          .fillMaxWidth()
                          .wrapContentHeight()
                          .align(Alignment.BottomStart)
                          .onGloballyPositioned {
                              rowHeight = it.size.height.toFloat()
                          }
                          .clip(RowClip(columnWidth,
                                        paddingRight)),
                      xStart = columnWidth,
                      scrollOffset = scrollOffset.value,
                      chartData = linePoints,
                      axisStart = columnWidth,
                      minimum = line_graph_data.time_min,
                      maximum = line_graph_data.time_max,
                      resolution = x_axis_resolution)
            }
        }
    }


}

/**
 *
 * returns the list of transformed points supported to be drawn on the container using the input points .
 * @param lineChartPoints :Input data points
 * @param xMin: Min X-Axis value.
 * @param xOffset : Total distance between two X-Axis points.
 * @param xLeft: Total left padding in X-Axis.
 * @param scrollOffset : Total scrolled offset.
 * @param yBottom : Bottom start offset for X-Axis.
 * @param yMin : Min Y-Axis value.
 * @param yOffset : Distance between two Y-Axis points.
 */
fun getMappingPointsToGraph(
    lineChartPoints: List<Point>,
    xMin: Float,
    xOffset: Float,
    xLeft: Float,
    scrollOffset: Float,
    yBottom: Float,
    yMin: Float,
    yOffset: Float,
                           ): MutableList<Offset>
{
    val pointsData = mutableListOf<Offset>()
    lineChartPoints.forEachIndexed { _, point ->
        val (x, y) = point
        val x1 = ((x - xMin) * xOffset) + xLeft - scrollOffset
        val y1 = yBottom - ((y - yMin) * yOffset)
        pointsData.add(Offset(x1,
                              y1))
    }
    return pointsData
}

/**
 *
 * returns the max scrollable distance based on the points to be drawn along with padding etc.
 * @param columnWidth : Width of the Y-Axis.
 * @param xMax : Max X-Axis value.
 * @param xMin: Min X-Axis value.
 * @param xOffset: Total distance between two X-Axis points.
 * @param paddingRight : Padding at the end of the canvas.
 * @param canvasWidth : Total available canvas width.
 * @param containerPaddingEnd : Container inside padding end after the last point of the graph.
 */
fun getMaxScrollDistance(columnWidth: Float,
                         xMax: Float,
                         xMin: Float,
                         xOffset: Float,
                         paddingRight: Float,
                         canvasWidth: Float,
                         containerPaddingEnd: Float = 0f): Float
{
    val xLastPoint = (xMax - xMin) * xOffset + columnWidth + paddingRight + containerPaddingEnd
    return if(xLastPoint > canvasWidth)
    {
        xLastPoint - canvasWidth
    }
    else 0f
}

/**
 *
 * DrawScope.drawStraightOrCubicLine extension method used for drawing a straight/cubic line for a given Point(x,y).
 * @param pointsData : List of points to be drawn on the canvas
 * @param cubicPoints1 : List of average left side values for a given Point(x,y).
 * @param cubicPoints2 : List of average right side values for a given Point(x,y).
 * @param lineStyle : All styles related to the path are included in [LineStyle].
 */
fun DrawScope.drawStraightOrCubicLine(pointsData: MutableList<Offset>,
                                      cubicPoints1: MutableList<Offset>,
                                      cubicPoints2: MutableList<Offset>,
                                      lineStyle: LineStyle): Path
{
    val path = Path()
    path.moveTo(pointsData.first().x,
                pointsData.first().y)
    for(i in 1 until pointsData.size)
    {
        when(lineStyle.lineType)
        {
            LineType.STRAIGHT ->
            {
                path.lineTo(pointsData[i].x,
                            pointsData[i].y)
            }

            LineType.CUBIC ->
            {
                path.cubicTo(cubicPoints1[i - 1].x,
                             cubicPoints1[i - 1].y,
                             cubicPoints2[i - 1].x,
                             cubicPoints2[i - 1].y,
                             pointsData[i].x,
                             pointsData[i].y)
            }
        }
    }
    with(lineStyle) {
        drawPath(path,
                 color = color,
                 style = lineStyle.style,
                 alpha = alpha,
                 colorFilter = colorFilter,
                 blendMode = blendMode)
    }
    return path
}

/**
 *
 * Returns the Drawstyle for the path.
 * @param lineType : Type of the line [LineType]
 * @param lineStyle : The style for the path [lineStyle]
 */
internal fun getDrawStyleForPath(lineType: LineType,
                                 lineStyle: LineStyle): DrawStyle =
    lineStyle.style

/**
 *
 * DrawScope.drawUnderScrollMask extension method used  for drawing a rectangular mask to make graph scrollable under the YAxis.
 * @param columnWidth : Width of the rectangular mask here width of Y Axis is used.
 * @param paddingRight : Padding given at the end of the graph.
 * @param bgColor : Background of the rectangular mask.
 */
private fun DrawScope.drawUnderScrollMask(columnWidth: Float,
                                          paddingRight: Dp,
                                          bgColor: Color)
{
    drawRect(bgColor,
             Offset(0f,
                    0f),
             Size(columnWidth,
                  size.height))
    drawRect(bgColor,
             Offset(size.width - paddingRight.toPx(),
                    0f),
             Size(paddingRight.toPx(),
                  size.height))
}

/**
 *
 * getCubicPoints method provides left and right average value for a given point to get a smooth curve.
 * @param pointsData : List of the points on the Line graph.
 */
fun getCubicPoints(pointsData: List<Offset>): Pair<MutableList<Offset>, MutableList<Offset>>
{
    val cubicPoints1 = mutableListOf<Offset>()
    val cubicPoints2 = mutableListOf<Offset>()

    for(i in 1 until pointsData.size)
    {
        cubicPoints1.add(Offset((pointsData[i].x + pointsData[i - 1].x) / 2,
                                pointsData[i - 1].y))
        cubicPoints2.add(Offset((pointsData[i].x + pointsData[i - 1].x) / 2,
                                pointsData[i].y))
    }
    return Pair(cubicPoints1,
                cubicPoints2)
}

/**
 * Returns the scroll state within the start and computed max scrollOffset & filters invalid scroll states.
 * @param currentScrollOffset: Current scroll offset when user trying to scroll the canvas.
 * @param computedMaxScrollOffset: Maximum calculated scroll offset for given data set.
 */
fun checkAndGetMaxScrollOffset(currentScrollOffset: Float,
                               computedMaxScrollOffset: Float): Float
{
    return when
    {
        currentScrollOffset < 0f -> 0f
        currentScrollOffset > computedMaxScrollOffset -> computedMaxScrollOffset
        else -> currentScrollOffset
    }
}

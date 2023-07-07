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
               "unused")

package net.ddns.rkdawenterprises.weatherstationdonna.UI.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import net.ddns.rkdawenterprises.weatherstationdonna.R

/**
 * https://fonts.google.com/specimen/Montserrat
 */
private val Montserrat = FontFamily(Font(R.font.montserrat_regular),
                                    Font(R.font.montserrat_medium, FontWeight.W500),
                                    Font(R.font.montserrat_semibold, FontWeight.W600))

/**
 * https://fonts.google.com/specimen/Domine
 */
private val Domine = FontFamily(Font(R.font.domine_regular),
    Font(R.font.domine_bold, FontWeight.Bold));

/**
 * https://fonts.google.com/specimen/Roboto+Condensed
 */
private val roboto_condensed = FontFamily(Font(R.font.roboto_condensed_regular),
                                         Font(R.font.roboto_condensed_bold, FontWeight.Bold),
                                         Font(R.font.roboto_condensed_light, FontWeight.Light),
                                         Font(resId = R.font.roboto_condensed_italic,
                                              weight = FontWeight.Normal,
                                              style = FontStyle.Italic));

val Main_typography = Typography(
    h3 = TextStyle(
        fontFamily = roboto_condensed,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp
    ),
    h4 = TextStyle(
        fontFamily = roboto_condensed,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp
    ),
    h5 = TextStyle(
        fontFamily = roboto_condensed,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    h6 = TextStyle(
        fontFamily = roboto_condensed,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    subtitle1 = TextStyle(
        fontFamily = roboto_condensed,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),
    subtitle2 = TextStyle(
        fontFamily = roboto_condensed,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    body1 = TextStyle(
        fontFamily = roboto_condensed,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    body2 = TextStyle(
        fontFamily = roboto_condensed,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    button = TextStyle(
        fontFamily = roboto_condensed,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = roboto_condensed,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    overline = TextStyle(
        fontFamily = roboto_condensed,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
)

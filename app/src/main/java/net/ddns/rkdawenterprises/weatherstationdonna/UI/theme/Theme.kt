@file:Suppress("PackageName")

package net.ddns.rkdawenterprises.weatherstationdonna.UI.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColors(
    primary = Red700,
    primaryVariant = Red900,
    onPrimary = Color.White,
    secondary = Red700,
    secondaryVariant = Red900,
    onSecondary = Color.White,
    error = Red800
)

private val DarkColors = darkColors(
    primary = Red300,
    primaryVariant = Red700,
    onPrimary = Color.Black,
    secondary = Red300,
    onSecondary = Color.Black,
    error = Red200
)

@Composable
fun WeatherStationDonnaTheme(
    dark_theme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (dark_theme) DarkColors else LightColors,
        typography = WeatherStationDonnaTypography,
        shapes = WeatherStationDonnaShapes,
        content = content
    )
}

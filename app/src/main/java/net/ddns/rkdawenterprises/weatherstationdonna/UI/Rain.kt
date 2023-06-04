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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.ddns.rkdawenterprises.rkdawe_api_common.Weather_data
import net.ddns.rkdawenterprises.weatherstationdonna.R
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.Main_typography

@Suppress("unused")
private const val LOG_TAG = "Rain_composable";

@Composable
fun Rain(weather_data_RKDAWE: Weather_data?,
         weather_data_davis: net.ddns.rkdawenterprises.davis_website.Weather_data?,
         spaced_by: Dp,
         column_weights: FloatArray,
         icon_size: Array<Dp>)
{
    Row(modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(space = spaced_by,
                                                     alignment = Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically)
    {
        Image(painterResource(R.drawable.rainy_48),
              contentDescription = stringResource(id = R.string.humidity_icon),
              modifier = Modifier
                  .width(icon_size[0])
                  .height(icon_size[1]));

        Text(stringResource(R.string.rain_colon),
             modifier = Modifier
                 .weight(column_weights[0],
                         fill = true),
             style = Main_typography.h6);

        val rain_text: String? =
            if(weather_data_RKDAWE != null)
            {
                "${weather_data_RKDAWE.storm_rain} ${weather_data_RKDAWE.rain_units}";
            }
            else if(weather_data_davis != null)
            {
                "${weather_data_davis.rain} ${weather_data_davis.rainUnits}}"
            }
            else null;

        if(rain_text != null)
        {
            Text(rain_text,
                 modifier = Modifier.weight(column_weights[1],
                                            fill = true),
                 style = Main_typography.h6)
        };

        Column(modifier = Modifier
            .weight(column_weights[2],
                    fill = true),
               verticalArrangement = Arrangement.spacedBy(5.dp))
        {
            if(weather_data_RKDAWE != null)
            {
                Text("${stringResource(R.string.rain_rate)} ${
                    String.format("%.1f ", weather_data_RKDAWE.rain_rate)} ${
                    weather_data_RKDAWE.rain_rate_units}",
                     style = Main_typography.subtitle1)
            };

            if(weather_data_RKDAWE != null)
            {
                Text("${stringResource(R.string.last_fifteen_minute)} ${
                    String.format("%.1f ", weather_data_RKDAWE.last_fifteen_min_rain)} ${
                    weather_data_RKDAWE.rain_units}",
                     style = Main_typography.subtitle1)
            };

            if(weather_data_RKDAWE != null)
            {
                Text("${stringResource(R.string.last_hour)} ${
                    String.format("%.1f ", weather_data_RKDAWE.last_hour_rain)} ${
                    weather_data_RKDAWE.rain_units}",
                     style = Main_typography.subtitle1)
            };

            if(weather_data_RKDAWE != null)
            {
                Text("${stringResource(R.string.day_total)} ${
                    String.format("%.1f ", weather_data_RKDAWE.daily_rain)} ${
                    weather_data_RKDAWE.rain_units}",
                     style = Main_typography.subtitle1)
            };

            if(weather_data_RKDAWE != null)
            {
                Text("${stringResource(R.string.last_twenty_four_hour)} ${
                    String.format("%.1f ", weather_data_RKDAWE.last_twenty_four_hour_rain)} ${
                    weather_data_RKDAWE.rain_units}",
                     style = Main_typography.subtitle1)
            };

            if(weather_data_RKDAWE != null)
            {
                Text("${stringResource(R.string.month_total)} ${
                    String.format("%.1f ", weather_data_RKDAWE.month_rain)} ${
                    weather_data_RKDAWE.rain_units}",
                     style = Main_typography.subtitle1)
            };

            val seasonal_total_rain_text: String? =
                if(weather_data_RKDAWE != null)
                {
                    "${stringResource(id = R.string.seasonal_total)} ${
                        String.format("%.1f ", weather_data_RKDAWE.year_rain)} ${
                        weather_data_RKDAWE.rain_units}";
                }
                else if(weather_data_davis != null)
                {
                    "${stringResource(id = R.string.seasonal_total)} ${
                        weather_data_davis.seasonalRain} ${weather_data_davis.rainUnits}";
                }
                else null;

            if(seasonal_total_rain_text != null)
            {
                Text(seasonal_total_rain_text,
                     style = Main_typography.subtitle1)
            };
        }
    }
}

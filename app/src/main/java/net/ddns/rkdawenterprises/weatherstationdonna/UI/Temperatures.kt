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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import net.ddns.rkdawenterprises.davis_website.Weather_data.get_forecast_icon_uri_for_date
import net.ddns.rkdawenterprises.davis_website.Weather_page
import net.ddns.rkdawenterprises.rkdawe_api_common.Utilities.convert_time_UTC_to_local
import net.ddns.rkdawenterprises.rkdawe_api_common.Utilities.convert_timestamp_to_local
import net.ddns.rkdawenterprises.rkdawe_api_common.Weather_data
import net.ddns.rkdawenterprises.weatherstationdonna.Main_activity
import net.ddns.rkdawenterprises.weatherstationdonna.R
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.Main_theme
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.Main_typography
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.material_colors_extended
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.cos
import kotlin.math.sin

@Suppress("unused")
private const val LOG_TAG = "Temperatures_composable";

@Composable
fun Temperatures(weather_data_RKDAWE: Weather_data?,
                 weather_data_davis: net.ddns.rkdawenterprises.davis_website.Weather_data?)
{
    ConstraintLayout() {
        val (forecast_icon, current_temperature, vertical_divider, todays_temperature_high, todays_temperature_high_time, todays_temperature_low, todays_temperature_low_time) = createRefs();

        val timestamp: ZonedDateTime? = if(weather_data_RKDAWE != null)
        {
            convert_time_UTC_to_local(weather_data_RKDAWE.time);
        }
        else if(weather_data_davis != null)
        {
            convert_timestamp_to_local(weather_data_davis.lastReceived,
                                       weather_data_davis.timeZoneId);
        }
        else null;

        val forecast_URI: String? = if(weather_data_davis != null)
        {
            get_forecast_icon_uri_for_date(timestamp,
                                           weather_data_davis.forecastOverview);
        }
        else null;

        if(forecast_URI != null)
        {
            AsyncImage(model = forecast_URI,
                       contentDescription = stringResource(id = R.string.dynamic_forecast_icon),
                       modifier = Modifier
                           .constrainAs(forecast_icon) {
                               top.linkTo(parent.top,
                                          margin = 5.dp)
                               start.linkTo(parent.start,
                                            margin = 5.dp)
                           }
                           .height(75.dp)
                           .width(60.dp),
                       contentScale = ContentScale.Fit,
                       alignment = Alignment.Center);
        }
        else
        {
            Image(painterResource(R.drawable.sunny_48),
                  contentDescription = stringResource(id = R.string.dynamic_forecast_unavailable_icon),
                  modifier = Modifier
                      .constrainAs(forecast_icon) {
                          top.linkTo(parent.top,
                                     margin = 5.dp)
                          start.linkTo(parent.start,
                                       margin = 5.dp)
                      }
                      .height(75.dp)
                      .width(60.dp));
        }

        val current_temperature_text: String? =
            if(weather_data_RKDAWE != null)
            {
                Html.fromHtml("${String.format("%.1f ", weather_data_RKDAWE.outside_temperature)} ${
                    weather_data_RKDAWE.temperature_units}", Html.FROM_HTML_MODE_COMPACT).toString();
            }
            else if(weather_data_davis != null)
            {
                Html.fromHtml("${weather_data_davis.temperature} ${weather_data_davis.tempUnits}",
                              Html.FROM_HTML_MODE_COMPACT).toString();
            }
            else null;

        if(current_temperature_text != null)
        {
            Text(current_temperature_text,
                 modifier = Modifier.constrainAs(current_temperature) {
                     top.linkTo(forecast_icon.top)
                     bottom.linkTo(forecast_icon.bottom)
                     start.linkTo(forecast_icon.end,
                                  margin = 10.dp)
                     end.linkTo(vertical_divider.start)
                 },
                 style = Main_typography.h4);
        }

        Divider(color = MaterialTheme.material_colors_extended.view_divider,
                modifier = Modifier
                    .constrainAs(vertical_divider) {
                        top.linkTo(forecast_icon.top)
                        bottom.linkTo(forecast_icon.bottom)
                        start.linkTo(current_temperature.end,
                                     margin = 10.dp)
                        end.linkTo(todays_temperature_high.start)
                        height = Dimension.fillToConstraints
                    }
                    .width(2.dp));

        val todays_temperature_high_text: String? =
            if(weather_data_RKDAWE != null)
            {
                Html.fromHtml("${stringResource(id = R.string.high_colon)} ${
                    String.format("%.1f ", weather_data_RKDAWE.day_hi_out_temp)
                } ${weather_data_RKDAWE.temperature_units}", Html.FROM_HTML_MODE_COMPACT).toString();
            }
            else if(weather_data_davis != null)
            {
                Html.fromHtml("${stringResource(id = R.string.high_colon)} ${weather_data_davis.hiTemp} ${
                    weather_data_davis.tempUnits}", Html.FROM_HTML_MODE_COMPACT).toString();
            }
            else null;

        if(todays_temperature_high_text != null)
        {
            Text(todays_temperature_high_text,
                 modifier = Modifier.constrainAs(todays_temperature_high) {
                     top.linkTo(vertical_divider.top)
                     bottom.linkTo(todays_temperature_low.top)
                     start.linkTo(vertical_divider.end,
                                  margin = 10.dp)
                     end.linkTo(todays_temperature_high_time.start)
                 },
                 style = Main_typography.h6,
                 textAlign = TextAlign.Left)
        };

        val todays_temperature_high_time_text: String? =
            if(weather_data_RKDAWE != null)
            {
                "${stringResource(id = R.string.at)} ${
                    convert_time_UTC_to_local(weather_data_RKDAWE.time_day_hi_out_temp,"h:mm a")}"
            }
            else if(weather_data_davis != null)
            {
                "${stringResource(id = R.string.at)} ${convert_timestamp_to_local(weather_data_davis.hiTempDate,
                                                                                  weather_data_davis.timeZoneId)}"
            }
            else null;

        if(todays_temperature_high_time_text != null)
        {
            Text(todays_temperature_high_time_text,
                 modifier = Modifier.constrainAs(todays_temperature_high_time) {
                     top.linkTo(todays_temperature_high.top)
                     bottom.linkTo(todays_temperature_high.bottom)
                     start.linkTo(todays_temperature_high.end,
                                  margin = 5.dp)
                 },
                 style = Main_typography.subtitle1,
                 textAlign = TextAlign.Left)
        };

        val todays_temperature_low_text: String? =
            if(weather_data_RKDAWE != null)
            {
                Html.fromHtml("${stringResource(id = R.string.low_colon)} ${
                    String.format("%.1f ", weather_data_RKDAWE.day_low_out_temp)
                } ${weather_data_RKDAWE.temperature_units}", Html.FROM_HTML_MODE_COMPACT).toString();
            }
            else if(weather_data_davis != null)
            {
                Html.fromHtml("${stringResource(id = R.string.high_colon)} ${weather_data_davis.loTemp} ${
                    weather_data_davis.tempUnits}", Html.FROM_HTML_MODE_COMPACT).toString();
            }
            else null;

        if(todays_temperature_low_text != null)
        {
            Text(todays_temperature_low_text,
                 modifier = Modifier.constrainAs(todays_temperature_low) {
                     top.linkTo(todays_temperature_high.bottom)
                     bottom.linkTo(vertical_divider.bottom)
                     start.linkTo(vertical_divider.end,
                                  margin = 10.dp)
                     end.linkTo(todays_temperature_low_time.start)
                 },
                 style = Main_typography.h6,
                 textAlign = TextAlign.Left)
        };

        val todays_temperature_low_time_text: String? =
            if(weather_data_RKDAWE != null)
            {
                "${stringResource(id = R.string.at)} ${
                    convert_time_UTC_to_local(weather_data_RKDAWE.time_day_low_out_temp,"h:mm a")}"
            }
            else if(weather_data_davis != null)
            {
                "${stringResource(id = R.string.at)} ${convert_timestamp_to_local(weather_data_davis.loTempDate,
                                                                                  weather_data_davis.timeZoneId)}"
            }
            else null;

        if(todays_temperature_low_time_text != null)
        {
            Text(todays_temperature_low_time_text,
                 modifier = Modifier.constrainAs(todays_temperature_low_time) {
                     top.linkTo(todays_temperature_low.top)
                     bottom.linkTo(todays_temperature_low.bottom)
                     start.linkTo(todays_temperature_low.end,
                                  margin = 5.dp)
                 },
                 style = Main_typography.subtitle1,
                 textAlign = TextAlign.Left)
        };
    }
}

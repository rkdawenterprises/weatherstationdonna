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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
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
import net.ddns.rkdawenterprises.rkdawe_api_common.Utilities.convert_time_UTC_to_local
import net.ddns.rkdawenterprises.rkdawe_api_common.Weather_data
import net.ddns.rkdawenterprises.weatherstationdonna.Main_activity
import net.ddns.rkdawenterprises.weatherstationdonna.R
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.Main_theme
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.Main_typography
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.material_colors_extended
import kotlin.math.cos
import kotlin.math.sin

@Suppress("unused")
private const val LOG_TAG = "Main_composable";

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Main(main_activity: Main_activity,
         main_view_model: Main_view_model)
{
    val is_night_mode = main_view_model.is_application_in_night_mode(main_activity).collectAsStateWithLifecycle(false,
                                                                                                                main_activity);
    val weather_data = main_view_model.combined_response.observeAsState();
    val is_refreshing by main_view_model.is_refreshing.collectAsStateWithLifecycle();
    val pull_refresh_state = rememberPullRefreshState(is_refreshing,
                                                      { main_view_model.refresh(main_activity) })

    Main_theme(main_activity,
               is_night_mode.value) {
        val data_storage: Main_view_model.Data_storage? = weather_data.value;
        if(data_storage != null)
        {
            val weather_data_RKDAWE = data_storage.m_data_RKDAWE;
            val weather_data_davis = data_storage.m_data_davis;
            val weather_page = data_storage.m_page_davis;

            Box(modifier = Modifier
                .padding(5.dp)
                .pullRefresh(pull_refresh_state)
                .clickable { main_activity.toggle() }) {
                LazyColumn(modifier = Modifier.fillMaxSize(),
                           verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    item() {
                        Spacer(modifier = Modifier.height(20.dp));
                    }

                    item() {

                        val system_name = if(weather_data_RKDAWE != null)
                        {
                            weather_data_RKDAWE.system_name;
                        }
                        else if(weather_page != null)
                        {
                            weather_page.systemName;
                        }
                        else
                        {
                            ""
                        }

                        if(system_name.length > 1) Header(system_name);
                    }

                    item() {
                        if((weather_data_RKDAWE != null) && (weather_data_davis != null)) Temperatures(weather_data_RKDAWE,
                                                                                                       weather_data_davis);
                    }

                    item() {
                        Divider(color = MaterialTheme.material_colors_extended.view_divider,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp))
                    }

                    item() {
                        if((weather_data_RKDAWE != null) && (weather_data_davis != null)) Conditions(weather_data_RKDAWE,
                                                                                                     weather_data_davis);
                    }

                    item() {
                        Text(text = "${weather_data.value?.m_data_RKDAWE?.serialize_to_JSON()}");
                    }

                    item() {
                        Text(text = "${weather_data.value?.m_data_davis?.serialize_to_JSON()}");
                    }

                    item() {
                        Text(text = "${weather_data.value?.m_page_davis?.serialize_to_JSON()}");
                    }
                }

                PullRefreshIndicator(is_refreshing,
                                     pull_refresh_state,
                                     Modifier.align(Alignment.TopCenter));
            }
        }
    }
}

@Composable
fun Header(system_name: String)
{
    TextField(value = system_name,
              modifier = Modifier.fillMaxWidth(),
              colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.material_colors_extended.primaryVariant,
                                                         disabledTextColor = MaterialTheme.material_colors_extended.onPrimary),
              shape = RectangleShape,
              singleLine = true,
              onValueChange = {},
              enabled = false,
              textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center,
                                                      fontFamily = Main_typography.subtitle1.fontFamily,
                                                      fontWeight = Main_typography.subtitle1.fontWeight,
                                                      fontSize = Main_typography.subtitle1.fontSize))
}

@Composable
fun Temperatures(weather_data_RKDAWE: Weather_data,
                 weather_data_davis: net.ddns.rkdawenterprises.davis_website.Weather_data)
{
    ConstraintLayout() {
        val (forecast_icon, current_temperature, vertical_divider, todays_temperature_high, todays_temperature_high_time, todays_temperature_low, todays_temperature_low_time) = createRefs();

        val forecast_URI: String = get_forecast_icon_uri_for_date(convert_time_UTC_to_local(weather_data_RKDAWE.time),
                                                                  weather_data_davis.forecastOverview);
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
                   alignment = Alignment.Center)

        val current_temperature_text: String =
            Html.fromHtml("${weather_data_RKDAWE.outside_temperature} ${weather_data_RKDAWE.temperature_units}",
                          Html.FROM_HTML_MODE_COMPACT).toString();
        Text(current_temperature_text,
             modifier = Modifier.constrainAs(current_temperature) {
                 top.linkTo(forecast_icon.top)
                 bottom.linkTo(forecast_icon.bottom)
                 start.linkTo(forecast_icon.end,
                              margin = 10.dp)
                 end.linkTo(vertical_divider.start)
             },
             style = Main_typography.h4)

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
                    .width(2.dp))

        val todays_temperature_high_text: String = Html.fromHtml("${stringResource(id = R.string.high_colon)} ${
            String.format("%.1f ",
                          weather_data_RKDAWE.day_hi_out_temp)
        } ${weather_data_RKDAWE.temperature_units}",
                                                                 Html.FROM_HTML_MODE_COMPACT).toString();
        Text(todays_temperature_high_text,
             modifier = Modifier.constrainAs(todays_temperature_high) {
                 top.linkTo(vertical_divider.top)
                 bottom.linkTo(todays_temperature_low.top)
                 start.linkTo(vertical_divider.end,
                              margin = 10.dp)
                 end.linkTo(todays_temperature_high_time.start)
             },
             style = Main_typography.subtitle1,
             textAlign = TextAlign.Left)

        val todays_temperature_high_time_text: String = "${stringResource(id = R.string.at)} ${
            convert_time_UTC_to_local(weather_data_RKDAWE.time_day_hi_out_temp,
                                      "h:mm a")
        }"
        Text(todays_temperature_high_time_text,
             modifier = Modifier.constrainAs(todays_temperature_high_time) {
                 top.linkTo(todays_temperature_high.top)
                 bottom.linkTo(todays_temperature_high.bottom)
                 start.linkTo(todays_temperature_high.end,
                              margin = 5.dp)
             },
             style = Main_typography.body1,
             textAlign = TextAlign.Left)

        val todays_temperature_low_text: String = Html.fromHtml("${stringResource(id = R.string.low_colon)} ${
            String.format("%.1f ",
                          weather_data_RKDAWE.day_low_out_temp)
        } ${weather_data_RKDAWE.temperature_units}",
                                                                Html.FROM_HTML_MODE_COMPACT).toString();
        Text(todays_temperature_low_text,
             modifier = Modifier.constrainAs(todays_temperature_low) {
                 top.linkTo(todays_temperature_high.bottom)
                 bottom.linkTo(vertical_divider.bottom)
                 start.linkTo(vertical_divider.end,
                              margin = 10.dp)
                 end.linkTo(todays_temperature_low_time.start)
             },
             style = Main_typography.subtitle1,
             textAlign = TextAlign.Left)

        val todays_temperature_low_time_text = "${stringResource(id = R.string.at)} ${
            convert_time_UTC_to_local(weather_data_RKDAWE.time_day_low_out_temp,
                                      "h:mm a")
        }"
        Text(todays_temperature_low_time_text,
             modifier = Modifier.constrainAs(todays_temperature_low_time) {
                 top.linkTo(todays_temperature_low.top)
                 bottom.linkTo(todays_temperature_low.bottom)
                 start.linkTo(todays_temperature_low.end,
                              margin = 5.dp)
             },
             style = Main_typography.body1,
             textAlign = TextAlign.Left)
    }
}

@Composable
fun Conditions(weather_data_RKDAWE: Weather_data,
               weather_data_davis: net.ddns.rkdawenterprises.davis_website.Weather_data)
{
    Column() {
        Humidity_row(weather_data_RKDAWE = weather_data_RKDAWE,
                     weather_data_davis = weather_data_davis);

        Spacer(modifier = Modifier.height(10.dp));

        Divider(color = MaterialTheme.material_colors_extended.view_divider,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp));

        Wind_row1(weather_data_RKDAWE = weather_data_RKDAWE);
        Wind_row2(weather_data_RKDAWE = weather_data_RKDAWE);

        Spacer(modifier = Modifier.height(10.dp));

        Divider(color = MaterialTheme.material_colors_extended.view_divider,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp));
    }
}

@Composable
fun Humidity_row(weather_data_RKDAWE: Weather_data,
                 weather_data_davis: net.ddns.rkdawenterprises.davis_website.Weather_data)
{
    Row(modifier = Modifier.padding(top = 10.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Image(painterResource(R.drawable.humidity_percentage_48),
              contentDescription = stringResource(id = R.string.humidity_icon),
              modifier = Modifier
                  .height(50.dp)
                  .width(45.dp));

        Text(stringResource(R.string.humidity_colon),
             modifier = Modifier
                 .weight(1f,
                         fill = true)
                 .padding(start = 10.dp),
             style = Main_typography.subtitle1);

        val current_humidity_text = Html.fromHtml("${weather_data_RKDAWE.outside_humidity} ${
            weather_data_RKDAWE.humidity_units
        }",
                                                  Html.FROM_HTML_MODE_COMPACT).toString();

        Text(current_humidity_text,
             modifier = Modifier.weight(0.75f,
                                        fill = true),
             style = Main_typography.subtitle1);

        val feels_like_temperature_text = Html.fromHtml("${stringResource(id = R.string.feels_like)} ${
            weather_data_davis.temperatureFeelLike
        } ${weather_data_RKDAWE.temperature_units}",
                                                        Html.FROM_HTML_MODE_COMPACT).toString();

        Text(feels_like_temperature_text,
             modifier = Modifier.weight(2f,
                                        fill = true),
             style = Main_typography.body1);
    }
}

@Composable
fun Wind_row1(weather_data_RKDAWE: Weather_data)
{
    Row(modifier = Modifier.padding(top = 10.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Image(painterResource(R.drawable.air_48),
              contentDescription = stringResource(id = R.string.wind_icon),
              modifier = Modifier
                  .height(50.dp)
                  .width(45.dp));

        Text(stringResource(R.string.wind_colon),
             modifier = Modifier
                 .weight(1f,
                         fill = true)
                 .padding(start = 10.dp),
             style = Main_typography.subtitle1);

        val current_wind_text = "${weather_data_RKDAWE.wind_speed} ${
            weather_data_RKDAWE.wind_speed_units
        }"

        Text(current_wind_text,
             modifier = Modifier.weight(0.75f,
                                        fill = true),
             style = Main_typography.subtitle1);

        val day_high_text = "${stringResource(id = R.string.peak)} ${
            weather_data_RKDAWE.daily_hi_wind_speed
        } ${weather_data_RKDAWE.wind_speed_units} ${
            stringResource(id = R.string.at)
        } ${
            convert_time_UTC_to_local(weather_data_RKDAWE.time_of_hi_speed,
                                      "h:mm a")
        }"

        Text(day_high_text,
             modifier = Modifier.weight(2f,
                                        fill = true),
             style = Main_typography.body1);
    }
}

@Composable
fun Wind_row2(weather_data_RKDAWE: Weather_data)
{
    Row(modifier = Modifier.padding(top = 10.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Compass(
            modifier = Modifier
                .size(70.dp)
                .weight(1f,
                        fill = true),
            angle = weather_data_RKDAWE.wind_direction,
            marker_degrees_step = 30,
               );

        Column(modifier = Modifier
            .weight(1.5f,
                    fill = true)
            .padding(start = 10.dp),
               verticalArrangement = Arrangement.spacedBy(5.dp)) {

            Text("${stringResource(id = R.string.two_minute_average)} ${
                weather_data_RKDAWE.two_min_avg_wind_speed
            } ${weather_data_RKDAWE.wind_speed_units}",
                 style = Main_typography.body1);

            Text("${stringResource(id = R.string.ten_minute_average)} ${
                weather_data_RKDAWE.ten_min_avg_wind_speed
            } ${weather_data_RKDAWE.wind_speed_units}",
                 style = Main_typography.body1);

            Text(Html.fromHtml("${stringResource(id = R.string.ten_minute_gust)} ${
                weather_data_RKDAWE.ten_min_wind_gust
            } ${weather_data_RKDAWE.wind_speed_units} ${
                stringResource(id = R.string.at)
            } ${weather_data_RKDAWE.wind_direction_of_ten_min_wind_gust} ${
                weather_data_RKDAWE.wind_direction_units
            }",
                               Html.FROM_HTML_MODE_COMPACT).toString(),
                 style = Main_typography.body1);
        }
    }
}

//        m_binding.conditionsAsOf.text = resources.getString(R.string.conditions_as_of_format,
//                                                            convert_time_UTC_to_local(weather_data.time,
//                                                                                      "h:mm a EEEE, MMM d, yyyy"));

@Composable
fun Circular_progress_bar(modifier: Modifier = Modifier,
                          percentage: Float,
                          fillColor: Color,
                          backgroundColor: Color,
                          strokeWidth: Dp)
{
    Canvas(modifier = modifier
        .size(150.dp)
        .padding(10.dp)) {
        // Background Line
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


        var angleInDegrees = (percentage * 260.0) + 50.0
        var radius = (size.height / 2)
        var x = -(radius * sin(Math.toRadians(angleInDegrees))).toFloat() + (size.width / 2)
        var y = (radius * cos(Math.toRadians(angleInDegrees))).toFloat() + (size.height / 2)

        drawCircle(color = Color.White,
                   radius = 5f,
                   center = Offset(x,
                                   y))
    }
}

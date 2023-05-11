@file:Suppress("ClassName",
               "FunctionName",
               "RedundantSemicolon",
               "PrivatePropertyName",
               "LocalVariableName",
               "PropertyName",
               "PackageName")

package net.ddns.rkdawenterprises.weatherstationdonna.UI

import android.content.Context
import android.text.Html
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WindPower
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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

@Suppress("unused")
private const val LOG_TAG = "Main_composable";

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Main(main_activity: Main_activity,
         main_view_model: Main_view_model)
{
    val is_night_mode = main_view_model.is_application_in_night_mode(main_activity)
            .collectAsStateWithLifecycle(false, main_activity);
    val weather_data = main_view_model.combined_response.observeAsState();
    val is_refreshing by main_view_model.is_refreshing.collectAsStateWithLifecycle();
    val pull_refresh_state = rememberPullRefreshState(is_refreshing, { main_view_model.refresh(main_activity) })

    Main_theme(main_activity, is_night_mode.value)
    {
        val data_storage: Main_view_model.Data_storage? = weather_data.value;
        if(data_storage != null)
        {
            val weather_data_RKDAWE = data_storage.m_data_RKDAWE;
            val weather_data_davis = data_storage.m_data_davis;
            val weather_page = data_storage.m_page_davis;

            Box(modifier = Modifier
                    .padding(5.dp)
                    .pullRefresh(pull_refresh_state)
                    .clickable { main_activity.toggle() })
            {
                LazyColumn(modifier = Modifier.fillMaxSize(),
                           verticalArrangement = Arrangement.spacedBy(10.dp))
                {
                    item()
                    {
                        Spacer(modifier = Modifier.height(20.dp));
                    }

                    item()
                    {

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

                    item()
                    {
                        if((weather_data_RKDAWE != null) && (weather_data_davis != null))
                            Temperatures(main_activity, weather_data_RKDAWE, weather_data_davis);
                    }

                    item()
                    {
                        Divider(color = MaterialTheme.material_colors_extended.view_divider,
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp))
                    }

                    item()
                    {
                        if((weather_data_RKDAWE != null) && (weather_data_davis != null))
                            Conditions(main_activity,
                                       weather_data_RKDAWE,
                                       weather_data_davis);
                    }

                    item()
                    {
                        Text(text = "${weather_data.value?.m_data_RKDAWE?.serialize_to_JSON()}");
                    }

                    item()
                    {
                        Text(text = "${weather_data.value?.m_data_davis?.serialize_to_JSON()}");
                    }

                    item()
                    {
                        Text(text = "${weather_data.value?.m_page_davis?.serialize_to_JSON()}");
                    }
                }

                PullRefreshIndicator(is_refreshing, pull_refresh_state, Modifier.align(Alignment.TopCenter));
            }
        }
    }
}

@Composable
fun Header(system_name: String)
{
    TextField(
        value = system_name,
        modifier = Modifier
                .fillMaxWidth(),
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
fun Temperatures(context: Context,
                 weather_data_RKDAWE: Weather_data,
                 weather_data_davis: net.ddns.rkdawenterprises.davis_website.Weather_data)
{
    ConstraintLayout()
    {
        val (forecast_icon,
            current_temperature,
            vertical_divider,
            todays_temperature_high,
            todays_temperature_high_time,
            todays_temperature_low,
            todays_temperature_low_time) = createRefs();

        val forecast_URI: String =
            get_forecast_icon_uri_for_date(convert_time_UTC_to_local(weather_data_RKDAWE.time),
                                           weather_data_davis.forecastOverview);
        AsyncImage(model = forecast_URI,
                   contentDescription = stringResource(id = R.string.dynamic_forecast_icon),
                   modifier = Modifier
                           .constrainAs(forecast_icon)
                           {
                               top.linkTo(parent.top, margin = 5.dp)
                               start.linkTo(parent.start, margin = 5.dp)
                           }
                           .height(75.dp)
                           .width(60.dp),
                   contentScale = ContentScale.Fit,
                   alignment = Alignment.Center
        )

        val current_temperature_text: String =
            Html.fromHtml("${weather_data_RKDAWE.outside_temperature} ${weather_data_RKDAWE.temperature_units}",
                          Html.FROM_HTML_MODE_COMPACT).toString();
        Text(current_temperature_text,
             modifier = Modifier.constrainAs(current_temperature)
             {
                 top.linkTo(forecast_icon.top)
                 bottom.linkTo(forecast_icon.bottom)
                 start.linkTo(forecast_icon.end, margin = 10.dp)
                 end.linkTo(vertical_divider.start)
             },
             style = Main_typography.h4)

        Divider(color = MaterialTheme.material_colors_extended.view_divider,
                modifier = Modifier
                        .constrainAs(vertical_divider)
                        {
                            top.linkTo(forecast_icon.top)
                            bottom.linkTo(forecast_icon.bottom)
                            start.linkTo(current_temperature.end, margin = 10.dp)
                            end.linkTo(todays_temperature_high.start)
                            height = Dimension.fillToConstraints
                        }
                        .width(2.dp))

        val todays_temperature_high_text: String =
            Html.fromHtml("${stringResource(id = R.string.high_colon)} ${String.format("%.1f ", weather_data_RKDAWE.day_hi_out_temp)} ${weather_data_RKDAWE.temperature_units}",
                          Html.FROM_HTML_MODE_COMPACT).toString();
        Text(todays_temperature_high_text,
             modifier = Modifier.constrainAs(todays_temperature_high)
             {
                 top.linkTo(vertical_divider.top)
                 bottom.linkTo(todays_temperature_low.top)
                 start.linkTo(vertical_divider.end, margin = 10.dp)
                 end.linkTo(todays_temperature_high_time.start)
             },
             style = Main_typography.subtitle1,
             textAlign = TextAlign.Left)

        val todays_temperature_high_time_text: String =
            "${stringResource(id = R.string.at)} ${convert_time_UTC_to_local(weather_data_RKDAWE.time_day_hi_out_temp, "h:mm a")}"
        Text(todays_temperature_high_time_text,
             modifier = Modifier.constrainAs(todays_temperature_high_time)
             {
                 top.linkTo(todays_temperature_high.top)
                 bottom.linkTo(todays_temperature_high.bottom)
                 start.linkTo(todays_temperature_high.end, margin = 5.dp)
             },
             style = Main_typography.body1,
             textAlign = TextAlign.Left)

        val todays_temperature_low_text: String =
            Html.fromHtml("${stringResource(id = R.string.low_colon)} ${String.format("%.1f ", weather_data_RKDAWE.day_low_out_temp)} ${weather_data_RKDAWE.temperature_units}",
                          Html.FROM_HTML_MODE_COMPACT).toString();
        Text(todays_temperature_low_text,
             modifier = Modifier.constrainAs(todays_temperature_low)
             {
                 top.linkTo(todays_temperature_high.bottom)
                 bottom.linkTo(vertical_divider.bottom)
                 start.linkTo(vertical_divider.end, margin = 10.dp)
                 end.linkTo(todays_temperature_low_time.start)
             },
             style = Main_typography.subtitle1,
             textAlign = TextAlign.Left)

        val todays_temperature_low_time_text =
            "${stringResource(id = R.string.at)} ${convert_time_UTC_to_local(weather_data_RKDAWE.time_day_low_out_temp, "h:mm a")}"
        Text(todays_temperature_low_time_text,
             modifier = Modifier.constrainAs(todays_temperature_low_time)
             {
                 top.linkTo(todays_temperature_low.top)
                 bottom.linkTo(todays_temperature_low.bottom)
                 start.linkTo(todays_temperature_low.end, margin = 5.dp)
             },
             style = Main_typography.body1,
             textAlign = TextAlign.Left)
    }
}

@Composable
fun Conditions(context: Context,
               weather_data_RKDAWE: Weather_data,
               weather_data_davis: net.ddns.rkdawenterprises.davis_website.Weather_data)
{
    ConstraintLayout()
    {
        val (forecast_icon,
            current_temperature,
            vertical_divider,
            todays_temperature_high,
            todays_temperature_high_time,
            todays_temperature_low,
            todays_temperature_low_time) = createRefs();
    }

}



@Composable
fun Humidity(context: Context,
             weather_data_RKDAWE: Weather_data,
             weather_data_davis: net.ddns.rkdawenterprises.davis_website.Weather_data)
{
    val current_humidity_text =
        Html.fromHtml(" ${weather_data_RKDAWE.outside_humidity} ${weather_data_RKDAWE.humidity_units}",
                      Html.FROM_HTML_MODE_COMPACT).toString();

    val humidity_description_text = Html.fromHtml("${stringResource(id = R.string.feels_like)} ${weather_data_davis.temperatureFeelLike} ${weather_data_RKDAWE.temperature_units}",
                                                  Html.FROM_HTML_MODE_COMPACT).toString();

    Row(verticalAlignment = Alignment.CenterVertically)
    {
        Column()
        {
            Image(painterResource(R.drawable.humidity_percentage_48),
                  contentDescription = stringResource(id = R.string.humidity_icon),
                  modifier = Modifier
                          .height(50.dp)
                          .width(45.dp));
        }

        Column(modifier = Modifier.weight(1f))
        {
            Text("${stringResource(R.string.humidity_colon)} $current_humidity_text",
                 style = Main_typography.subtitle1,
                 modifier = Modifier.padding(start = 10.dp));
        }

        Column(modifier = Modifier.weight(1.5f))
        {
            Text(humidity_description_text,
                 style = Main_typography.body1);
        }
    }
}

@Composable
fun Wind(context: Context,
         weather_data_RKDAWE: Weather_data)
{
    /*
    val current_wind_text =
        Html.fromHtml(" ${weather_data_RKDAWE.wind_speed} ${weather_data_RKDAWE.wind_speed_units}",
                      Html.FROM_HTML_MODE_COMPACT).toString();

    val wind_gust_text =
        context.resources.getString(R.string.humidity_description_format,
                                    weather_data_davis.temperatureFeelLike).toString();

    Row(verticalAlignment = Alignment.CenterVertically)
    {
        Column()
        {
            Image(painterResource(R.drawable.humidity_percentage_48),
                  contentDescription = null,
                  modifier = Modifier
                          .height(50.dp)
                          .width(45.dp));
        }

        Column(modifier = Modifier.weight(1f))
        {
            Text(stringResource(R.string.humidity_colon),
                 style = Main_typography.subtitle1,
                 modifier = Modifier.padding(start = 5.dp));
        }

        Column(modifier = Modifier.weight(0.5f))
        {
            Text(current_humidity_text,
                 style = Main_typography.subtitle1);
        }

        Column(modifier = Modifier.weight(1.5f))
        {
            Text(humidity_description_text,
                 style = Main_typography.body1);
        }
    }
    */
}


//        m_binding.conditionsAsOf.text = resources.getString(R.string.conditions_as_of_format,
//                                                            convert_time_UTC_to_local(weather_data.time,
//                                                                                      "h:mm a EEEE, MMM d, yyyy"));
//        Glide.with(this).load(forecast_URI).fitCenter().into(m_binding.forecastIcon);
//
//        m_binding.currentTemperature.text =
//            Html.fromHtml("${weather_data.outside_temperature} ${weather_data.temperature_units}",
//                          Html.FROM_HTML_MODE_COMPACT);
//
//        m_binding.todaysTemperatureHigh.text =
//            Html.fromHtml(getString(R.string.high_format,
//                                    String.format("%.1f", weather_data.day_hi_out_temp),
//                                    weather_data.temperature_units),
//                          Html.FROM_HTML_MODE_COMPACT);
//
//        m_binding.todaysTemperatureHighTime.text =
//            resources.getString(R.string.at_format,
//                                convert_time_UTC_to_local(weather_data.time_day_hi_out_temp,
//                                                          "h:mm a"));
//
//        m_binding.todaysTemperatureLow.text =
//            Html.fromHtml(getString(R.string.low_format,
//                                    String.format("%.1f", weather_data.day_low_out_temp),
//                                    weather_data.temperature_units),
//                          Html.FROM_HTML_MODE_COMPACT);
//
//        m_binding.todaysTemperatureLowTime.text =
//            resources.getString(R.string.at_format,
//                                convert_time_UTC_to_local(weather_data.time_day_low_out_temp,
//                                                          "h:mm a"));
//
//        m_binding.currentHumidity.text =
//        Html.fromHtml(" ${weather_data.outside_humidity} ${weather_data.humidity_units}",
//                      Html.FROM_HTML_MODE_COMPACT);
//
//        m_binding.humidityDescription.text =
//            resources.getString(R.string.humidity_description_format, json_data.temperatureFeelLike );
//
//    }

//        Surface(color = MaterialTheme.material_colors_extended.background) {
//            Box(Modifier.fillMaxSize(), Alignment.Center) {
//                Button(
//                    onClick = { dark = !dark },
//                    colors = ButtonDefaults.buttonColors(
//                        backgroundColor = MaterialTheme.material_colors_extended.warning,
//                        contentColor = MaterialTheme.material_colors_extended.on_warning,
//                    ),
//                ) {
//                    Text("Toggle")
//                }
//            }
//        }
//                    if(!is_ok)
//                    {
//                        val message = m_context.resources.getString(R.string.not_allowed_to_get_weather_data_unless_over_wifi);
//                        Log.d(Main_activity.LOG_TAG, "is_ok_to_fetch_data: $message");
//                        Snackbar.make(m_binding.root,
//                                      message,
//                                      Snackbar.LENGTH_SHORT).setAction(R.string.ok) {}.show();
//                    }

//        val scaffold_state = rememberScaffoldState();
//        Scaffold(
//            topBar = { Top_app_bar(scaffold_state, main_activity) }
//        ) { innerPadding->
//            LazyColumn(contentPadding = innerPadding) {
//                item {
//                    Header("Weather Station Donna @ Hot Springs, AR - Hot Springs, AR, USA");
//                }
//                item {
//                    FeaturedPost(
//                        post = featured,
//                        modifier = Modifier.padding(16.dp)
//                    )
//                }
//                item {
//                    Header(stringResource(R.string.popular))
//                }
//                items(posts) { post ->
//                    PostItem(post = post)
//                    Divider(startIndent = 72.dp)
//                }
//            }
//        }
//    }

//@Composable
//private fun Top_app_bar(scaffold_state: ScaffoldState, main_activity: Main_activity)
//{
//    val coroutine_scope = rememberCoroutineScope();
//    var manual_refresh by remember { mutableStateOf(1); }
//    var show_menu by remember { mutableStateOf(false); }
//    TopAppBar(
//        navigationIcon = {
//            IconButton(onClick = {
//                coroutine_scope.launch {
//                    val message = String.format("%s %s",
//                                                main_activity.resources.getString(R.string.exiting),
//                                                main_activity.resources.getString(R.string.app_name));
//                    scaffold_state.snackbarHostState.showSnackbar(
//                        message = message,
//                        actionLabel = main_activity.resources.getString(R.string.ok),
//                        duration = SnackbarDuration.Long
//                    )
//                }
//
//                main_activity.exit_app();
//            },
//                       modifier = Modifier.padding(horizontal = 12.dp)) {
//                Icon(imageVector = Icons.Filled.ArrowBack,
//                     contentDescription = stringResource(R.string.navigate_back))
//            }
//        },
//        title = {
//            Text(text = stringResource(R.string.app_name))
//        },
//        backgroundColor = MaterialTheme.colors.primarySurface,
//        actions = {
//            IconButton(onClick = { manual_refresh++ }) {
//                Icon(imageVector = Icons.Filled.Refresh,
//                     contentDescription = stringResource(id = R.string.action_refresh_weather_data))
//            }
//            IconButton(onClick = { show_menu = !show_menu }) {
//                Icon(imageVector = Icons.Filled.MoreVert,
//                     contentDescription = null)
//            }
//            DropdownMenu(
//                expanded = show_menu,
//                onDismissRequest = { show_menu = false }
//            ) {
//                val night_mode_dialog_open = remember { mutableStateOf(false); }
//                DropdownMenuItem(onClick = {
//                    val selections = main_activity.resources.getStringArray(R.array.night_mode_selection);
//                    night_mode_dialog_open.value = true;
//                    if (night_mode_dialog_open.value)
//                    {
//                        AlertDialog( onDismissRequest = {
//                            night_mode_dialog_open.value = false
//                        },)
//                    }
//                    AlertDialogBuilder(main_activity)
//                            .setTitle(R.string.select_night_mode)
//                            .setSingleChoiceItems(selections, main_activity.get_night_mode_selection(), null)
//                            .setPositiveButton(R.string.ok) { dialog, _->
//                                val selection = (dialog as AlertDialog).listView.checkedItemPosition;
//                                main_activity.put_night_mode_selection(selection);
//                                main_activity.update_night_mode(selection)
//                            }
//                            .setNegativeButton(R.string.cancel) { dialog, _-> dialog.cancel(); }
//                            .show();
//                }) {
//                    Icon(imageVector = Icons.Filled.DarkMode,
//                         contentDescription = stringResource(id = R.string.action_set_night_mode_theme));
//                    Spacer(Modifier.size(ButtonDefaults.IconSpacing));
//                    Text(stringResource(id = R.string.action_set_night_mode_theme));
//                }
//            }
//        }
//    )
//}


//@Composable
//fun FeaturedPost(
//    post: Post,
//    modifier: Modifier = Modifier
//) {
//    Card(modifier) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .clickable { /* onClick */ }
//        ) {
//            Image(
//                painter = painterResource(post.imageId),
//                contentDescription = null,
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .heightIn(min = 180.dp)
//                    .fillMaxWidth()
//            )
//            Spacer(Modifier.height(16.dp))
//
//            val padding = Modifier.padding(horizontal = 16.dp)
//            Text(
//                text = post.title,
//                style = MaterialTheme.typography.h6,
//                modifier = padding
//            )
//            Text(
//                text = post.metadata.author.name,
//                style = MaterialTheme.typography.body2,
//                modifier = padding
//            )
//            PostMetadata(post, padding)
//            Spacer(Modifier.height(16.dp))
//        }
//    }
//}
//
//@Composable
//private fun PostMetadata(
//    post: Post,
//    modifier: Modifier = Modifier
//) {
//    val divider = "  •  "
//    val tagDivider = "  "
//    val text = buildAnnotatedString {
//        append(post.metadata.date)
//        append(divider)
//        append(stringResource(R.string.read_time, post.metadata.readTimeMinutes))
//        append(divider)
//        val tagStyle = MaterialTheme.typography.overline.toSpanStyle().copy(
//            background = MaterialTheme.colors.primary.copy(alpha = 0.1f)
//        )
//        post.tags.forEachIndexed { index, tag ->
//            if (index != 0) {
//                append(tagDivider)
//            }
//            withStyle(tagStyle) {
//                append(" ${tag.uppercase(Locale.getDefault())} ")
//            }
//        }
//    }
//    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
//        Text(
//            text = text,
//            style = MaterialTheme.typography.body2,
//            modifier = modifier
//        )
//    }
//}
//
//@OptIn(ExperimentalMaterialApi::class)
//@Composable
//fun PostItem(
//    post: Post,
//    modifier: Modifier = Modifier
//) {
//    ListItem(
//        modifier = modifier
//            .clickable { /* todo */ }
//            .padding(vertical = 8.dp),
//        icon = {
//            Image(
//                painter = painterResource(post.imageThumbId),
//                contentDescription = null,
//                modifier = Modifier.clip(shape = MaterialTheme.shapes.small)
//            )
//        },
//        text = {
//            Text(text = post.title)
//        },
//        secondaryText = {
//            PostMetadata(post)
//        }
//    )
//}
//
//@Preview("Post Item")
//@Composable
//private fun PostItemPreview() {
//    val post = remember { PostRepo.getFeaturedPost() }
//    JetnewsTheme {
//        Surface {
//            PostItem(post = post)
//        }
//    }
//}
//
//@Preview("Featured Post")
//@Composable
//private fun FeaturedPostPreview() {
//    val post = remember { PostRepo.getFeaturedPost() }
//    JetnewsTheme {
//        FeaturedPost(post = post)
//    }
//}
//
//@Preview("Featured Post • Dark")
//@Composable
//private fun FeaturedPostDarkPreview() {
//    val post = remember { PostRepo.getFeaturedPost() }
//    JetnewsTheme(darkTheme = true) {
//        FeaturedPost(post = post)
//    }
//}
//
//@Preview("Home")
//@Composable
//private fun HomePreview() {
//    Home()
//}

//@Preview
//@Composable
//private fun ThemeSwapDemo() {
//    var dark by remember { mutableStateOf(false) }
//    Crossfade(targetState = dark) { isDark ->
//        Main_theme(darkTheme = isDark) {
//            Surface(color = MaterialTheme.myColors.background) {
//                Box(Modifier.fillMaxSize(), Alignment.Center) {
//                    Button(
//                        onClick = { dark = !dark },
//                        colors = ButtonDefaults.buttonColors(
//                            backgroundColor = MaterialTheme.myColors.warning,
//                            contentColor = MaterialTheme.myColors.onWarning,
//                        ),
//                    ) {
//                        Text("Toggle")
//                    }
//                }
//            }
//        }
//    }
//}

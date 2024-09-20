/*
 * Copyright (c) 2024 RKDAW Enterprises and Ralph Williamson.
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
               "PackageName")

package net.ddns.rkdawenterprises.weatherstationdonna.UI

import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import net.ddns.rkdawenterprises.rkdawe_api_common.Weather_data
import net.ddns.rkdawenterprises.weatherstationdonna.R
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.Main_theme
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.Typography
import net.ddns.rkdawenterprises.weatherstationdonna.User_settings


@Suppress("unused")
private const val LOG_TAG = "Main_composable";

@OptIn(ExperimentalMaterial3Api::class,
       ExperimentalPermissionsApi::class)
@Composable
fun Main(context: Context,
         window_insets_controller: WindowInsetsControllerCompat,
         main_view_model: Main_view_model)
{
    val are_system_bars_visible =
        main_view_model.are_system_bars_visible.collectAsStateWithLifecycle(initialValue = true)

    val is_dark_mode = User_settings.is_application_in_dark_mode(context)
            .collectAsStateWithLifecycle(initialValue = false)
    User_settings.load_and_update_dark_mode_selection(context = context,
                                                      scope = main_view_model.viewModelScope) {}

    val is_show_about_dialog: Boolean by main_view_model.is_show_about_dialog.collectAsStateWithLifecycle(initialValue = false)
    val is_menu_expanded: MutableState<Boolean> = remember { mutableStateOf(false) }

    val snackbar_host_state = remember { SnackbarHostState() }

    val modal_dialog_host_state = remember { Modal_dialog_host_state() }

    val location_permission_dont_ask_again_checked_state: MutableState<Boolean> =
        remember { mutableStateOf(false) }
    val download_over_wifi_only_checked_state =
        remember { mutableStateOf(context.resources.getBoolean(R.bool.download_over_wifi_only_default)) }
    val auto_hide_toolbars_checked_state =
        remember { mutableStateOf(context.resources.getBoolean(R.bool.auto_hide_toolbars_default)) }

    val location_text_field_value: MutableState<TextFieldValue> = remember { mutableStateOf(TextFieldValue()) }
    val location_permissions_state = rememberMultiplePermissionsState(listOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                                                             android.Manifest.permission.ACCESS_FINE_LOCATION)) {
        get_location_and_update(context = context,
                                modal_dialog_host_state = modal_dialog_host_state,
                                main_view_model = main_view_model,
                                snackbar_host_state = snackbar_host_state,
                                location_text = location_text_field_value)
    }

    val weather_data_state = main_view_model.weather_data.observeAsState()

    val is_refreshing: Boolean by main_view_model.is_refreshing.collectAsStateWithLifecycle(initialValue = false)
    val pull_to_refresh_state = rememberPullToRefreshState()

    // TODO:
    val window_size_class: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass;
    val is_larger_window = window_size_class.windowWidthSizeClass != WindowWidthSizeClass.COMPACT;

    // TODO: Move some of these to the action bar for large screens...
    val action_menu_items: List<Action_menu_item> = remember {
        listOf(Action_menu_item(title = R.string.action_set_dart_mode_theme,
                                title_condensed = R.string.action_set_dart_mode_theme_condensed,
                                description = R.string.action_set_dart_mode_theme,
                                icon = R.drawable.outline_dark_mode_32,
                                on_clicked = {
                                    action_set_dark_mode_theme(context = context,
                                                               main_view_model = main_view_model,
                                                               modal_dialog_host_state = modal_dialog_host_state,
                                                               window_insets_controller = window_insets_controller)
                                }),
               Action_menu_item(title = R.string.action_set_forcast_location,
                                title_condensed = R.string.action_set_forcast_location_condensed,
                                description = R.string.action_set_forcast_location,
                                icon = R.drawable.twotone_map_32,
                                on_clicked = {
                                    action_set_forcast_location(context = context,
                                                                main_view_model = main_view_model,
                                                                snackbar_host_state = snackbar_host_state,
                                                                modal_dialog_host_state = modal_dialog_host_state,
                                                                window_insets_controller = window_insets_controller,
                                                                location_text_field_value = location_text_field_value,
                                                                location_permissions_state = location_permissions_state,
                                                                location_permission_dont_ask_again_state = location_permission_dont_ask_again_checked_state)
                                }),
               Action_menu_item(title = R.string.action_download_over_wifi_only,
                                title_condensed = R.string.action_download_over_wifi_only_condensed,
                                description = R.string.action_download_over_wifi_only,
                                icon = R.drawable.outline_signal_wifi_off_32,
                                checked_state = download_over_wifi_only_checked_state,
                                on_checked = { checked ->
                                    download_over_wifi_only_checked_state.value = checked
                                    User_settings.store_download_over_wifi_only(context,
                                                                                main_view_model.viewModelScope,
                                                                                checked)
                                },
                                on_clicked = {
                                    download_over_wifi_only_checked_state.value = !download_over_wifi_only_checked_state.value
                                    User_settings.store_download_over_wifi_only(context,
                                                                                main_view_model.viewModelScope,
                                                                                download_over_wifi_only_checked_state.value)
                                }),
               Action_menu_item(title = R.string.action_auto_hide_toolbars,
                                title_condensed = R.string.action_auto_hide_toolbars_condensed,
                                description = R.string.action_auto_hide_toolbars,
                                icon = R.drawable.outline_hide_32,
                                checked_state = auto_hide_toolbars_checked_state,
                                on_checked = { checked ->
                                    auto_hide_toolbars_checked_state.value = checked
                                    User_settings.store_auto_hide_toolbars(context,
                                                                           main_view_model.viewModelScope,
                                                                           checked)
                                },
                                on_clicked = {
                                    auto_hide_toolbars_checked_state.value = !auto_hide_toolbars_checked_state.value
                                    User_settings.store_download_over_wifi_only(context,
                                                                                main_view_model.viewModelScope,
                                                                                auto_hide_toolbars_checked_state.value)
                                }),
               Action_menu_item(title = R.string.action_about,
                                title_condensed = R.string.action_about_condensed,
                                description = R.string.action_about,
                                icon = R.drawable.outline_info_32,
                                on_clicked = { main_view_model.show_about_dialog() }),
               Action_menu_item(title = R.string.action_exit,
                                title_condensed = R.string.action_exit_condensed,
                                description = R.string.action_exit,
                                icon = R.drawable.outline_exit_to_app_32,
                                on_clicked = {
                                    action_exit(context = context,
                                                main_view_model = main_view_model,
                                                snackbar_host_state = snackbar_host_state)
                                }))
    }

    val action_bar_items: List<Action_menu_item> = remember {
        listOf(Action_menu_item(title = R.string.action_refresh_weather_data,
                                title_condensed = R.string.action_refresh_weather_data_condensed,
                                description = R.string.action_refresh_weather_data,
                                icon = R.drawable.outline_refresh_32,
                                on_clicked = {
                                    action_refresh_weather_data(context = context,
                                                                main_view_model = main_view_model,
                                                                snackbar_host_state = snackbar_host_state)
                                }))
    }

    LaunchedEffect(Unit)
    {
        User_settings.load_location_permission_dont_ask_again(context = context,
                                                              scope = main_view_model.viewModelScope) { is_location_permission_dont_ask_again ->
            location_permission_dont_ask_again_checked_state.value = is_location_permission_dont_ask_again
        }

        User_settings.load_download_over_wifi_only(context = context,
                                                   scope = main_view_model.viewModelScope) { is_download_over_wifi_only ->
            download_over_wifi_only_checked_state.value = is_download_over_wifi_only
            action_menu_items[2].checked_state!!.value = download_over_wifi_only_checked_state.value;
        }

        User_settings.load_auto_hide_toolbars(context = context,
                                              scope = main_view_model.viewModelScope) { is_auto_hide_toolbars ->
            auto_hide_toolbars_checked_state.value = is_auto_hide_toolbars
            action_menu_items[3].checked_state!!.value = auto_hide_toolbars_checked_state.value;
        }
    }

    Main_theme(is_dark_theme = is_dark_mode.value) {
        Scaffold(modifier = Modifier
                .fillMaxSize()
                .fillMaxSize()
                .clickable {
                    main_view_model.toggle(context = context,
                                           window_insets_controller = window_insets_controller,
                                           are_system_bars_visible = are_system_bars_visible)
                },
                 snackbarHost = {
                     SnackbarHost(hostState = snackbar_host_state)
                 }) { paddingValues ->
            Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding())) {
                if(modal_dialog_host_state.m_is_modal_dialog_visible)
                {
                    Modal_dialog_host(host_state = modal_dialog_host_state)
                }

                if(is_show_about_dialog)
                {
                    About_dialog(on_ok = main_view_model::about_dialog_ok,
                                 on_cancel = main_view_model::about_dialog_cancel);
                }

                AnimatedVisibility(are_system_bars_visible.value) {
                    Top_app_bar(scroll_behavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState()),
                                is_menu_expanded = is_menu_expanded,
                                action_menu_items = action_menu_items,
                                action_bar_items = action_bar_items,
                                on_arrow_back = {
                                    action_exit(context = context,
                                                main_view_model = main_view_model,
                                                snackbar_host_state = snackbar_host_state)
                                },
                                on_menu_expanded = {
                                    main_view_model.cancel_next_toolbars_auto_hide()
                                },
                                on_menu_dismissed = {
                                    main_view_model.hide_toolbars_if_auto_hide_with_small_delay_workaround(context = context,
                                                                                                           window_insets_controller = window_insets_controller)
                                },
                                on_action_taken = {
                                    main_view_model.hide_toolbars_if_auto_hide_with_small_delay_workaround(context = context,
                                                                                                           window_insets_controller = window_insets_controller)
                                })
                }

                PullToRefreshBox(
                    modifier = Modifier,
                    state = pull_to_refresh_state,
                    isRefreshing = is_refreshing,
                    onRefresh = {
                          Log.d(LOG_TAG, ">>> refreshing")
                        main_view_model.refresh(context = context,
                                                snackbar_host_state = snackbar_host_state)
                    })
                {
                    val weather_data: Weather_data? = weather_data_state.value;
                    if(weather_data != null)
                    {
                        Scrolling_content(weather_data = weather_data,
                                      is_larger_window = is_larger_window)
                    }
                    else
                    {
                        Text(modifier = Modifier
                            .fillMaxSize()
                            .wrapContentHeight(align = Alignment.CenterVertically)
                            .wrapContentWidth(align = Alignment.CenterHorizontally),
                             text = stringResource(id = R.string.unable_to_get_weather_data),
                             style = Typography.bodyLarge);
                    }
                }
            }
        }
    }
}

@Composable
private fun Scrolling_content(weather_data: Weather_data,
                              is_larger_window: Boolean)
{
    val divider_thickness = if(!is_larger_window)
    {
        4.dp
    }
    else
    {
        8.dp
    }

    LazyColumn(modifier = Modifier
                .fillMaxSize()
                .padding(start = 5.dp,
                         end = 5.dp),
                   verticalArrangement = Arrangement.spacedBy(10.dp))
    {
        item()
        {
            Spacer(modifier = Modifier.height(20.dp));
        }

        item()
        {
            Header(weather_data = weather_data,
                   is_larger_window = is_larger_window);
        }

        item()
        {
            Current_conditions(weather_data = weather_data,
                               is_larger_window = is_larger_window,
                               divider_thickness = divider_thickness);
        }

        item()
        {
            HorizontalDivider(modifier = Modifier.fillMaxWidth().height(divider_thickness))
        }

//        item()
//        {
//            Forecast(weather_data = weather_data,
//                     is_larger_window = is_larger_window);
//        }
//
//        item()
//        {
//            HorizontalDivider(modifier = Modifier.fillMaxWidth().height(divider_thickness))
//        }

        item()
        {
            All_data(weather_data = weather_data);
        }
    }
}

@Preview(showBackground = true,
         device = Devices.PIXEL_4)
@Composable
fun Current_conditions_preview()
{
    Scrolling_content(weather_data = test_data(),
                       is_larger_window = false)
}

@Preview(showBackground = true,
         device = Devices.PIXEL_TABLET)
@Composable
fun Current_conditions_large_preview()
{
    Scrolling_content(weather_data = test_data(),
                       is_larger_window = true)
}

fun test_data(): Weather_data
{
    val data_as_string = """
            {
                "DID": "00:1D:0A:00:98:03",
                "bar_trend": "Rising Slowly",
                "barometer": 30.12,
                "barometer_units": "in Hg",
                "console_battery_voltage": 4.65234375,
                "console_battery_voltage_units": "Volts",
                "daily_et": 0.0,
                "daily_hi_wind_speed": 7,
                "daily_high_barometer": 30.121,
                "daily_low_barometer": 30.046,
                "daily_rain": 1.3,
                "day_hi_dew_point": 76,
                "day_hi_humidity": 84,
                "day_hi_in_hum": 54,
                "day_hi_inside_temp": 76.5,
                "day_hi_out_temp": 86.4,
                "day_high_heat": 95,
                "day_high_rain_rate": 0.5,
                "day_low_dew_point": 58,
                "day_low_humidity": 63,
                "day_low_in_hum": 50,
                "day_low_inside_temp": 73.8,
                "day_low_out_temp": 68.3,
                "day_low_wind_chill": 68,
                "dew_point": 73,
                "firmware_date_code": "May 1 2012",
                "firmware_version": "3.0",
                "heat_index": 93,
                "heat_index_derived": 93.23203180110002,
                "hour_high_rain_rate": 0.0,
                "humidity_units": "%",
                "inside_humidity": 54,
                "inside_temperature": 76.5,
                "largest_number_packets_received_in_a_row": 1154,
                "last_fifteen_min_rain": 0.6,
                "last_hour_rain": 2.0,
                "last_twenty_four_hour_rain": 5.0,
                "month_hi_dew_point": 80,
                "month_hi_humidity": 87,
                "month_hi_in_hum": 63,
                "month_hi_in_temp": 78.9,
                "month_hi_out_temp": 97.6,
                "month_hi_wind_speed": 21,
                "month_high_bar": 30.121,
                "month_high_heat": 116,
                "month_high_rain_rate": 7.89,
                "month_low_bar": 29.603,
                "month_low_dew_point": 55,
                "month_low_humidity": 41,
                "month_low_in_hum": 48,
                "month_low_in_temp": 72.1,
                "month_low_out_temp": 63.7,
                "month_low_wind_chill": 64,
                "month_rain": 3.2,
                "number_of_CRC_errors_detected": 9,
                "number_of_resynchronizations": 0,
                "outside_humidity": 64,
                "outside_temperature": 86.3,
                "rain_rate": 0.15,
                "rain_rate_units": "in/hr",
                "rain_units": "in",
                "start_date_of_current_storm": "2127-15-31",
                "storm_rain": 0.0,
                "system_name": "Weather Station Donna @ Hot Springs, AR",
                "temperature_units": "&#x00B0;F",
                "ten_min_avg_wind_speed": 1.5,
                "ten_min_wind_gust": 0.7,
                "time": "2024-07-13T15:54:16Z",
                "time_day_hi_dew_point": "2024-07-13T14:32:00Z",
                "time_day_hi_humidity": "2024-07-13T06:53:00Z",
                "time_day_hi_in_hum": "2024-07-13T13:31:00Z",
                "time_day_hi_in_temp": "2024-07-13T15:44:00Z",
                "time_day_hi_out_temp": "2024-07-13T15:40:00Z",
                "time_day_low_chill": "2024-07-13T10:48:00Z",
                "time_day_low_dew_point": "2024-07-13T11:42:00Z",
                "time_day_low_humidity": "2024-07-13T15:45:00Z",
                "time_day_low_in_hum": "2024-07-13T05:00:00Z",
                "time_day_low_in_temp": "2024-07-13T06:21:00Z",
                "time_day_low_out_temp": "2024-07-13T11:29:00Z",
                "time_of_day_high_bar": "2024-07-13T15:52:00Z",
                "time_of_day_high_heat": "2024-07-13T14:38:00Z",
                "time_of_day_high_rain_rate": "2024-07-13T03:59:00Z",
                "time_of_day_low_bar": "2024-07-13T05:21:00Z",
                "time_of_hi_speed": "2024-07-13T15:45:00Z",
                "total_packets_missed": 32,
                "total_packets_received": 8096,
                "transmitter_battery_status": "OK",
                "two_min_avg_wind_speed": 3.9,
                "wind_chill": 86,
                "wind_chill_derived": 86.3,
                "wind_direction": 275,
                "wind_direction_of_ten_min_wind_gust": 270,
                "wind_direction_units": "&#x00B0;",
                "wind_speed": 5,
                "wind_speed_units": "MPH",
                "wrd": 17,
                "year_hi_dew_point": 82,
                "year_hi_humidity": 97,
                "year_hi_in_hum": 72,
                "year_hi_in_temp": 79.0,
                "year_hi_out_temp": 98.6,
                "year_hi_wind_speed": 40,
                "year_high_barometer": 30.644,
                "year_high_heat": 121,
                "year_high_rain_rate": 15.57,
                "year_low_barometer": 29.314,
                "year_low_dew_point": -3,
                "year_low_humidity": 15,
                "year_low_in_hum": 24,
                "year_low_in_temp": 60.0,
                "year_low_out_temp": 3.7,
                "year_low_wind_chill": 59,
                "year_rain": 43.84
            }
        """.trimIndent()

    return Weather_data.deserialize_from_JSON(data_as_string) ?: Weather_data()
}

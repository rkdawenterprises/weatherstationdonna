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

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.State
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.ddns.rkdawenterprises.rkdawe_api_common.API_paths
import net.ddns.rkdawenterprises.rkdawe_api_common.Get_API_paths_GET_response
import net.ddns.rkdawenterprises.rkdawe_api_common.Get_weather_station_data_GET_response
import net.ddns.rkdawenterprises.rkdawe_api_common.RKDAWE_API
import net.ddns.rkdawenterprises.rkdawe_api_common.Weather_data
import net.ddns.rkdawenterprises.weatherstationdonna.R
import net.ddns.rkdawenterprises.weatherstationdonna.User_settings

class Main_view_model(application: Application) : AndroidViewModel(application)
{
    companion object
    {
        @Suppress("unused")
        private const val LOG_TAG = "Main_view_model";

        /**
         * Trigger the initial toolbar hide shortly after the activity has been created.
         * Time is in milliseconds.
         */
        const val INITIAL_HIDE_DELAY = 500;

        /**
         * The time to delay before automatically hiding the toolbars if auto-hide is enabled.
         * Time is in milliseconds.
         */
        const val AUTO_HIDE_TOOLBARS_DELAY = 3500;
    }

    fun toggle(context: Context,
               are_system_bars_visible: State<Boolean>,
               window_insets_controller: WindowInsetsControllerCompat)
    {
        if(are_system_bars_visible.value)
        {
            hide_toolbars(window_insets_controller = window_insets_controller);
        }
        else
        {
            show_toolbars_with_auto_hide(context = context,
                                         window_insets_controller = window_insets_controller);
        }
    }

    fun show_toolbars_with_auto_hide(context: Context,
                                     window_insets_controller: WindowInsetsControllerCompat,
                                     delay_in_milliseconds: Int = AUTO_HIDE_TOOLBARS_DELAY)
    {
        show_toolbars(window_insets_controller = window_insets_controller)
        User_settings.load_auto_hide_toolbars(context = context,
                                              scope = this.viewModelScope) { auto_hide_toolbars ->
            if(auto_hide_toolbars)
            {
                start_delayed_hide_timer(delay_in_milliseconds = delay_in_milliseconds,
                                         window_insets_controller = window_insets_controller);
            }
        }
    }

    private fun hide_toolbars_if_auto_hide(context: Context,
                                           window_insets_controller: WindowInsetsControllerCompat)
    {
        User_settings.load_auto_hide_toolbars(context = context,
                                              scope = this.viewModelScope) { auto_hide_toolbars ->
            if(auto_hide_toolbars)
            {
                cancel_next_toolbars_auto_hide()
                hide_toolbars(window_insets_controller = window_insets_controller);
            }
        }
    }

    fun cancel_next_toolbars_auto_hide()
    {
        if(m_show_hide_handler == null) return;

        if(hide_toolbars_runnable != null)
        {
            m_show_hide_handler!!.removeCallbacks(hide_toolbars_runnable!!);
            hide_toolbars_runnable = null;
        }
    }

    fun hide_toolbars_if_auto_hide_with_small_delay_workaround(context: Context,
                                                               window_insets_controller: WindowInsetsControllerCompat)
    {
        // After menu closes with an action, the system bars will not hide without a little delay...
        Handler(Looper.getMainLooper()).postDelayed({
                                                        hide_toolbars_if_auto_hide(context = context,
                                                                                   window_insets_controller = window_insets_controller)
                                                    },
                                                    100)
    }

    var m_show_hide_handler: Handler? = null

    private var hide_toolbars_runnable: Runnable? = null

    private fun start_delayed_hide_timer(delay_in_milliseconds: Int,
                                         window_insets_controller: WindowInsetsControllerCompat)
    {
        if(m_show_hide_handler == null) return;

        if(hide_toolbars_runnable != null)
        {
            m_show_hide_handler!!.removeCallbacks(hide_toolbars_runnable!!);
            hide_toolbars_runnable = null;
        }

        hide_toolbars_runnable = Runnable { hide_toolbars(window_insets_controller = window_insets_controller); }
        m_show_hide_handler!!.postDelayed(hide_toolbars_runnable!!,
                                          delay_in_milliseconds.toLong());
    }

    private fun show_toolbars(window_insets_controller: WindowInsetsControllerCompat)
    {
        window_insets_controller.show(WindowInsetsCompat.Type.systemBars());
        set_are_system_bars_visible(true);
    }

    private fun hide_toolbars(window_insets_controller: WindowInsetsControllerCompat)
    {
        set_are_system_bars_visible(false);
        window_insets_controller.hide(WindowInsetsCompat.Type.systemBars());
    }

    private val m_are_system_bars_visible = MutableStateFlow(true);
    val are_system_bars_visible: StateFlow<Boolean> get() = m_are_system_bars_visible.asStateFlow();

    private fun set_are_system_bars_visible(visible: Boolean)
    {
        m_are_system_bars_visible.value = visible;
    }

    private val m_is_show_about_dialog = MutableStateFlow(false);
    val is_show_about_dialog: StateFlow<Boolean> get() = m_is_show_about_dialog.asStateFlow();

    fun show_about_dialog()
    {
        m_is_show_about_dialog.value = true;
    }

    fun about_dialog_ok()
    {
        m_is_show_about_dialog.value = false;
    }

    fun about_dialog_cancel()
    {
        m_is_show_about_dialog.value = false;
    }

    private val m_rkdawe_API = MutableLiveData<API_paths>();
    val rkdawe_API: LiveData<API_paths> get() = m_rkdawe_API;

    private val m_weather_data = MutableLiveData<Weather_data>();
    val weather_data: LiveData<Weather_data> get() = m_weather_data;

    private val m_is_refreshing = MutableStateFlow(false);
    val is_refreshing: StateFlow<Boolean> get() = m_is_refreshing.asStateFlow();

    fun refresh(stored_data: Weather_data)
    {
        m_weather_data.value = stored_data;
    }

    fun refresh(context: Context,
                snackbar_host_state: SnackbarHostState? = null)
    {
        if(!m_is_refreshing.value)
        {
            User_settings.load_forecast_location_setting(context = context,
                                                         scope = viewModelScope)
            { forecast_location_setting ->
                if(forecast_location_setting.isEmpty())
                {
                    if(snackbar_host_state != null)
                    {
                        logging_ok_snackbar(context = context,
                                            main_view_model = this@Main_view_model,
                                            snackbar_host_state = snackbar_host_state,
                                            title_message = context.resources.getString(R.string.please_set_forecast_location))
                    }
                }

                viewModelScope.launch {
                    m_is_refreshing.emit(true);

                    try
                    {
                        val paths = RKDAWE_API.m_RKDAWE_API_service.update_paths()

                        val update_paths = Get_API_paths_GET_response.deserialize_from_JSON(paths)
                        if((update_paths != null) && (update_paths.success == "true"))
                        {
                            m_rkdawe_API.value = update_paths.paths
                        }

                        val weather_station_data_path = update_paths.paths.weather_station_data_path

                        val data = if(forecast_location_setting.isEmpty())
                        {
                            RKDAWE_API.m_RKDAWE_API_service.get_weather_station_data(path = weather_station_data_path)
                        }
                        else
                        {
                            RKDAWE_API.m_RKDAWE_API_service.get_weather_station_data(path = weather_station_data_path,
                                                                                                forecast_location = forecast_location_setting)
                        }

                        val get_weather_station_data = Get_weather_station_data_GET_response.deserialize_from_JSON(data);
                        if((get_weather_station_data != null) && (get_weather_station_data.success == "true"))
                        {
                            m_weather_data.value = get_weather_station_data.weather_data;
                        }
                    }
                    catch(exception: Exception)
                    {
                        if(snackbar_host_state != null)
                        {
                            logging_ok_snackbar(context = context,
                                                main_view_model = this@Main_view_model,
                                                snackbar_host_state = snackbar_host_state,
                                                title_message = context.resources.getString(R.string.unable_to_get_weather_data),
                                                logging_message = "Failure fetching RKDAWE weather data: ${exception.message}")
                        }
                    }

                    m_is_refreshing.emit(false);
                }
            }
        }
    }
}

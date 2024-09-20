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

package net.ddns.rkdawenterprises.weatherstationdonna

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewModelScope
import net.ddns.rkdawenterprises.weatherstationdonna.UI.Main
import net.ddns.rkdawenterprises.weatherstationdonna.UI.Main_view_model

class Main_activity : ComponentActivity()
{
    companion object
    {
        @Suppress("unused")
        private const val LOG_TAG = "Main_activity"
    }

    private val m_main_view_model: Main_view_model by viewModels();

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        m_main_view_model.m_show_hide_handler = Handler(Looper.myLooper()!!)

        enableEdgeToEdge()

        val window_insets_controller = WindowCompat.getInsetsController(window,
                                                                        window.decorView)
        window_insets_controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        WindowCompat.setDecorFitsSystemWindows(window,
                                               false)

        setContent {
            Main(context = this,
                 window_insets_controller = window_insets_controller,
                 main_view_model = m_main_view_model)
        }
    }

    override fun onPause()
    {
        val weather_data = m_main_view_model.weather_data.value;
        if(weather_data != null)
        {
            User_settings.store_last_weather_data_fetched(context = this,
                                                          scope = m_main_view_model.viewModelScope,
                                                          value = weather_data)
        }

        super.onPause()
    }

    override fun onResume()
    {
        super.onResume();

        User_settings.load_last_weather_data_fetched(context = this,
                                                     scope = m_main_view_model.viewModelScope) { last_weather_data_fetched ->
            if(last_weather_data_fetched != null) m_main_view_model.refresh(last_weather_data_fetched)
        }

        m_main_view_model.show_toolbars_with_auto_hide(context = this,
                                                       window_insets_controller = WindowCompat.getInsetsController(window,
                                                                        window.decorView),
                                                       delay_in_milliseconds = Main_view_model.INITIAL_HIDE_DELAY)

        User_settings.is_ok_to_fetch_data(context = this,
                                          scope = m_main_view_model.viewModelScope) { is_ok_to_fetch_data ->
            if(is_ok_to_fetch_data)
            {
                m_main_view_model.refresh(context = this);
            }
        }
    }
}

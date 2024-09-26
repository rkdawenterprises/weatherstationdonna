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

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.ddns.rkdawenterprises.rkdawe_api_common.Weather_data

object User_settings
{
    @Suppress("unused")
    private const val LOG_TAG = "User_settings"

    private val Context.user_preferences_data_store: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

    /**
     * Dark mode selection preferences storage key.
     */
    private val DARK_MODE_SELECTION_KEY = intPreferencesKey("dark_mode_selection");

    /**
     * Dark mode selection preferences storage key.
     */
    private val FORECAST_LOCATION_SETTING_KEY = stringPreferencesKey("forecast_location_setting");

    /**
     * Location permissions don't ask again preferences storage key.
     */
    private val LOCATION_PERMISSION_DONT_ASK_AGAIN_KEY = booleanPreferencesKey("location_permission_dont_ask_again");

    /**
     * Download over WiFi only preferences storage key.
     */
    private val DOWNLOAD_OVER_WIFI_ONLY_KEY = booleanPreferencesKey("download_over_wifi_only");

    /**
     * Auto-hide toolbars preferences storage key.
     */
    private val AUTO_HIDE_TOOLBARS_KEY = booleanPreferencesKey("auto_hide_toolbars");

    /**
     * Last weather data fetched successfully.
     */
    private val LAST_WEATHER_DATA_FETCHED_KEY = stringPreferencesKey("last_weather_data_fetched")

    fun load_and_update_dark_mode_selection(context: Context,
                                            scope: CoroutineScope,
                                            function: () -> Unit)
    {
        load_dark_mode_selection(context,
                                 scope) {
            update_dark_mode(context,
                             it)
            function.invoke()
        }
    }

    fun load_dark_mode_selection(context: Context,
                                 scope: CoroutineScope,
                                 function: (Int) -> Unit)
    {
        scope.launch()
        {
            function(load_dark_mode_selection(context).first());
        }
    }

    fun store_update_dark_mode_selection(context: Context,
                                         scope: CoroutineScope,
                                         selection: Int)
    {
        scope.launch()
        {
            store_dark_mode_selection(context,
                                      selection);
            update_dark_mode(context,
                             selection);
        }
    }

    fun load_forecast_location_setting(context: Context,
                                       scope: CoroutineScope,
                                       function: (String) -> Unit)
    {
        scope.launch()
        {
            function(load_forecast_location_setting(context).first());
        }
    }

    fun store_forecast_location_setting(context: Context,
                                        scope: CoroutineScope,
                                        location: String)
    {
        scope.launch()
        {
            store_forecast_location_setting(context,
                                            location);
        }
    }

    fun load_download_over_wifi_only(context: Context,
                                     scope: CoroutineScope,
                                     function: (Boolean) -> Unit)
    {
        scope.launch()
        {
            function(load_download_over_wifi_only(context).first());
        }
    }

    fun store_download_over_wifi_only(context: Context,
                                      scope: CoroutineScope,
                                      value: Boolean)
    {
        scope.launch()
        {
            store_download_over_wifi_only(context,
                                          value);
        }
    }

    fun load_location_permission_dont_ask_again(context: Context,
                                     scope: CoroutineScope,
                                     function: (Boolean) -> Unit)
    {
        scope.launch()
        {
            function(load_location_permission_dont_ask_again(context).first());
        }
    }

    fun store_location_permission_dont_ask_again(context: Context,
                                      scope: CoroutineScope,
                                      value: Boolean)
    {
        scope.launch()
        {
            store_location_permission_dont_ask_again(context,
                                          value);
        }
    }

    fun load_auto_hide_toolbars(context: Context,
                                scope: CoroutineScope,
                                function: (Boolean) -> Unit)
    {
        scope.launch()
        {
            function(load_auto_hide_toolbars(context).first());
        }
    }

    fun store_auto_hide_toolbars(context: Context,
                                 scope: CoroutineScope,
                                 value: Boolean)
    {
        scope.launch()
        {
            store_auto_hide_toolbars(context,
                                     value);
        }
    }

    fun load_last_weather_data_fetched(context: Context,
                                       scope: CoroutineScope,
                                       function: (Weather_data?) -> Unit)
    {
        scope.launch()
        {
            val value = load_last_weather_data_fetched(context).first()
            function(value);
        }
    }

    fun store_last_weather_data_fetched(context: Context,
                                        scope: CoroutineScope,
                                        value: Weather_data)
    {
        scope.launch()
        {
            store_last_weather_data_fetched(context,
                                            value);
        }
    }

    fun is_ok_to_fetch_data(context: Context,
                            scope: CoroutineScope,
                            function: (Boolean) -> Unit)
    {
        scope.launch()
        {
            function(is_ok_to_fetch_data(context).first());
        }
    }

    fun is_application_in_dark_mode(context: Context): Flow<Boolean>
    {
        return context.user_preferences_data_store.data.map { preferences ->
            val selections = context.resources.getStringArray(R.array.dark_mode_options);
            val selection = preferences[DARK_MODE_SELECTION_KEY]
                ?: context.resources.getInteger(R.integer.dark_mode_selection_default);

            if(selections[selection].contains("dark",
                                              true))
            {
                true;
            }
            else if(selections[selection].contains("light",
                                                   true))
            {
                false;
            }
            else    // Follow system...
            {
                is_system_in_dark_mode(context);
            }
        }
    }

    private fun is_ok_to_fetch_data(context: Context): Flow<Boolean>
    {
        return context.user_preferences_data_store.data.map { preferences ->
            var is_metered = true;

            val connectivity_manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager;
            val network_capabilities = connectivity_manager.getNetworkCapabilities(connectivity_manager.activeNetwork);
            if(network_capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == false) is_metered = false;
            if(network_capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED) == true) is_metered = false;

            val download_over_wifi_only = preferences[DOWNLOAD_OVER_WIFI_ONLY_KEY]
                ?: context.resources.getBoolean(R.bool.download_over_wifi_only_default)
            val is_ok = !(is_metered && download_over_wifi_only);
            is_ok;
        }
    }

    private fun load_dark_mode_selection(context: Context): Flow<Int>
    {
        return context.user_preferences_data_store.data.map { preferences ->
            val selection = preferences[DARK_MODE_SELECTION_KEY]
                ?: context.resources.getInteger(R.integer.dark_mode_selection_default)
            selection
        }
    }

    private suspend fun store_dark_mode_selection(context: Context,
                                                  selection: Int)
    {
        context.user_preferences_data_store.edit { preferences ->
            preferences[DARK_MODE_SELECTION_KEY] = selection;
        }
    }

    private fun load_forecast_location_setting(context: Context): Flow<String>
    {
        return context.user_preferences_data_store.data.map { preferences ->
            val location = preferences[FORECAST_LOCATION_SETTING_KEY]
                ?: "" /*context.resources.getString(R.string.forecast_location_setting_default)*/
            location
        }
    }

    private suspend fun store_forecast_location_setting(context: Context,
                                                        location: String)
    {
        context.user_preferences_data_store.edit { preferences ->
            preferences[FORECAST_LOCATION_SETTING_KEY] = location;
        }
    }

    private fun load_location_permission_dont_ask_again(context: Context): Flow<Boolean>
    {
        return context.user_preferences_data_store.data.map { preferences ->
            val value = preferences[LOCATION_PERMISSION_DONT_ASK_AGAIN_KEY]
                ?: false;
            value
        }
    }

    private suspend fun store_location_permission_dont_ask_again(context: Context,
                                                      value: Boolean)
    {
        context.user_preferences_data_store.edit { preferences ->
            preferences[LOCATION_PERMISSION_DONT_ASK_AGAIN_KEY] = value;
        }
    }

    private fun load_download_over_wifi_only(context: Context): Flow<Boolean>
    {
        return context.user_preferences_data_store.data.map { preferences ->
            val value = preferences[DOWNLOAD_OVER_WIFI_ONLY_KEY]
                ?: context.resources.getBoolean(R.bool.download_over_wifi_only_default);
            value
        }
    }

    private suspend fun store_download_over_wifi_only(context: Context,
                                                      value: Boolean)
    {
        context.user_preferences_data_store.edit { preferences ->
            preferences[DOWNLOAD_OVER_WIFI_ONLY_KEY] = value;
        }
    }

    private fun load_auto_hide_toolbars(context: Context): Flow<Boolean>
    {
        return context.user_preferences_data_store.data.map { preferences ->
            val value = preferences[AUTO_HIDE_TOOLBARS_KEY]
                ?: context.resources.getBoolean(R.bool.auto_hide_toolbars_default)
            value
        }
    }

    private suspend fun store_auto_hide_toolbars(context: Context,
                                                 value: Boolean)
    {
        context.user_preferences_data_store.edit { preferences ->
            preferences[AUTO_HIDE_TOOLBARS_KEY] = value;
        }
    }

    private fun load_last_weather_data_fetched(context: Context): Flow<Weather_data?>
    {
        return context.user_preferences_data_store.data.map { preferences ->
            val data_as_string: String? = preferences[LAST_WEATHER_DATA_FETCHED_KEY];
            if(data_as_string == null) null
            Weather_data.deserialize_from_JSON(data_as_string)
        }
    }

    private suspend fun store_last_weather_data_fetched(context: Context,
                                                        value: Weather_data)
    {
        val data_as_string = value.serialize_to_JSON()
        context.user_preferences_data_store.edit { preferences ->
            if(data_as_string != null)
            {
                preferences[LAST_WEATHER_DATA_FETCHED_KEY] = data_as_string
            }
        }
    }
}

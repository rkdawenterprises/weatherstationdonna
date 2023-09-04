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
               "PackageName")

package net.ddns.rkdawenterprises.weatherstationdonna.UI

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.ddns.rkdawenterprises.davis_website.Davis_API
import net.ddns.rkdawenterprises.davis_website.Weather_page
import net.ddns.rkdawenterprises.rkdawe_api_common.Get_weather_station_data_GET_response
import net.ddns.rkdawenterprises.rkdawe_api_common.RKDAWE_API
import net.ddns.rkdawenterprises.rkdawe_api_common.Weather_data
import net.ddns.rkdawenterprises.weather_gov_api.Weather_gov_data
import net.ddns.rkdawenterprises.weather_gov_api.Weather_gov_API
import net.ddns.rkdawenterprises.weatherstationdonna.R
import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sqrt


/**
 * Provides a combined live data from four different servers.
 */
class Main_view_model(application: Application) : AndroidViewModel(application)
{
    /**
     * Last "successful" fetched weather data. Does not store the failed fetches.
     */
    class Data_storage(first_status: String? = null,
                       first_data: String? = null,
                       second_status: String? = null,
                       second_data: String? = null,
                       third_status: String? = null,
                       third_data: String? = null,
                       fourth_status: String? = null,
                       fourth_data: String? = null)
    {
        companion object
        {
            @Suppress("unused")
            private const val LOG_TAG = "Data_storage";

            private val s_GSON: Gson = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()

            fun serialize_to_JSON(`object`: Data_storage?): String?
            {
                return s_GSON.toJson(`object`)
            }

            fun deserialize_from_JSON(string_JSON: String): Data_storage?
            {
                var instance: Data_storage? = null

                try
                {
                    @Suppress("unused") instance = s_GSON.fromJson(string_JSON,
                                                                   Data_storage::class.java)
                }
                catch(exception: JsonSyntaxException)
                {
                    Log.d(LOG_TAG,
                          "Bad data format for Weather_data: $exception")
                    Log.d(LOG_TAG,
                          ">>>$string_JSON<<<")
                }

                return instance
            }
        }

        init
        {
            set_data_RKDAWE(first_status,
                            first_data);
            set_data_davis(second_status,
                           second_data);
            set_page_davis(third_status,
                           third_data);
            set_data_gov(fourth_status,
                         fourth_data);
        }

        var m_data_RKDAWE: Weather_data? = null
            private set
        var m_data_davis: net.ddns.rkdawenterprises.davis_website.Weather_data? = null
            private set
        var m_page_davis: Weather_page? = null
            private set
        var m_data_weather_gov: Weather_gov_data? = null
            private set

        @Suppress("unused",
                  "MemberVisibilityCanBePrivate")
        fun set_data_RKDAWE(status: String?,
                            data: String?): Boolean
        {
            if(status == "success")
            {
//                Log.d(LOG_TAG, "Got RKDAWE data...")
                val response = Get_weather_station_data_GET_response.deserialize_from_JSON(data);
                if((response != null) && (response.success == "true"))
                {
                    m_data_RKDAWE = response.weather_data;
                    return false;
                }
            }

            return true;
        }

        @Suppress("unused",
                  "MemberVisibilityCanBePrivate")
        fun set_data_davis(status: String?,
                           data: String?): Boolean
        {
            if(status == "success")
            {
//                Log.d(LOG_TAG, "Got davis data...")
                val response = net.ddns.rkdawenterprises.davis_website.Weather_data.deserialize_from_JSON(data);
                if(response != null)
                {
                    m_data_davis = response;
                    return false;
                }
            }

            return true;
        }

        @Suppress("unused",
                  "MemberVisibilityCanBePrivate")
        fun set_page_davis(status: String?,
                           data: String?): Boolean
        {
            if(status == "success")
            {
//                Log.d(LOG_TAG, "Got davis page...")
                val response = Weather_page.deserialize_from_JSON(data);
                if(response != null)
                {
                    m_page_davis = response;
                    return false;
                }
            }

            return true;
        }

        @Suppress("unused",
                  "MemberVisibilityCanBePrivate")
        private fun set_data_gov(status: String?,
                                 data: String?): Boolean
        {
            if(status == "success")
            {
//                Log.d(LOG_TAG, "Got weather.gov data...")
                val response = Weather_gov_data.deserialize_from_JSON(data);
                if(response != null)
                {
                    m_data_weather_gov = response;
                    return false;
                }
            }

            return true;
        }

        fun is_empty(): Boolean
        {
            return ((m_data_RKDAWE == null) && (m_data_davis == null) && (m_page_davis == null) && (m_data_weather_gov == null))
        }

        fun serialize_to_JSON(): String?
        {
            return serialize_to_JSON(this)
        }
    }

    companion object
    {
        @Suppress("unused")
        private const val LOG_TAG = "Main_view_model";

        private const val STATE_IDLE = 0b0000000;
        private const val STATE_STARTED = 0b0000010;
        private const val STATE_FIRST = 0b0000100;
        private const val STATE_SECOND = 0b0001000;
        private const val STATE_THIRD = 0b0010000;
        private const val STATE_FOURTH = 0b0100000;
        private const val STATE_ALL = STATE_STARTED or STATE_FIRST or STATE_SECOND or STATE_THIRD or STATE_FOURTH;
    }

    private val m_state: AtomicInteger = AtomicInteger(STATE_IDLE);

    private fun get_state(): Int
    {
        return m_state.get();
    }

    private fun set_state(state: Int)
    {
        m_state.set(state);
    }

    private fun or_state(state: Int)
    {
        m_state.getAndUpdate { i -> i or state }
    }

    private val m_first_response = MutableLiveData<Array<String>>();

    @Suppress("unused")
    val first_response: LiveData<Array<String>> get() = m_first_response;

    private val m_second_response = MutableLiveData<Array<String>>();

    @Suppress("unused")
    val second_response: LiveData<Array<String>> get() = m_second_response;

    private val m_third_response = MutableLiveData<Array<String>>();

    @Suppress("unused")
    val third_response: LiveData<Array<String>> get() = m_third_response;

    private val m_fourth_response = MutableLiveData<Array<String>>();

    @Suppress("unused")
    val fourth_response: LiveData<Array<String>> get() = m_fourth_response;

    private val m_combined_response = MutableLiveData<Data_storage>();
    val combined_response: LiveData<Data_storage> get() = m_combined_response;

    private val m_is_refreshing = MutableStateFlow(false);
    val is_refreshing: StateFlow<Boolean> get() = m_is_refreshing.asStateFlow();

    private val m_snackbar_message = MutableLiveData<Array<String>>();

    @Suppress("unused")
    val snackbar_message: LiveData<Array<String>> get() = m_snackbar_message;

    fun refresh(stored_data: Data_storage)
    {
        m_combined_response.value = stored_data;
    }

    fun refresh()
    {
        val application_context = getApplication<Application>().applicationContext;

        if(get_state() == STATE_IDLE)
        {
            set_state(STATE_STARTED);

            viewModelScope.launch {
                m_is_refreshing.emit(true);

                var value: Array<String> = try
                {
                    arrayOf("success",
                            RKDAWE_API.m_RKDAWE_API_service.get_weather_station_data());
                }
                catch(exception: Exception)
                {
                    m_snackbar_message.value = arrayOf(application_context.resources.getString(R.string.unable_to_get_weather_data),
                                                       "Failure fetching RKDAWE data: ${exception.message}");
                    arrayOf("failure",
                            "${exception.message}");
                }

                if(value[0] == "failure")
                {
                    value = try
                    {
                        arrayOf("success",
                                RKDAWE_API.m_RKDAWE_API_service.get_weather_station_data());
                    }
                    catch(exception: Exception)
                    {
                        m_snackbar_message.value =
                            arrayOf(application_context.resources.getString(R.string.unable_to_get_weather_data),
                                    "Failure fetching RKDAWE data: ${exception.message}");
                        arrayOf("failure",
                                "${exception.message}");
                    }
                }

                m_first_response.value = value;
                combine_latest_data(STATE_FIRST,
                                    value,
                                    m_second_response.value,
                                    m_third_response.value,
                                    m_fourth_response.value);
            }

            viewModelScope.launch {
                val value: Array<String> = try
                {
                    arrayOf("success",
                            Davis_API.m_davis_API_service.get_weather_station_data());
                }
                catch(exception: Exception)
                {
                    m_snackbar_message.value = arrayOf(application_context.resources.getString(R.string.unable_to_get_weather_data),
                                                       "Failure fetching davis data: ${exception.message}");
                    arrayOf("failure",
                            "${exception.message}");
                }

                m_second_response.value = value;
                combine_latest_data(STATE_SECOND,
                                    m_first_response.value,
                                    value,
                                    m_third_response.value,
                                    m_fourth_response.value);
            }

            viewModelScope.launch {
                val value: Array<String> = try
                {
                    val page_data = Davis_API.m_davis_API_service.get_weather_station_page();
                    val weather_page = Weather_page.scrape_page(page_data);
                    arrayOf("success",
                            weather_page.serialize_to_JSON());
                }
                catch(exception: Exception)
                {
                    m_snackbar_message.value = arrayOf(application_context.resources.getString(R.string.unable_to_get_weather_data),
                                                       "Failure fetching davis page: ${exception.message}");
                    arrayOf("failure",
                            "${exception.message}");
                }

                m_third_response.value = value;
                combine_latest_data(STATE_THIRD,
                                    m_first_response.value,
                                    m_second_response.value,
                                    value,
                                    m_fourth_response.value);
            }

            load_forecast_location_setting(application_context) { forecast_location_setting ->
                viewModelScope.launch() {
                    val value: Array<String> = try
                    {
                        val location = process_location_for_get_points(forecast_location_setting);
                        val points = get_points(location[0],
                                                location[1]);
                        delay(10);

                        val wfo = points[0];
                        val x = points[1];
                        val y = points[2];
                        val city = points[3];
                        val state = points[4];
                        val gridpoints_all = get_gridpoints_all(wfo,
                                                                x,
                                                                y);
                        val gridpoints = gridpoints_all[0];
                        val gridpoints_forecast = gridpoints_all[1];
                        val gridpoints_forecast_hourly = gridpoints_all[2];
                        val gridpoints_stations = gridpoints_all[3];
                        val stations_observations_latest = get_stations_observations(location,
                                                                                     gridpoints_stations);
                        val alerts_active_pretty = get_alerts_active(location[0],
                                                                     location[1]);


                        Log.d(LOG_TAG,
                              "Got all weather.gov data")
                        // TODO: Create JSON object with with what is needed in the UI.

                        arrayOf("success",
                                """
                                    {
                                        "city": "$city",     
                                        "state": "$state",     
                                        "gridpoints": $gridpoints
                                    }
                                """.trimMargin());
                    }
                    catch(exception: Exception)
                    {
                        m_snackbar_message.value =
                            arrayOf(application_context.resources.getString(R.string.unable_to_get_forcast_data),
                                    "Failure fetching location forecast: ${exception.message}");
                        arrayOf("failure",
                                "${exception.message}");
                    }

                    m_fourth_response.value = value;
                    combine_latest_data(STATE_FOURTH,
                                        m_first_response.value,
                                        m_second_response.value,
                                        m_third_response.value,
                                        value);
                }
            }
        }
    }

    private suspend fun get_alerts_active(latitude: String,
                                          longitude: String): String
    {
        val alerts_active_JSON = Weather_gov_API.m_weather_gov_API_service.get_alerts_active("${latitude},${longitude}");
        delay(10);
        val alerts_active = JSONObject(alerts_active_JSON);
        val alerts_active_pretty = alerts_active.toString(4);
        return alerts_active_pretty;
    }

    private suspend fun get_stations_observations(location: Array<String>,
                                                  gridpoints_stations_JSON: String): String
    {
        val station_ID: Array<String> = find_closest_station_ID(location,
                                                                gridpoints_stations_JSON);

        val station_identifier: String = station_ID[0];
        val station_name: String = station_ID[1];
        val observations = Weather_gov_API.m_weather_gov_API_service.get_stations_observations_latest(station_identifier);
        delay(10);
        return observations;
    }

    private fun find_closest_station_ID(location: Array<String>,
                                        gridpoints_stations_JSON: String): Array<String>
    {
        val gridpoints_stations = JSONObject(gridpoints_stations_JSON);
        val features = gridpoints_stations.getJSONArray("features");
        var item = features.getJSONObject(0);
        var properties = item.getJSONObject("properties");
        var closest_station_identifier = properties.getString("stationIdentifier");
        var closest_station_name = properties.getString("name");
        val start_latitude = location[0].toDouble();
        val start_longitude = location[1].toDouble();
        var running_delta: Double? = null;
        for(i in 0 until features.length())
        {
            item = features.getJSONObject(i);
            val geometry = item.getJSONObject("geometry");
            val coordinates = geometry.getJSONArray("coordinates");
            val end_latitude = coordinates.getDouble(1);
            val end_longitude = coordinates.getDouble(0);
            val delta = distance_between_points_km(start_latitude,
                                                   start_longitude,
                                                   end_latitude,
                                                   end_longitude);
            if((running_delta == null) || (running_delta > delta))
            {
                running_delta = delta;
                properties = item.getJSONObject("properties");
                closest_station_identifier = properties.getString("stationIdentifier");
                closest_station_name = properties.getString("name");
            }
        }

        return arrayOf(closest_station_identifier,
                       closest_station_name);
    }

    private fun distance_between_points_km(lat1: Double,
                                           lon1: Double,
                                           lat2: Double,
                                           lon2: Double): Double
    {
        val p = Math.PI / 180;
        return (2 * 6371 * asin(sqrt(0.5 - cos((lat2 - lat1) * p) / 2 + cos(lat1 * p) * cos(lat2 * p) * (1 - cos((lon2 - lon1) * p)) / 2)));
    }

    /**
     * Gets the four different types of gridpoint data.
     */
    private suspend fun get_gridpoints_all(wfo: String,
                                           x: String,
                                           y: String): Array<String>
    {
        val gridpoints_JSON = Weather_gov_API.m_weather_gov_API_service.get_gridpoints(wfo,
                                                                                       x,
                                                                                       y);
        delay(10);
        val gridpoints = JSONObject(gridpoints_JSON);
        val gridpoints_pretty = gridpoints.toString(4);

        val gridpoints_forecast_JSON = Weather_gov_API.m_weather_gov_API_service.get_gridpoints_forecast(wfo,
                                                                                                         x,
                                                                                                         y);
        delay(10);
        val gridpoints_forecast = JSONObject(gridpoints_forecast_JSON);
        val gridpoints_forecast_pretty = gridpoints_forecast.toString(4);

        val gridpoints_forecast_hourly_JSON = Weather_gov_API.m_weather_gov_API_service.get_gridpoints_forecast_hourly(wfo,
                                                                                                                       x,
                                                                                                                       y);
        delay(10);
        val gridpoints_forecast_hourly = JSONObject(gridpoints_forecast_hourly_JSON);
        val gridpoints_forecast_hourly_pretty = gridpoints_forecast_hourly.toString(4);

        val gridpoints_stations_JSON = Weather_gov_API.m_weather_gov_API_service.get_gridpoints_stations(wfo,
                                                                                                         x,
                                                                                                         y);
        delay(10);
        val gridpoints_stations = JSONObject(gridpoints_stations_JSON);
        val gridpoints_stations_pretty = gridpoints_stations.toString(4);

        return arrayOf(gridpoints_pretty,
                       gridpoints_forecast_pretty,
                       gridpoints_forecast_hourly_pretty,
                       gridpoints_stations_pretty);
    }

    /**
     * Formats the forecast location setting string into lat/long strings with 4 decimal digit precision.
     *
     * @param location_string CSV string with latitude and longitude in decimal degrees.
     *
     * @return String array with [0] latitude and [1] longitude.
     */
    private fun process_location_for_get_points(location_string: String): Array<String>
    {
        val location = location_string.split(',');
        if(location.size == 2)
        {
            val latitude = BigDecimal(location[0].trim()).setScale(4,
                                                                   RoundingMode.HALF_EVEN);
            val longitude = BigDecimal(location[1].trim()).setScale(4,
                                                                    RoundingMode.HALF_EVEN);
            return arrayOf(latitude.toString(),
                           longitude.toString());
        }

        throw (NumberFormatException("Bad location string"))
    }

    private suspend fun get_points(latitude: String,
                                   longitude: String): Array<String>
    {
        val points_JSON = Weather_gov_API.m_weather_gov_API_service.get_points(latitude,
                                                                               longitude);
//        Log.d(LOG_TAG, points_JSON)

        val points = JSONObject(points_JSON);
        val properties = points.getJSONObject("properties");
        val cwa = properties.getString("cwa");
        val grid_x = properties.getString("gridX");
        val grid_y = properties.getString("gridY");
        val relative_location = properties.getJSONObject("relativeLocation");
        val description = relative_location.getJSONObject("properties");
        val city = description.getString("city")
        val state = description.getString("state")

        return arrayOf(cwa,
                       grid_x,
                       grid_y,
                       city,
                       state);
    }

    private suspend fun combine_latest_data(state: Int,
                                            first_value: Array<String>?,
                                            second_value: Array<String>?,
                                            third_value: Array<String>?,
                                            fourth_value: Array<String>?)
    {
        or_state(state);

        val combined_response = if(get_state() == STATE_ALL)
        {
            set_state(STATE_IDLE);

            m_is_refreshing.emit(false);

            val data_storage = Data_storage(first_value?.get(0),
                                            first_value?.get(1),
                                            second_value?.get(0),
                                            second_value?.get(1),
                                            third_value?.get(0),
                                            third_value?.get(1),
                                            fourth_value?.get(0),
                                            fourth_value?.get(1));

            data_storage;
        }
        else
        {
            Data_storage();
        }

        if(get_state() == STATE_IDLE)
        {
            m_combined_response.value = combined_response;
        }
    }

    @Suppress("unused")
    fun is_system_in_night_mode(context: Context): Boolean
    {
        return User_settings.is_system_in_night_mode(context);
    }

    fun load_night_mode_selection(context: Context)
    {
        viewModelScope.launch() {
            update_night_mode(context,
                              User_settings.get_night_mode_selection(context).first())
        }
    }

    fun load_night_mode_selection(context: Context,
                                  function: (Int) -> Unit)
    {
        viewModelScope.launch() {
            function(User_settings.get_night_mode_selection(context).first());
        }
    }

    fun store_night_mode_selection(context: Context,
                                   selection: Int)
    {
        viewModelScope.launch() {
            User_settings.store_night_mode_selection(context,
                                                     selection);
            update_night_mode(context,
                              selection);
        }
    }

    fun load_forecast_location_setting(context: Context,
                                       function: (String) -> Unit)
    {
        viewModelScope.launch() {
            function(User_settings.get_forecast_location_setting(context).first());
        }
    }

    fun store_forecast_location_setting(context: Context,
                                        location: String)
    {
        viewModelScope.launch() {
            User_settings.store_forecast_location_setting(context,
                                                          location);
        }
    }

    fun load_download_over_wifi_only(context: Context,
                                     function: (Boolean) -> Unit)
    {
        viewModelScope.launch() {
            function(User_settings.load_download_over_wifi_only(context).first());
        }
    }

    fun store_download_over_wifi_only(context: Context,
                                      value: Boolean)
    {
        viewModelScope.launch() {
            User_settings.store_download_over_wifi_only(context,
                                                        value);
        }
    }

    fun load_auto_hide_toolbars(context: Context,
                                function: (Boolean) -> Unit)
    {
        viewModelScope.launch() {
            function(User_settings.load_auto_hide_toolbars(context).first());
        }
    }

    fun store_auto_hide_toolbars(context: Context,
                                 value: Boolean)
    {
        viewModelScope.launch() {
            User_settings.store_auto_hide_toolbars(context,
                                                   value);
        }
    }

    fun load_last_weather_data_fetched(context: Context,
                                       function: (Data_storage) -> Unit)
    {
        viewModelScope.launch() {
            function(User_settings.load_last_weather_data_fetched(context).first());
        }
    }

    fun store_last_weather_data_fetched(context: Context,
                                        data_storage: Data_storage)
    {
        viewModelScope.launch() {
            User_settings.store_last_weather_data_fetched(context,
                                                          data_storage);
        }
    }

    private fun update_night_mode(context: Context,
                                  selection: Int)
    {
        val selections = context.resources.getStringArray(R.array.night_mode_options);

        val mode: Int = if(selections[selection].contains("dark",
                                                          true))
        {
            AppCompatDelegate.MODE_NIGHT_YES;
        }
        else if(selections[selection].contains("light",
                                               true))
        {
            AppCompatDelegate.MODE_NIGHT_NO;
        }
        else
        {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }

        AppCompatDelegate.setDefaultNightMode(mode);
    }

    fun is_ok_to_fetch_data(context: Context,
                            function: (Boolean) -> Unit)
    {
        viewModelScope.launch() {
            function(User_settings.is_ok_to_fetch_data(context).first());
        }
    }

    fun is_application_in_night_mode(context: Context): Flow<Boolean>
    {
        return User_settings.is_application_in_night_mode(context);
    }

    private object User_settings
    {
        @Suppress("unused")
        private const val LOG_TAG = "User_settings";

        /**
         * Dark mode selection preferences storage key.
         */
        private val DARK_MODE_SELECTION_KEY = intPreferencesKey("dark_mode_selection");

        /**
         * Dark mode selection preferences storage key.
         */
        private val FORECAST_LOCATION_SETTING_KEY = stringPreferencesKey("forecast_location_setting");

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
        private val LAST_WEATHER_DATA_FETCHED_KEY = stringPreferencesKey("last_weather_data_fetched");

        private val Context.user_preferences_data_store: DataStore<Preferences> by preferencesDataStore(name = "user_settings");

        fun is_system_in_night_mode(context: Context): Boolean
        {
            var current = false;
            when(context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK))
            {
                Configuration.UI_MODE_NIGHT_YES ->
                {
                    current = true;
                }
            }

            return current;
        }

        fun get_night_mode_selection(context: Context): Flow<Int>
        {
            return context.user_preferences_data_store.data.map { preferences ->
                preferences[DARK_MODE_SELECTION_KEY]
                    ?: context.resources.getInteger(R.integer.night_mode_selection_default);
            }
        }

        fun is_application_in_night_mode(context: Context): Flow<Boolean>
        {
            return context.user_preferences_data_store.data.map { preferences ->
                val selections = context.resources.getStringArray(R.array.night_mode_options);
                val selection = preferences[DARK_MODE_SELECTION_KEY]
                    ?: context.resources.getInteger(R.integer.night_mode_selection_default);

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
                    is_system_in_night_mode(context);
                }
            }
        }

        suspend fun store_night_mode_selection(context: Context,
                                               selection: Int)
        {
            context.user_preferences_data_store.edit { preferences ->
                preferences[DARK_MODE_SELECTION_KEY] = selection;
            }
        }

        fun get_forecast_location_setting(context: Context): Flow<String>
        {
            return context.user_preferences_data_store.data.map { preferences ->
                preferences[FORECAST_LOCATION_SETTING_KEY]
                    ?: context.resources.getString(R.string.forecast_location_setting_default);
            }
        }

        suspend fun store_forecast_location_setting(context: Context,
                                                    location: String)
        {
            context.user_preferences_data_store.edit() { preferences ->
                preferences[FORECAST_LOCATION_SETTING_KEY] = location;
            }
        }

        fun load_download_over_wifi_only(context: Context): Flow<Boolean>
        {
            return context.user_preferences_data_store.data.map { preferences ->
                preferences[DOWNLOAD_OVER_WIFI_ONLY_KEY]
                    ?: context.resources.getBoolean(R.bool.download_over_wifi_only_default);
            }
        }

        suspend fun store_download_over_wifi_only(context: Context,
                                                  value: Boolean)
        {
            context.user_preferences_data_store.edit { preferences ->
                preferences[DOWNLOAD_OVER_WIFI_ONLY_KEY] = value;
            }
        }

        fun is_ok_to_fetch_data(context: Context): Flow<Boolean>
        {
            return context.user_preferences_data_store.data.map { preferences ->
                var is_metered = true;

                val connectivity_manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager;
                val network_capabilities = connectivity_manager.getNetworkCapabilities(connectivity_manager.activeNetwork);
                if(network_capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == false) is_metered = false;
                if(network_capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED) == true) is_metered = false;

                val download_over_wifi_only = preferences[DOWNLOAD_OVER_WIFI_ONLY_KEY]
                    ?: context.resources.getBoolean(R.bool.download_over_wifi_only_default);
                val is_ok = !(is_metered && download_over_wifi_only);
                is_ok;
            }
        }

        fun load_auto_hide_toolbars(context: Context): Flow<Boolean>
        {
            return context.user_preferences_data_store.data.map { preferences ->
                preferences[AUTO_HIDE_TOOLBARS_KEY]
                    ?: context.resources.getBoolean(R.bool.auto_hide_toolbars_default);
            }
        }

        suspend fun store_auto_hide_toolbars(context: Context,
                                             value: Boolean)
        {
            context.user_preferences_data_store.edit { preferences ->
                preferences[AUTO_HIDE_TOOLBARS_KEY] = value;
            }
        }

        fun load_last_weather_data_fetched(context: Context): Flow<Data_storage>
        {
            return context.user_preferences_data_store.data.map { preferences ->
                val data_storage_as_string: String? = preferences[LAST_WEATHER_DATA_FETCHED_KEY];
                if(data_storage_as_string == null)
                {
                    Data_storage();
                }
                else
                {
                    Data_storage.deserialize_from_JSON(data_storage_as_string)
                        ?: Data_storage();
                }
            }
        }

        suspend fun store_last_weather_data_fetched(context: Context,
                                                    data_storage: Data_storage)
        {
            val data_storage_as_string = data_storage.serialize_to_JSON();
            context.user_preferences_data_store.edit { preferences ->
                if(data_storage_as_string != null)
                {
                    preferences[LAST_WEATHER_DATA_FETCHED_KEY] = data_storage_as_string;
                }
            }
        }
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
}
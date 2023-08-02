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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.ddns.rkdawenterprises.davis_website.Weather_page
import net.ddns.rkdawenterprises.rkdawe_api_common.Get_weather_station_data_GET_response
import net.ddns.rkdawenterprises.rkdawe_api_common.RKDAWE_API
import net.ddns.rkdawenterprises.rkdawe_api_common.Weather_data
import net.ddns.rkdawenterprises.weatherstationdonna.R
import net.ddns.rkdawenterprises.davis_website.Davis_API
import java.util.concurrent.atomic.AtomicInteger

/**
 * Provides a combined live data from three different servers.
 */
class Main_view_model(application: Application): AndroidViewModel(application)
{
    /**
     * Last "successful" fetched weather data. Does not store the failed fetches.
     */
    class Data_storage(first_status: String? = null,
                       first_data: String? = null,
                       second_status: String? = null,
                       second_data: String? = null,
                       third_status: String? = null,
                       third_data: String? = null)
    {
        companion object
        {
            @Suppress("unused")
            private const val LOG_TAG = "Data_storage";

            private val s_GSON: Gson = GsonBuilder().disableHtmlEscaping()
                    .setPrettyPrinting()
                    .create()

            fun serialize_to_JSON(`object`: Data_storage?): String?
            {
                return s_GSON.toJson(`object`)
            }

            fun deserialize_from_JSON(string_JSON: String): Data_storage?
            {
                var instance: Data_storage? = null

                try
                {
                    @Suppress("unused")
                    instance = s_GSON.fromJson(string_JSON,
                                               Data_storage::class.java)
                }
                catch(exception: JsonSyntaxException)
                {
                    Log.d(LOG_TAG, "Bad data format for Weather_data: $exception")
                    Log.d(LOG_TAG, ">>>$string_JSON<<<")
                }

                return instance
            }
        }

        init
        {
            set_data_RKDAWE(first_status, first_data);
            set_data_davis(second_status, second_data);
            set_page_davis(third_status, third_data);
        }

        var m_data_RKDAWE: Weather_data? = null
            private set
        var m_data_davis: net.ddns.rkdawenterprises.davis_website.Weather_data? = null
            private set
        var m_page_davis: Weather_page? = null
            private set

        @Suppress("unused",
                  "MemberVisibilityCanBePrivate")
        fun set_data_RKDAWE(status: String?, data: String?): Boolean
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
        fun set_data_davis(status: String?, data: String?): Boolean
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
        fun set_page_davis(status: String?, data: String?): Boolean
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

        fun is_empty(): Boolean
        {
            return ((m_data_RKDAWE == null) && (m_data_davis == null) && (m_page_davis == null))
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

        private const val STATE_IDLE = 0b000000;
        private const val STATE_STARTED = 0b000010;
        private const val STATE_FIRST = 0b000100;
        private const val STATE_SECOND = 0b001000;
        private const val STATE_THIRD = 0b010000;
        private const val STATE_ALL = STATE_STARTED or STATE_FIRST or STATE_SECOND or STATE_THIRD;
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
        m_state.getAndUpdate { i-> i or state }
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

    private val m_combined_response = MutableLiveData<Data_storage>();
    val combined_response: LiveData<Data_storage> get() = m_combined_response;

    private val m_is_refreshing = MutableStateFlow(false);
    val is_refreshing: StateFlow<Boolean> get() = m_is_refreshing.asStateFlow();

    private val m_snackbar_message = MutableLiveData<Array<String>>();
    @Suppress("unused")
    val snackbar_message: LiveData<Array<String>> get() = m_snackbar_message;

    fun refresh(stored_data: Data_storage)
    {
//        Log.d(LOG_TAG, "Using stored data...")
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
                    arrayOf("success", RKDAWE_API.m_RKDAWE_API_service.get_weather_station_data());
                }
                catch(exception: Exception)
                {
                    m_snackbar_message.value = arrayOf(application_context.resources.getString(R.string.unable_to_get_weather_data),
                                                       "Failure fetching RKDAWE data: ${exception.message}");
                    arrayOf("failure", "${exception.message}");
                }

                if(value[0] == "failure")
                {
                    value = try
                    {
                        arrayOf("success", RKDAWE_API.m_RKDAWE_API_service.get_weather_station_data());
                    }
                    catch(exception: Exception)
                    {
                        m_snackbar_message.value = arrayOf(application_context.resources.getString(R.string.unable_to_get_weather_data),
                                                           "Failure fetching RKDAWE data: ${exception.message}");
                        arrayOf("failure", "${exception.message}");
                    }
                }

                m_first_response.value = value;
                val combined_value = combine_latest_data(STATE_FIRST,
                                                         value,
                                                         m_second_response.value,
                                                         m_third_response.value);
                if(get_state() == STATE_IDLE)
                {
                    m_combined_response.value = combined_value;
                }
            }

            viewModelScope.launch {
                val value: Array<String> = try
                {
                    arrayOf("success", Davis_API.m_davis_API_service.get_weather_station_data());
                }
                catch(exception: Exception)
                {
                    m_snackbar_message.value = arrayOf(application_context.resources.getString(R.string.unable_to_get_weather_data),
                                                       "Failure fetching davis data: ${exception.message}");
                    arrayOf("failure", "${exception.message}");
                }

                m_second_response.value = value;
                val combined_value = combine_latest_data(STATE_SECOND,
                                                         m_first_response.value,
                                                         value,
                                                         m_third_response.value);
                if(get_state() == STATE_IDLE)
                {
                    m_combined_response.value = combined_value;
                }
            }

            viewModelScope.launch {
                val value: Array<String> = try
                {
                    val page_data = Davis_API.m_davis_API_service.get_weather_station_page();
                    val weather_page = Weather_page.scrape_page(page_data);
                    arrayOf("success", weather_page.serialize_to_JSON());
                }
                catch(exception: Exception)
                {
                    m_snackbar_message.value = arrayOf(application_context.resources.getString(R.string.unable_to_get_weather_data),
                                                       "Failure fetching davis page: ${exception.message}");
                    arrayOf("failure", "${exception.message}");
                }

                m_third_response.value = value;
                val combined_value = combine_latest_data(STATE_THIRD,
                                                         m_first_response.value,
                                                         m_second_response.value,
                                                         value);
                if(get_state() == STATE_IDLE)
                {
                    m_combined_response.value = combined_value;
                }
            }
        }
    }

    private suspend fun combine_latest_data(state: Int,
                                            first_value: Array<String>?,
                                            second_value: Array<String>?,
                                            third_value: Array<String>?): Data_storage
    {
        or_state(state);

        return if(get_state() == STATE_ALL)
        {
            set_state(STATE_IDLE);

            m_is_refreshing.emit(false);

            val data_storage = Data_storage(first_value?.get(0),
                                            first_value?.get(1),
                                            second_value?.get(0),
                                            second_value?.get(1),
                                            third_value?.get(0),
                                            third_value?.get(1));

            data_storage;
        }
        else
        {
            Data_storage();
        }
    }

    @Suppress("unused")
    fun is_system_in_night_mode(context: Context): Boolean
    {
        return User_settings.is_system_in_night_mode(context);
    }

    fun load_night_mode_selection(context: Context)
    {
        viewModelScope.launch()
        {
            update_night_mode(context, User_settings.get_night_mode_selection(context).first())
        }
    }

    fun load_night_mode_selection(context: Context,
                                  function: (Int) -> Unit)
    {
        viewModelScope.launch()
        {
            function(User_settings.get_night_mode_selection(context).first());
        }
    }

    fun store_night_mode_selection(context: Context, selection: Int)
    {
        viewModelScope.launch()
        {
            User_settings.store_night_mode_selection(context, selection);
            update_night_mode(context, selection);
        }
    }

    fun load_download_over_wifi_only(context: Context,
                                     function: (Boolean) -> Unit)
    {
        viewModelScope.launch()
        {
            function(User_settings.load_download_over_wifi_only(context).first());
        }
    }

    fun store_download_over_wifi_only(context: Context, value: Boolean)
    {
        viewModelScope.launch()
        {
            User_settings.store_download_over_wifi_only(context, value);
        }
    }

    fun load_auto_hide_toolbars(context: Context,
                                function: (Boolean) -> Unit)
    {
        viewModelScope.launch()
        {
            function(User_settings.load_auto_hide_toolbars(context).first());
        }
    }

    fun store_auto_hide_toolbars(context: Context, value: Boolean)
    {
        viewModelScope.launch()
        {
            User_settings.store_auto_hide_toolbars(context, value);
        }
    }

    fun load_last_weather_data_fetched(context: Context,
                                       function: (Data_storage) -> Unit)
    {
        viewModelScope.launch()
        {
            function(User_settings.load_last_weather_data_fetched(context).first());
        }
    }

    fun store_last_weather_data_fetched(context: Context, data_storage: Data_storage)
    {
        viewModelScope.launch()
        {
            User_settings.store_last_weather_data_fetched(context, data_storage);
        }
    }

    private fun update_night_mode(context: Context, selection: Int)
    {
        val selections = context.resources.getStringArray(R.array.night_mode_options);

        val mode: Int = if(selections[selection].contains("dark", true))
        {
            AppCompatDelegate.MODE_NIGHT_YES;
        }
        else if(selections[selection].contains("light", true))
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
        viewModelScope.launch()
        {
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
         * Weather data URI preferences storage key.
         */
        private val DARK_MODE_SELECTION_KEY = intPreferencesKey("dark_mode_selection");

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
            return context.user_preferences_data_store.data
                    .map { preferences->
                        preferences[DARK_MODE_SELECTION_KEY]
                            ?: context.resources.getInteger(R.integer.night_mode_selection_default);
                    }
        }

        fun is_application_in_night_mode(context: Context): Flow<Boolean>
        {
            return context.user_preferences_data_store.data
                    .map { preferences->
                        val selections = context.resources.getStringArray(R.array.night_mode_options);
                        val selection = preferences[DARK_MODE_SELECTION_KEY]
                            ?: context.resources.getInteger(R.integer.night_mode_selection_default);

                        if(selections[selection].contains("dark", true))
                        {
                            true;
                        }
                        else if(selections[selection].contains("light", true))
                        {
                            false;
                        }
                        else    // Follow system...
                        {
                            is_system_in_night_mode(context);
                        }
                    }
        }

        suspend fun store_night_mode_selection(context: Context, selection: Int)
        {
            context.user_preferences_data_store.edit { preferences->
                preferences[DARK_MODE_SELECTION_KEY] = selection;
            }
        }

        fun load_download_over_wifi_only(context: Context): Flow<Boolean>
        {
            return context.user_preferences_data_store.data
                    .map { preferences->
                        preferences[DOWNLOAD_OVER_WIFI_ONLY_KEY]
                            ?: context.resources.getBoolean(R.bool.download_over_wifi_only_default);
                    }
        }

        suspend fun store_download_over_wifi_only(context: Context, value: Boolean)
        {
            context.user_preferences_data_store.edit { preferences->
                preferences[DOWNLOAD_OVER_WIFI_ONLY_KEY] = value;
            }
        }

        fun is_ok_to_fetch_data(context: Context): Flow<Boolean>
        {
            return context.user_preferences_data_store.data
                    .map { preferences->
                        var is_metered = true;

                        val connectivity_manager =
                            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager;
                        val network_capabilities =
                            connectivity_manager.getNetworkCapabilities(connectivity_manager.activeNetwork);
                        if(network_capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == false) is_metered =
                            false;
                        if(network_capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED) == true) is_metered =
                            false;

                        val download_over_wifi_only = preferences[DOWNLOAD_OVER_WIFI_ONLY_KEY]
                            ?: context.resources.getBoolean(R.bool.download_over_wifi_only_default);
                        val is_ok = !(is_metered && download_over_wifi_only);
                        is_ok;
                    }
        }

        fun load_auto_hide_toolbars(context: Context): Flow<Boolean>
        {
            return context.user_preferences_data_store.data
                    .map { preferences->
                        preferences[AUTO_HIDE_TOOLBARS_KEY] ?: context.resources.getBoolean(R.bool.auto_hide_toolbars_default);
                    }
        }

        suspend fun store_auto_hide_toolbars(context: Context, value: Boolean)
        {
            context.user_preferences_data_store.edit { preferences->
                preferences[AUTO_HIDE_TOOLBARS_KEY] = value;
            }
        }

        fun load_last_weather_data_fetched(context: Context): Flow<Data_storage>
        {
            return context.user_preferences_data_store.data
                    .map { preferences->
                        val data_storage_as_string: String? = preferences[LAST_WEATHER_DATA_FETCHED_KEY];
                        if(data_storage_as_string == null)
                        {
                            Data_storage();
                        }
                        else
                        {
                            Data_storage.deserialize_from_JSON(data_storage_as_string) ?: Data_storage();
                        }
                    }
        }

        suspend fun store_last_weather_data_fetched(context: Context, data_storage: Data_storage)
        {
            val data_storage_as_string = data_storage.serialize_to_JSON();
            context.user_preferences_data_store.edit { preferences->
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
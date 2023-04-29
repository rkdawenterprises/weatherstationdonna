@file:Suppress("ClassName",
               "FunctionName",
               "RedundantSemicolon",
               "PrivatePropertyName",
               "LocalVariableName",
               "PropertyName",
               "PackageName")

package net.ddns.rkdawenterprises.weatherstationdonna

import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
 * Auto-hide toolbars delay preferences storage key.
 */
private val AUTO_HIDE_TOOLBARS_DELAY_KEY = intPreferencesKey("auto_hide_toolbars_delay");

/**
 * Last weather data fetched successfully.
 */
private val LAST_WEATHER_DATA_FETCHED_KEY = stringPreferencesKey("last_weather_data_fetched");

object User_settings
{
    private const val LOG_TAG = "User_settings";

    private val Context.user_preferences_data_store: DataStore<Preferences> by preferencesDataStore(name = "user_settings");

    fun is_system_night_mode(context: Context): Boolean
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

    fun is_night_mode_derived(context: Context): Flow<Boolean>
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
                        is_system_night_mode(context);
                    }
                }
    }

    suspend fun put_night_mode_selection(context: Context, selection: Int)
    {
        context.user_preferences_data_store.edit { preferences->
            preferences[DARK_MODE_SELECTION_KEY] = selection;
        }
    }

    fun get_download_over_wifi_only(context: Context): Flow<Boolean>
    {
        return context.user_preferences_data_store.data
                .map { preferences->
                    preferences[DOWNLOAD_OVER_WIFI_ONLY_KEY]
                        ?: context.resources.getBoolean(R.bool.download_over_wifi_only_default);
                }
    }

    suspend fun put_download_over_wifi_only(context: Context, value: Boolean)
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

    fun get_auto_hide_toolbars(context: Context): Flow<Boolean>
    {
        return context.user_preferences_data_store.data
                .map { preferences->
                    preferences[AUTO_HIDE_TOOLBARS_KEY] ?: context.resources.getBoolean(R.bool.auto_hide_toolbars_default);
                }
    }

    suspend fun put_auto_hide_toolbars(context: Context, value: Boolean)
    {
        context.user_preferences_data_store.edit { preferences->
            preferences[AUTO_HIDE_TOOLBARS_KEY] = value;
        }
    }

    fun get_auto_hide_toolbars_delay(context: Context): Flow<Int>
    {
        return context.user_preferences_data_store.data
                .map { preferences->
                    preferences[AUTO_HIDE_TOOLBARS_DELAY_KEY]
                        ?: context.resources.getInteger(R.integer.auto_hide_toolbars_delay_default);
                }
    }

    @Suppress("unused")
    suspend fun put_auto_hide_toolbars_delay(context: Context, value: Int)
    {
        context.user_preferences_data_store.edit { preferences->
            preferences[AUTO_HIDE_TOOLBARS_DELAY_KEY] = value;
        }
    }

    fun get_last_data(context: Context): Flow<Data_storage>
    {
        return context.user_preferences_data_store.data
                .map { preferences->
                    val data_storage_as_string: String? = preferences[LAST_WEATHER_DATA_FETCHED_KEY];
                    if( data_storage_as_string == null)
                    {
                        Data_storage();
                    }
                    else
                    {
                        Data_storage.deserialize_from_JSON(data_storage_as_string)?: Data_storage();
                    }
                }
    }

    suspend fun put_last_data(context: Context, data_storage: Data_storage)
    {
        val data_storage_as_string = data_storage.serialize_to_JSON();
        context.user_preferences_data_store.edit { preferences->
            if(data_storage_as_string != null)
            {
                preferences[LAST_WEATHER_DATA_FETCHED_KEY] = data_storage_as_string;
            }
        }
    }

    class Data_storage(var first_status: String? = null,
                       var first_data: String? = null,
                       var second_status: String? = null,
                       var second_data: String? = null,
                       var third_status: String? = null,
                       var third_data: String? = null)
    {
        companion object
        {
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
                    Log.d(LOG_TAG,">>>$string_JSON<<<")
                }

                return instance
            }
        }

        fun is_empty(): Boolean
        {
            return((first_status == null) && (first_data == null) &&
                    (second_status == null) && (second_data == null) &&
                    (third_status == null) && (third_data == null))
        }

        fun serialize_to_JSON(): String?
        {
            return serialize_to_JSON(this)
        }
    }
}
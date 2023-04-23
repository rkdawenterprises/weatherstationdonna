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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings");

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
 * Last weather data fetched.
 */
private val LAST_WEATHER_DATA_FETCHED_KEY = stringPreferencesKey("last_weather_data_fetched");
private val LAST_WEATHER_DATA_DAVIS_FETCHED_KEY = stringPreferencesKey("last_weather_data_davis_fetched");
private val LAST_WEATHER_PAGE_DAVIS_FETCHED_KEY = stringPreferencesKey("last_weather_page_davis_fetched");

object User_settings
{
    @Suppress("MemberVisibilityCanBePrivate")
    fun is_night_mode(context: Context): Boolean
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
        return context.dataStore.data
                .map { preferences->
                    preferences[DARK_MODE_SELECTION_KEY]
                        ?: context.resources.getInteger(R.integer.night_mode_selection_default);
                }
    }

    fun get_night_mode_derived(context: Context): Flow<Boolean>
    {
        return context.dataStore.data
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
                        is_night_mode(context);
                    }
                }
    }

    suspend fun put_night_mode_selection(context: Context,
                                         value: Int)
    {
        context.dataStore.edit { preferences->
            val stored_value = preferences[DARK_MODE_SELECTION_KEY];
            if(value != stored_value) preferences[DARK_MODE_SELECTION_KEY] = value;
        }
    }

    fun get_download_over_wifi_only(context: Context): Flow<Boolean>
    {
        return context.dataStore.data
                .map { preferences->
                    preferences[DOWNLOAD_OVER_WIFI_ONLY_KEY]
                        ?: context.resources.getBoolean(R.bool.download_over_wifi_only_default);
                }
    }

    suspend fun put_download_over_wifi_only(context: Context,
                                            value: Boolean)
    {
        context.dataStore.edit { preferences->
            val stored_value = preferences[DOWNLOAD_OVER_WIFI_ONLY_KEY];
            if(value != stored_value) preferences[DOWNLOAD_OVER_WIFI_ONLY_KEY] = value;
        }
    }

    fun get_auto_hide_toolbars(context: Context): Flow<Boolean>
    {
        return context.dataStore.data
                .map { preferences->
                    preferences[AUTO_HIDE_TOOLBARS_KEY] ?: context.resources.getBoolean(R.bool.auto_hide_toolbars_default);
                }
    }

    suspend fun put_auto_hide_toolbars(context: Context,
                                       value: Boolean)
    {
        context.dataStore.edit { preferences->
            val stored_value = preferences[AUTO_HIDE_TOOLBARS_KEY];
            if(value != stored_value) preferences[AUTO_HIDE_TOOLBARS_KEY] = value;
        }
    }

    fun get_auto_hide_toolbars_delay(context: Context): Flow<Int>
    {
        return context.dataStore.data
                .map { preferences->
                    preferences[AUTO_HIDE_TOOLBARS_DELAY_KEY]
                        ?: context.resources.getInteger(R.integer.auto_hide_toolbars_delay_default);
                }
    }

    suspend fun put_auto_hide_toolbars_delay(context: Context,
                                             value: Int)
    {
        context.dataStore.edit { preferences->
            val stored_value = preferences[AUTO_HIDE_TOOLBARS_DELAY_KEY];
            if(value != stored_value) preferences[AUTO_HIDE_TOOLBARS_DELAY_KEY] = value;
        }
    }
}
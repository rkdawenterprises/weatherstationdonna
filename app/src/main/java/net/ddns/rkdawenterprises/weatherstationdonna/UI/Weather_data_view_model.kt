@file:Suppress("ClassName",
               "FunctionName",
               "RedundantSemicolon",
               "PrivatePropertyName",
               "LocalVariableName",
               "PropertyName",
               "PackageName")

package net.ddns.rkdawenterprises.weatherstationdonna.UI

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.ddns.rkdawenterprises.davis_website.Davis_API
import net.ddns.rkdawenterprises.weatherstationdonna.R
import net.ddns.rkdawenterprises.weatherstationdonna.RKDAWE_API
import net.ddns.rkdawenterprises.weatherstationdonna.User_settings
import java.util.concurrent.atomic.AtomicInteger

class Weather_data_view_model: ViewModel()
{
    companion object
    {
        const val STATE_IDLE = 0b000000;
        const val STATE_STARTED = 0b000010;
        const val STATE_FIRST = 0b000100;
        const val STATE_SECOND = 0b001000;
        const val STATE_THIRD = 0b010000;
        const val STATE_ALL = STATE_STARTED or STATE_FIRST or STATE_SECOND or STATE_THIRD;
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
    val first_response: LiveData<Array<String>> get() = m_first_response;

    private val m_second_response = MutableLiveData<Array<String>>();
    val second_response: LiveData<Array<String>> get() = m_second_response;

    private val m_third_response = MutableLiveData<Array<String>>();
    val third_response: LiveData<Array<String>> get() = m_third_response;

    private val m_combined_response = MutableLiveData<User_settings.Data_storage>();
    val combined_response: LiveData<User_settings.Data_storage> get() = m_combined_response;

    private val m_is_refreshing = MutableStateFlow(false);
    val is_refreshing: StateFlow<Boolean> get() = m_is_refreshing.asStateFlow();

    fun set_night_mode_selection(context: Context, selection: Int)
    {
        viewModelScope.launch {
            User_settings.put_night_mode_selection(context, selection);
            update_night_mode(context, selection);
        }
    }

    fun set_download_over_wifi_only(context: Context, value: Boolean)
    {
        viewModelScope.launch {
            User_settings.put_download_over_wifi_only(context, value);
        }
    }

    fun set_auto_hide_toolbars(context: Context, value: Boolean)
    {
        viewModelScope.launch {
            User_settings.put_auto_hide_toolbars(context, value);
        }
    }

    fun set_last_weather_data_fetched(context: Context, data_storage: User_settings.Data_storage)
    {
        viewModelScope.launch {
            User_settings.put_last_data(context, data_storage);
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

    fun refresh(stored_data: User_settings.Data_storage)
    {
        m_combined_response.value = stored_data;
    }

    fun refresh()
    {
        if(get_state() == STATE_IDLE)
        {
            set_state(STATE_STARTED);

            viewModelScope.launch {
                m_is_refreshing.emit(true);

                val value: Array<String> = try
                {
                    arrayOf("success", RKDAWE_API.m_RKDAWE_API_service.get_weather_station_data());
                }
                catch(exception: Exception)
                {
                    arrayOf("failure", "${exception.message}");
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
                m_is_refreshing.emit(true);

                val value: Array<String> = try
                {
                    arrayOf("success", Davis_API.m_davis_API_service.get_weather_station_data());
                }
                catch(exception: Exception)
                {
                    arrayOf("failure", "${exception.message}");
                }

                m_first_response.value = value;
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
                m_is_refreshing.emit(true);

                val value: Array<String> = try
                {
                    arrayOf("success", Davis_API.m_davis_API_service.get_weather_station_page());
                }
                catch(exception: Exception)
                {
                    arrayOf("failure", "${exception.message}");
                }

                m_first_response.value = value;
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
                                            third_value: Array<String>?): User_settings.Data_storage
    {
        or_state(state);

        return if(get_state() == STATE_ALL)
        {
            set_state(STATE_IDLE);
            m_is_refreshing.emit(false);
            User_settings.Data_storage(first_value?.get(0),
                                       first_value?.get(1),
                                       second_value?.get(0),
                                       second_value?.get(1),
                                       third_value?.get(0),
                                       third_value?.get(1));
        }
        else
        {
            User_settings.Data_storage();
        }
    }
}
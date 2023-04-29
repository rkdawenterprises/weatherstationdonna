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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.ddns.rkdawenterprises.rkdawe_api_common.RKDAWE_API
import net.ddns.rkdawenterprises.weatherstationdonna.Main_activity
import net.ddns.rkdawenterprises.weatherstationdonna.R
import net.ddns.rkdawenterprises.weatherstationdonna.User_settings
import net.ddns.rkdawenterprises.weatherstationdonna.davis_website.Davis_API
import java.util.concurrent.atomic.AtomicInteger

class Main_view_model(context: Main_activity): ViewModel()
{
    class Main_view_model_factory(private val context: Main_activity): ViewModelProvider.Factory
    {
        override fun <T: ViewModel> create(modelClass: Class<T>): T
        {
            return Main_view_model(context) as T
        }
    }

    companion object
    {
        private const val LOG_TAG = "Main_view_model";

        const val STATE_IDLE = 0b000000;
        const val STATE_STARTED = 0b000010;
        const val STATE_FIRST = 0b000100;
        const val STATE_SECOND = 0b001000;
        const val STATE_THIRD = 0b010000;
        const val STATE_ALL = STATE_STARTED or STATE_FIRST or STATE_SECOND or STATE_THIRD;

        // Cached data-store items for non-flow access.
        var s_night_mode_selection: Int = 0
        var s_is_night_mode_derived: Boolean = false;
        var s_is_download_over_wifi_only: Boolean = true;
        var s_is_auto_hide_toolbars: Boolean = false;
        var s_auto_hide_toolbars_delay: Int = 3500;
        var s_is_ok_to_fetch_data: Boolean = false;
        var s_last_weather_data_fetched: User_settings.Data_storage = User_settings.Data_storage();
    }

    init
    {
        s_night_mode_selection = context.resources.getInteger(R.integer.night_mode_selection_default);
        s_is_night_mode_derived = User_settings.is_system_night_mode(context);
        s_is_download_over_wifi_only = context.resources.getBoolean(R.bool.download_over_wifi_only_default);
        s_is_auto_hide_toolbars = context.resources.getBoolean(R.bool.auto_hide_toolbars_default);
        s_auto_hide_toolbars_delay = context.resources.getInteger(R.integer.auto_hide_toolbars_delay_default);
        s_is_ok_to_fetch_data = false;
        s_last_weather_data_fetched = User_settings.Data_storage();

        initialize_saved_settings(context);
    }
    
    private val m_state: AtomicInteger = AtomicInteger(STATE_IDLE);

    private fun initialize_saved_settings(context: Main_activity)
    {
        viewModelScope.launch {
            s_night_mode_selection = User_settings.get_night_mode_selection(context).first();
            update_night_mode(context, s_night_mode_selection);
            s_is_night_mode_derived = User_settings.is_night_mode_derived(context).first();
            s_is_download_over_wifi_only = User_settings.get_download_over_wifi_only(context).first();
            s_is_auto_hide_toolbars = User_settings.get_auto_hide_toolbars(context).first();
            s_auto_hide_toolbars_delay = User_settings.get_auto_hide_toolbars_delay(context).first();
            s_is_ok_to_fetch_data = User_settings.is_ok_to_fetch_data(context).first();
        }

        User_settings.get_night_mode_selection(context).distinctUntilChanged().asLiveData().observe(context) { night_mode_selection ->
            s_night_mode_selection = night_mode_selection;
        }

        User_settings.is_night_mode_derived(context).distinctUntilChanged().asLiveData().observe(context) { is_night_mode_derived ->
            s_is_night_mode_derived = is_night_mode_derived;
        }

        User_settings.get_download_over_wifi_only(context).distinctUntilChanged().asLiveData().observe(context) { is_download_over_wifi_only ->
            s_is_download_over_wifi_only = is_download_over_wifi_only;
        }

        User_settings.get_auto_hide_toolbars(context).distinctUntilChanged().asLiveData().observe(context) { is_auto_hide_toolbars ->
            s_is_auto_hide_toolbars = is_auto_hide_toolbars;
        }

        User_settings.get_auto_hide_toolbars_delay(context).distinctUntilChanged().asLiveData().observe(context) { auto_hide_toolbars_delay ->
            s_auto_hide_toolbars_delay = auto_hide_toolbars_delay;
        }

        User_settings.is_ok_to_fetch_data(context).distinctUntilChanged().asLiveData().observe(context) { is_ok_to_fetch_data ->
            s_is_ok_to_fetch_data = is_ok_to_fetch_data;
        }
    }

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
        }

        // TODO: The observer is not being called, so update manually. Check if this changes in the future...
        s_night_mode_selection = selection;
        update_night_mode(context, selection);

        viewModelScope.launch {
            s_is_night_mode_derived = User_settings.is_night_mode_derived(context).first();
        }
    }

    fun set_download_over_wifi_only(context: Context, value: Boolean)
    {
        viewModelScope.launch {
            User_settings.put_download_over_wifi_only(context, value);
        }

        s_is_download_over_wifi_only = value
    }

    fun set_auto_hide_toolbars(context: Context, value: Boolean)
    {
        viewModelScope.launch {
            User_settings.put_auto_hide_toolbars(context, value);
        }

        s_is_auto_hide_toolbars = value;
    }

    fun set_last_weather_data_fetched(context: Context, data_storage: User_settings.Data_storage)
    {
        viewModelScope.launch {
            User_settings.put_last_data(context, data_storage);
            s_last_weather_data_fetched = data_storage;
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
                m_is_refreshing.emit(true);

                val value: Array<String> = try
                {
                    arrayOf("success", Davis_API.m_davis_API_service.get_weather_station_page());
                }
                catch(exception: Exception)
                {
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
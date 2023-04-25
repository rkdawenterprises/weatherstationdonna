@file:Suppress("ClassName",
               "FunctionName",
               "RedundantSemicolon",
               "PrivatePropertyName",
               "LocalVariableName",
               "PropertyName")

package net.ddns.rkdawenterprises.weatherstationdonna

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.ddns.rkdawenterprises.weatherstationdonna.network.davis_website.Davis_API
import net.ddns.rkdawenterprises.weatherstationdonna.network.RKDAWE_API
import java.util.concurrent.atomic.AtomicInteger

class Weather_data_view_model: ViewModel()
{
    companion object
    {
        const val STATE_IDLE =    0b000000;
        const val STATE_ERROR =   0b000001;
        const val STATE_STARTED = 0b000010;
        const val STATE_FIRST =   0b000100;
        const val STATE_SECOND =  0b001000;
        const val STATE_THIRD =   0b010000;
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

    private val m_combined_response = MutableLiveData<Triple<Array<String>?, Array<String>?, Array<String>?>>();
    val combined_response: LiveData<Triple<Array<String>?, Array<String>?, Array<String>?>> get() = m_combined_response;

    private val m_is_refreshing = MutableStateFlow(false);
    val is_refreshing: StateFlow<Boolean> get() = m_is_refreshing.asStateFlow();

    suspend fun refresh()
    {
        if((get_state() == STATE_IDLE) || (get_state() == STATE_ERROR))
        {
            set_state(STATE_STARTED);
            m_is_refreshing.emit(true);

            viewModelScope.launch {
                try
                {
                    val value = arrayOf("success",
                                        RKDAWE_API.m_RKDAWE_API_service.get_weather_station_data());
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
                catch(exception: Exception)
                {
                    val value = arrayOf("failure", "${exception.message}");
                    m_first_response.value = value;
                    m_combined_response.value = combine_latest_data(STATE_FIRST,
                                                                    value,
                                                                    m_second_response.value,
                                                                    m_third_response.value);
                    set_state(STATE_ERROR);
                    m_is_refreshing.emit(false);
                }
            }

            viewModelScope.launch {
                try
                {
                    val value = arrayOf("success", Davis_API.m_davis_API_service.get_weather_station_data());
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
                catch(exception: Exception)
                {
                    val value = arrayOf("failure", "${exception.message}");
                    m_combined_response.value = combine_latest_data(STATE_SECOND,
                                                                    m_first_response.value,
                                                                    value,
                                                                    m_third_response.value);
                    set_state(STATE_ERROR);
                    m_is_refreshing.emit(false);
                }
            }

            viewModelScope.launch {
                try
                {
                    val value = arrayOf("success", Davis_API.m_davis_API_service.get_weather_station_page());
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
                catch(exception: Exception)
                {
                    val value = arrayOf("failure", "${exception.message}");
                    m_combined_response.value = combine_latest_data(STATE_THIRD,
                                                                    m_first_response.value,
                                                                    m_second_response.value,
                                                                    value);
                    set_state(STATE_ERROR);
                    m_is_refreshing.emit(false);
                }
            }
        }
    }

    private suspend fun combine_latest_data(state: Int,
                                            first_value: Array<String>?,
                                            second_value: Array<String>?,
                                            third_value: Array<String>?): Triple<Array<String>?, Array<String>?, Array<String>?>
    {
        or_state(state);
Log.d("combine_latest_data", ">>>>> $state" );
        return if(get_state() == STATE_ALL)
        {
            set_state(STATE_IDLE);
            m_is_refreshing.emit(false);
            Triple(first_value, second_value, third_value);
        }
        else
        {
            Triple(null, null, null);
        }
    }
}
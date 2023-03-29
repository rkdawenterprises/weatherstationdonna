@file:Suppress("ClassName",
               "FunctionName",
               "RedundantSemicolon",
               "PrivatePropertyName",
               "LocalVariableName",
               "PropertyName")

package net.ddns.rkdawenterprises.weatherstationdonna

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.IntUnaryOperator
import java.util.function.LongUnaryOperator

class Combined_mediator_live_data<F, S>(first_live_data: LiveData<F>, second_live_data: LiveData<S>)
    : MediatorLiveData<Pair<F?, S?>>()
{
    companion object
    {
        const val STATE_IDLE = 0b00000;
        const val STATE_STARTED = 0b00001;
        const val STATE_GOT_FIRST = 0b00010;
        const val STATE_GOT_SECOND = 0b00100;
        const val STATE_GOT_BOTH = STATE_STARTED or STATE_GOT_FIRST or STATE_GOT_SECOND;
    }

    val m_state: AtomicInteger = AtomicInteger(STATE_IDLE);

    init
    {
        addSource(first_live_data) { first_live_data_value: F->
            value = combine_latest_data(STATE_GOT_FIRST, first_live_data_value, second_live_data.value);
        }

        addSource(second_live_data) { second_live_data_value: S->
            value = combine_latest_data(STATE_GOT_SECOND, first_live_data.value, second_live_data_value);
        }
    }

    private fun combine_latest_data(got_first_or_second: Int, first_value: F?, second_value: S?): Pair<F?, S?>
    {
        m_state.getAndUpdate { i-> i or got_first_or_second };

        return if(m_state.get() == STATE_GOT_BOTH)
        {
            m_state.set(STATE_IDLE);
            Pair(first_value, second_value);
        }
        else
        {
            Pair(null, null);
        }
    }
}

class Combined_weather_data: ViewModel()
{
    private val m_RKDAWE_response = MutableLiveData<Array<String>>();
    val RKDAWE_response: LiveData<Array<String>> = m_RKDAWE_response;

    private val m_davis_response = MutableLiveData<Array<String>>();
    val davis_response: LiveData<Array<String>> = m_davis_response;

    val combined_response = Combined_mediator_live_data(m_RKDAWE_response, m_davis_response);

    fun get_weather_data()
    {
        if(combined_response.m_state.get() == Combined_mediator_live_data.STATE_IDLE)
        {
            combined_response.m_state.set(Combined_mediator_live_data.STATE_STARTED);

            viewModelScope.launch {
                try
                {
                    m_RKDAWE_response.value = arrayOf("success",
                                                      RKDAWE_API.m_RKDAWE_API_service.get_weather_station_data());
                }
                catch(exception: Exception)
                {
                    m_RKDAWE_response.value = arrayOf("failure", "${exception.message}");
                    combined_response.m_state.set(Combined_mediator_live_data.STATE_IDLE);
                }
            }

            viewModelScope.launch {
                try
                {
                    val weather_page =
                        Davis_API.m_davis_API_service.get_weather_station_page();
                    m_davis_response.value =
                        arrayOf("success", Davis_API.m_davis_API_service.get_weather_station_data(), weather_page);
                }
                catch(exception: Exception)
                {
                    m_davis_response.value = arrayOf("failure", "${exception.message}");
                    combined_response.m_state.set(Combined_mediator_live_data.STATE_IDLE);
                }
            }
        }
    }
}
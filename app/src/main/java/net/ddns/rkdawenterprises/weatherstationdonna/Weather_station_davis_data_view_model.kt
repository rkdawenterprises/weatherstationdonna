@file:Suppress("ClassName",
               "FunctionName",
               "RedundantSemicolon",
               "PrivatePropertyName",
               "LocalVariableName")

package net.ddns.rkdawenterprises.weatherstationdonna

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class Weather_station_davis_data_view_model: ViewModel()
{
    private val m_weather_data_fetch_response = MutableLiveData<Array<String>>();
    val m_response: LiveData<Array<String>> = m_weather_data_fetch_response;

    fun get_weather_data()
    {
        viewModelScope.launch {
            try
            {
                val weather_data =
                    Weather_station_davis_API.m_weather_station_davis_API_service.get_weather_station_data();
                val weather_page =
                    Weather_station_davis_API.m_weather_station_davis_API_service.get_weather_station_page();
                m_weather_data_fetch_response.value = arrayOf("success", weather_data, weather_page);
            }
            catch(exception: Exception)
            {
                m_weather_data_fetch_response.value = arrayOf("failure", "${exception.message}");
            }
        }
    }
}

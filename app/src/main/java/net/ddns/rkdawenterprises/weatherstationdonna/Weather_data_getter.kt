@file:Suppress("ClassName",
               "FunctionName",
               "RedundantSemicolon",
               "PrivatePropertyName",
               "LocalVariableName")

package net.ddns.rkdawenterprises.weatherstationdonna

import android.util.Log
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.concurrent.timerTask

class Weather_data_getter
{
    companion object
    {
        private const val LOG_TAG = "Weather_data_getter";
    }

    private var m_timer: Timer? = null;

    fun stop_update()
    {
        Log.d(LOG_TAG,
              ">>>> stop");

        m_timer?.cancel();
        m_timer = null;
    }

    fun start_update(update_period_ms: Long)
    {
        stop_update();

        Log.d(LOG_TAG,
              ">>>> $update_period_ms ms");
        Log.d(LOG_TAG,
              ">>>> ${Main_activity.get_weather_data_URI().toString()}");
        Log.d(LOG_TAG,
              ">>>> ${Main_activity.get_weather_history_URI_prefix().toString()}${Main_activity.get_all_weather_history_postfix()}");
        Log.d(LOG_TAG,
              ">>>> ${Main_activity.get_weather_history_URI_prefix().toString()}${Main_activity.get_recent_weather_history_postfix()}");

        m_timer = Timer();
        m_timer?.scheduleAtFixedRate(timerTask
                                     {
                                         Log.e(LOG_TAG,
                                               "Fetch data...");
                                     },
                                     0,
                                     update_period_ms);
    }
}
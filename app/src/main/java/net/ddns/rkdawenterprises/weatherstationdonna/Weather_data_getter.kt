@file:Suppress("ClassName",
               "FunctionName",
               "RedundantSemicolon",
               "PrivatePropertyName",
               "LocalVariableName")

package net.ddns.rkdawenterprises.weatherstationdonna

import android.accounts.NetworkErrorException
import android.os.Handler
import android.util.Log
import net.ddns.rkdawenterprises.rkdawe_api_common.Directory_listing
import net.ddns.rkdawenterprises.rkdawe_api_common.Weather_data
import java.io.IOException
import java.util.*
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection
import kotlin.concurrent.timerTask

class Weather_data_getter(val m_weather_data_UI_update: Handler)
{
    companion object
    {
        private const val LOG_TAG = "Weather_data_getter";

        private const val PING = 1;
        private const val PONG = 0;

        private const val NETWORK_TIMEOUT = 5000;
        private const val READ_BUFFER_LENGTH = 8 * 1024;

        private var m_timer: Timer? = null;

        private var m_buffer_ping_n_pong: Boolean = false;

        private val m_read_buffer_ping = CharArray(READ_BUFFER_LENGTH)
        private val m_read_buffer_pong = CharArray(READ_BUFFER_LENGTH)
    }

    fun stop_update()
    {
        Log.d(LOG_TAG,
              ">>>> stop");

        m_timer?.cancel();
        m_timer?.purge();
        m_timer = null;
    }

    fun start_update(update_period_ms: Long)
    {
        Log.d(LOG_TAG,
              ">>>> $update_period_ms ms");
        Log.d(LOG_TAG,
              ">>>> ${Main_activity.get_weather_data_URI().toString()}");
        Log.d(LOG_TAG,
              ">>>> ${Main_activity.get_weather_history_URI_prefix().toString()}");

        if(m_timer != null) stop_update();
        m_timer = Timer();
        m_timer?.scheduleAtFixedRate(timerTask
                                     {
                                         Log.d(LOG_TAG,
                                               m_timer.toString())
                                         get_data()
                                     },
                                     0,
                                     update_period_ms);
    }

    private fun get_data()
    {
        Log.d(LOG_TAG,
              ">>>>>>>> +get_data()")
        m_buffer_ping_n_pong = !m_buffer_ping_n_pong;

        update_weather_data();




        m_weather_data_UI_update.sendEmptyMessage(if(m_buffer_ping_n_pong) PING else PONG);
    }

    @Throws(IOException::class,
            NetworkErrorException::class)
    private fun update_weather_data()
    {
        var weather_data: Weather_data;
        var history_dir: Directory_listing;

        Main_activity.get_weather_data_URI()?.let {
            with(it.toURL().openConnection() as HttpsURLConnection)
            {
                try
                {
                    readTimeout = NETWORK_TIMEOUT;
                    connectTimeout = NETWORK_TIMEOUT;
                    requestMethod = "GET";
                    doInput = true;
                    connect();
                    if(responseCode != HttpsURLConnection.HTTP_OK)
                    {
                        throw NetworkErrorException("HTTP error code: $responseCode");
                    }

                    with(inputStream.bufferedReader())
                    {
                        val text: String = lines().collect(Collectors.joining("\n"))
                        Log.d(LOG_TAG,
                              text);
                        Weather_data.deserialize_from_JSON(text);
                    }
                }
                finally
                {
                    disconnect();
                }
            }






            
        };


//        with(get_URI.openConnection() as HttpsURLConnection)
//        {
//            try
//            {
//                readTimeout = NETWORK_TIMEOUT
//                connectTimeout = NETWORK_TIMEOUT
//                requestMethod = "GET"
//                doInput = true
//                connect()
//                if(responseCode != HttpsURLConnection.HTTP_OK)
//                {
//                    throw NetworkErrorException("HTTP error code: $responseCode")
//                }
//
//                with(inputStream.bufferedReader())
//                {
//                    lines().forEach { line ->
//                        Log.d(LOG_TAG, line) }
//                }
//            }
//            catch( exception: Exception ){ return ""; }
//            finally
//            {
//                disconnect()
//            }
//        }
    }
}
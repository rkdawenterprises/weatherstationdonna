@file:Suppress("ClassName",
               "FunctionName",
               "RedundantSemicolon",
               "PrivatePropertyName",
               "LocalVariableName",
               "PropertyName",
               "PackageName")

package net.ddns.rkdawenterprises.weatherstationdonna

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.Log
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.WindowCompat
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import net.ddns.rkdawenterprises.davis_website.Data_parser
import net.ddns.rkdawenterprises.davis_website.Weather_data.get_forecast_icon_uri_for_date
import net.ddns.rkdawenterprises.davis_website.Weather_data_container
import net.ddns.rkdawenterprises.davis_website.Weather_page
import net.ddns.rkdawenterprises.rkdawe_api_common.Get_weather_station_data_GET_response
import net.ddns.rkdawenterprises.rkdawe_api_common.Utilities.convert_time_UTC_to_local
import net.ddns.rkdawenterprises.rkdawe_api_common.Weather_data
import net.ddns.rkdawenterprises.weatherstationdonna.UI.Main
import net.ddns.rkdawenterprises.weatherstationdonna.UI.Weather_data_view_model
import net.ddns.rkdawenterprises.weatherstationdonna.databinding.ActivityMainBinding
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Some older devices needs a small delay between UI widget updates and a change of the status and navigation bar.
 * Time is in milliseconds.
 */
const val TOOLBAR_SHOW_HIDE_ANIMATION_DELAY = 100;

/**
 * Trigger the initial toolbar hide shortly after the activity has been created.
 * Time is in milliseconds.
 */
const val INITIAL_HIDE_DELAY = 500;

class Main_activity: AppCompatActivity()
{
    companion object
    {
        private const val LOG_TAG = "Main_activity";
    }

    private lateinit var m_binding: ActivityMainBinding;

    private val m_show_hide_handler = Handler(Looper.myLooper()!!);

    private val m_weather_data: Weather_data_view_model by viewModels();

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState);

        initialize_saved_settings();

        m_binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(m_binding.root);

        setSupportActionBar(m_binding.toolbar);
        supportActionBar?.setHomeButtonEnabled(true);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        val compose_view = m_binding.scrollingContent;
        compose_view.setContent { Main(this); }
        compose_view.setOnClickListener { toggle(); }

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        window.attributes.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;

        WindowCompat.setDecorFitsSystemWindows(window, false);

        // When interacting with the menu, delay any scheduled hide.
        supportActionBar?.addOnMenuVisibilityListener { visible->
            if(visible)
            {
                m_show_hide_handler.removeCallbacks(hide_toolbars_runnable);
                return@addOnMenuVisibilityListener;
            }
            else if(get_auto_hide_toolbars())
            {
                delayed_hide();
            }
        }

//        m_weather_data.combined_response.observe(this) { result->
//            val RKDAWE_response = result.first;
//            val davis_response = result.second;
//            if((RKDAWE_response != null) && (davis_response != null))
//            {
//                check_RKDAWE_response(RKDAWE_response, davis_response, false);
//
//                if(m_binding.swipeToRefresh.isRefreshing)
//                {
//                    m_binding.swipeToRefresh.isRefreshing = false;
//                }
//            }
//        }

        m_binding.swipeToRefresh.setOnRefreshListener {
            if(is_ok_to_fetch_data())
            {
//                m_weather_data.get_weather_data();
            }
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?)
    {
        super.onPostCreate(savedInstanceState);

        if(is_ok_to_fetch_data())
        {
//            m_weather_data.get_weather_data();
        }

        delayed_hide(INITIAL_HIDE_DELAY);
    }

//    private fun check_RKDAWE_response(RKDAWE_result: Array<String>,
//                                      davis_result: Array<String>,
//                                      store_update_not: Boolean)
//    {
//        val fetch_status = RKDAWE_result[0];
//        if(fetch_status == "success")
//        {
//            Log.d(LOG_TAG, "Got donna data...")
//            val weather_data_string_JSON = RKDAWE_result[1];
//            val get_weather_data_response =
//                Get_weather_station_data_GET_response.deserialize_from_JSON(weather_data_string_JSON);
//            val status = get_weather_data_response.success;
//            if(status == "true")
//            {
//                check_davis_response(get_weather_data_response.weather_data,
//                                     davis_result,
//                                     store_update_not);
//            }
//            else
//            {
//                display_fetch_issue();
//            }
//        }
//        else
//        {
//            display_fetch_issue();
//        }
//    }

//    private fun check_davis_response(weather_data: Weather_data,
//                                     davis_result: Array<String>,
//                                     store_update_not: Boolean)
//    {
//        val fetch_status = davis_result[0];
//        if(fetch_status == "success")
//        {
//            Log.d(LOG_TAG, "Got davis data...")
//            val parsed: Weather_data_container =
//                Data_parser.parse(davis_result[2], davis_result[1]);
//
//            if(store_update_not)
//            {
//                val shared_prefs = getPreferences(Context.MODE_PRIVATE);
//                val edit = shared_prefs.edit();
//                edit.putString(LAST_WEATHER_DATA_FETCHED_KEY,
//                               weather_data.serialize_to_JSON());
//                edit.putString(LAST_WEATHER_DATA_DAVIS_FETCHED_KEY,
//                               parsed.json_data.serialize_to_JSON());
//                edit.putString(LAST_WEATHER_PAGE_DAVIS_FETCHED_KEY,
//                               parsed.page_data.serialize_to_JSON());
//                edit.apply();
//            }
//            else
//            {
//                update_UI_with_weather_data(weather_data,
//                                            parsed.json_data,
//                                            parsed.page_data);
//            }
//        }
//        else
//        {
//            display_fetch_issue();
//        }
//    }

//    private fun display_fetch_issue()
//    {
//        if(m_binding.swipeToRefresh.isRefreshing)
//        {
//            m_binding.swipeToRefresh.isRefreshing = false;
//        }
//
//        val message = resources.getString(R.string.unable_to_get_weather_data);
//        Log.d(LOG_TAG, "display_fetch_issue: $message");
//        Snackbar.make(m_binding.root,
//                      message,
//                      Snackbar.LENGTH_LONG).setAction(R.string.ok) {}.show();
//    }

    private val hide_toolbars_runnable = Runnable { hide_toolbars(); }

    private val show_action_bar_runnable = Runnable {
        supportActionBar?.show();

        if(get_auto_hide_toolbars())
        {
            delayed_hide();
        }
    }

    private val hide_status_and_navigation_bars_runnable = Runnable {
        hide_status_and_navigation_toolbars();
    }

    private fun hide_toolbars()
    {
        supportActionBar?.hide();

        m_show_hide_handler.removeCallbacks(show_action_bar_runnable);
        m_show_hide_handler.postDelayed(hide_status_and_navigation_bars_runnable, TOOLBAR_SHOW_HIDE_ANIMATION_DELAY.toLong());
    }

    private fun show_toolbars()
    {
        show_status_and_navigation_toolbars();

        m_show_hide_handler.removeCallbacks(hide_status_and_navigation_bars_runnable);
        m_show_hide_handler.postDelayed(show_action_bar_runnable, TOOLBAR_SHOW_HIDE_ANIMATION_DELAY.toLong());
    }

    /**
     * Schedules a call to hide() after the given [delay_in_milliseconds].
     * Any previously scheduled calls are removed.
     *
     * @param delay_in_milliseconds Time in milliseconds to delay before hiding the toolbars.
     *
     * @return void
     */
    private fun delayed_hide(delay_in_milliseconds: Int = get_auto_hide_toolbars_delay())
    {
        m_show_hide_handler.removeCallbacks(hide_toolbars_runnable);
        m_show_hide_handler.postDelayed(hide_toolbars_runnable, delay_in_milliseconds.toLong());
    }

    private fun toggle()
    {
        if(supportActionBar?.isShowing == true)
        {
            hide_toolbars();
        }
        else
        {
            show_toolbars();
        }
    }

    private fun hide_status_and_navigation_toolbars()
    {
        if(Build.VERSION.SDK_INT >= 30)
        {
            m_binding.scrollingContent.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        }
        else
        {
            @Suppress("DEPRECATION")
            m_binding.scrollingContent.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
    }

    private fun show_status_and_navigation_toolbars()
    {
        if(Build.VERSION.SDK_INT >= 30)
        {
            m_binding.scrollingContent.windowInsetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        }
        else
        {
            @Suppress("DEPRECATION")
            m_binding.scrollingContent.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        if(menu is MenuBuilder)
        {
            menu.setOptionalIconsVisible(true);
        }

        menuInflater.inflate(R.menu.main_menu,
                             menu);

        menu.findItem(R.id.action_download_over_wifi_only).isChecked = get_download_over_wifi_only();
        menu.findItem(R.id.action_auto_hide_toolbars).isChecked = get_auto_hide_toolbars();

        return true
    }

    @Suppress("unused")
    private fun log_shared_preferences()
    {
        val shared_prefs = getPreferences(Context.MODE_PRIVATE);
        val all: Map<String, *> = shared_prefs.all;
        all.forEach { entry->
            if(entry.key == "dark_mode_selection")
            {
                val selections = resources.getStringArray(R.array.night_mode_selection);
                Log.i(LOG_TAG, "${entry.key}[${entry.value.toString()}] : ${selections[entry.value.toString().toInt()]}");
            }
            else
            {
                Log.i(LOG_TAG, "${entry.key} : ${entry.value.toString()}");
            }
        }
    }

    /**
     * Do an initial "get" of all settings to make sure preferences is set up properly.
     */
    private fun initialize_saved_settings()
    {
        update_night_mode(get_night_mode_selection());
        get_download_over_wifi_only();
        get_auto_hide_toolbars();
        get_auto_hide_toolbars_delay();
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        val item_ID = item.itemId;

        hide_toolbars();

        if(item_ID == R.id.action_refresh_weather_data)
        {
            if(is_ok_to_fetch_data())
            {
                m_binding.swipeToRefresh.post { m_binding.swipeToRefresh.isRefreshing = true }
//                m_weather_data.get_weather_data();
            }

            return true;
        }

        if(item_ID == R.id.action_set_night_mode_theme)
        {
            val selections = resources.getStringArray(R.array.night_mode_selection);

            MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.select_night_mode)
                    .setSingleChoiceItems(selections, get_night_mode_selection(), null)
                    .setPositiveButton(resources.getString(R.string.ok)) { dialog, _->
                        val selection = (dialog as AlertDialog).listView.checkedItemPosition;
                        put_night_mode_selection(selection);
                        update_night_mode(selection)
                    }
                    .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _-> dialog.cancel(); }
                    .show();

            return true;
        }

        if(item_ID == R.id.action_download_over_wifi_only)
        {
            val changed_value = !item.isChecked;
            item.isChecked = changed_value;
            put_download_over_wifi_only(changed_value);

            return true;
        }

        if(item_ID == R.id.action_auto_hide_toolbars)
        {
            val changed_value = !item.isChecked;
            item.isChecked = changed_value;
            put_auto_hide_toolbars(changed_value);

            return true;
        }

        if((item_ID == android.R.id.home) || (item_ID == R.id.action_exit))
        {
            val message = String.format("%s %s",
                                        resources.getString(R.string.exiting),
                                        resources.getString(R.string.app_name));
            Log.d(LOG_TAG, "onOptionsItemSelected: $message");
            Snackbar.make(m_binding.root,
                          message,
                          Snackbar.LENGTH_LONG).setAction(R.string.ok) {}.show();

            // Allow menu and toast time to close.
            Handler(Looper.getMainLooper()).postDelayed({
                                                            moveTaskToBack(true);
                                                            finish();
                                                        },
                                                        1000);
            return true;
        }

        return super.onOptionsItemSelected(item)
    }

    private fun update_night_mode(selection: Int)
    {
        val selections = resources.getStringArray(R.array.night_mode_options);

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

    private fun is_ok_to_fetch_data(): Boolean
    {
        var is_metered = true;

        val connectivity_manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager;
        val network_capabilities = connectivity_manager.getNetworkCapabilities(connectivity_manager.activeNetwork);
        if(network_capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == false) is_metered = false;
        if(network_capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED) == true) is_metered = false;

        val shared_prefs = getPreferences(Context.MODE_PRIVATE);
        val download_over_wifi_only = shared_prefs.getBoolean(DOWNLOAD_OVER_WIFI_ONLY_KEY,
                                                              resources.getBoolean(R.bool.download_over_wifi_only_default))
        val is_ok = !(is_metered && download_over_wifi_only);

        if(!is_ok)
        {
            val message = resources.getString(R.string.not_allowed_to_get_weather_data_unless_over_wifi);
            Log.d(LOG_TAG, "is_ok_to_fetch_data: $message");
            Snackbar.make(m_binding.root,
                          message,
                          Snackbar.LENGTH_SHORT).setAction(R.string.ok) {}.show();
        }

        return is_ok;
    }

    override fun onPause()
    {
//        val RKDAWE_response = m_weather_data.RKDAWE_response.value;
//        val davis_response = m_weather_data.davis_response.value;
//        if((RKDAWE_response != null) && (davis_response != null))
//        {
//            check_RKDAWE_response(RKDAWE_response, davis_response, true);
//        }

        super.onPause()
    }

    override fun onResume()
    {
        super.onResume();

//        val shared_prefs = getPreferences(Context.MODE_PRIVATE);
//        val weather_data_JSON_string = shared_prefs.getString(LAST_WEATHER_DATA_FETCHED_KEY, null);
//        val weather_data_davis_JSON_string = shared_prefs.getString(LAST_WEATHER_DATA_DAVIS_FETCHED_KEY, null);
//        val weather_page_davis_JSON_string = shared_prefs.getString(LAST_WEATHER_PAGE_DAVIS_FETCHED_KEY, null);
//        if((weather_data_JSON_string != null)
//            && (weather_data_davis_JSON_string != null)
//            && (weather_page_davis_JSON_string != null))
//        {
//            val weather_data = Weather_data.deserialize_from_JSON(weather_data_JSON_string);
//            val weather_data_davis =
//                net.ddns.rkdawenterprises.davis_website.Weather_data.deserialize_from_JSON(weather_data_davis_JSON_string);
//            val weather_page_davis =
//                Weather_page.deserialize_from_JSON(weather_page_davis_JSON_string);
//            update_UI_with_weather_data(weather_data,
//                                        weather_data_davis,
//                                        weather_page_davis);
//        }
    }

//    private fun update_UI_with_weather_data(weather_data: Weather_data,
//                                            json_data: net.ddns.rkdawenterprises.davis_website.Weather_data,
//                                            page_data: Weather_page)
//    {
//        m_binding.systemName.text = weather_data.system_name;
//
//        m_binding.conditionsAsOf.text = resources.getString(R.string.conditions_as_of_format,
//                                                            convert_time_UTC_to_local(weather_data.time,
//                                                                                      "h:mm a EEEE, MMM d, yyyy"));
//        val forecast_URI: String =
//            get_forecast_icon_uri_for_date(convert_time_UTC_to_local(weather_data.time),
//                                           json_data.forecastOverview);
//        Glide.with(this).load(forecast_URI).fitCenter().into(m_binding.forecastIcon);
//
//        m_binding.currentTemperature.text =
//            Html.fromHtml("${weather_data.outside_temperature} ${weather_data.temperature_units}",
//                          Html.FROM_HTML_MODE_COMPACT);
//
//        m_binding.todaysTemperatureHigh.text =
//            Html.fromHtml(getString(R.string.high_format,
//                                    String.format("%.1f", weather_data.day_hi_out_temp),
//                                    weather_data.temperature_units),
//                          Html.FROM_HTML_MODE_COMPACT);
//
//        m_binding.todaysTemperatureHighTime.text =
//            resources.getString(R.string.at_format,
//                                convert_time_UTC_to_local(weather_data.time_day_hi_out_temp,
//                                                          "h:mm a"));
//
//        m_binding.todaysTemperatureLow.text =
//            Html.fromHtml(getString(R.string.low_format,
//                                    String.format("%.1f", weather_data.day_low_out_temp),
//                                    weather_data.temperature_units),
//                          Html.FROM_HTML_MODE_COMPACT);
//
//        m_binding.todaysTemperatureLowTime.text =
//            resources.getString(R.string.at_format,
//                                convert_time_UTC_to_local(weather_data.time_day_low_out_temp,
//                                                          "h:mm a"));
//
//        m_binding.currentHumidity.text =
//        Html.fromHtml(" ${weather_data.outside_humidity} ${weather_data.humidity_units}",
//                      Html.FROM_HTML_MODE_COMPACT);
//
//        m_binding.humidityDescription.text =
//            resources.getString(R.string.humidity_description_format, json_data.temperatureFeelLike );
//
//    }
}
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
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.WindowCompat
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.ddns.rkdawenterprises.weatherstationdonna.UI.Main
import net.ddns.rkdawenterprises.weatherstationdonna.UI.Weather_data_view_model
import net.ddns.rkdawenterprises.weatherstationdonna.databinding.ActivityMainBinding
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

    private var m_night_mode_selection: Int = resources.getInteger(R.integer.night_mode_selection_default);
    private var m_is_download_over_wifi_only: Boolean = resources.getBoolean(R.bool.download_over_wifi_only_default);
    private var m_is_auto_hide_toolbars: Boolean = resources.getBoolean(R.bool.auto_hide_toolbars_default);
    private var m_auto_hide_toolbars_delay: Int = resources.getInteger(R.integer.auto_hide_toolbars_delay_default);
    private var m_is_ok_to_fetch_data: Boolean = false;

    var m_last_weather_data_fetched: User_settings.Data_storage = User_settings.Data_storage();

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
        compose_view.setContent { Main(this, m_weather_data); }
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
            else if(m_is_auto_hide_toolbars)
            {
                delayed_hide();
            }
        }

        m_weather_data.combined_response.observe(this) { result->
            if(( result.first_status == "success") && (result.first_data != null))
            {
                m_last_weather_data_fetched.first_status = result.first_status;
                m_last_weather_data_fetched.first_data = result.first_data;
            }

            if(( result.second_status == "success") && (result.second_data != null))
            {
                m_last_weather_data_fetched.second_status = result.second_status;
                m_last_weather_data_fetched.second_data = result.second_data;
            }

            if(( result.third_status == "success") && (result.third_data != null))
            {
                m_last_weather_data_fetched.third_status = result.third_status;
                m_last_weather_data_fetched.third_data = result.third_data;
            }
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
        }

        m_binding.swipeToRefresh.setOnRefreshListener {
            if(m_is_ok_to_fetch_data)
            {
                m_weather_data.refresh();
            }
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?)
    {
        super.onPostCreate(savedInstanceState);

//        if(is_ok_to_fetch_data())
//        {
//            m_weather_data.get_weather_data();
//        }

        delayed_hide(INITIAL_HIDE_DELAY);
    }

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

        if(m_is_auto_hide_toolbars)
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
    private fun delayed_hide(delay_in_milliseconds: Int = m_auto_hide_toolbars_delay)
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

        menu.findItem(R.id.action_download_over_wifi_only).isChecked = m_is_download_over_wifi_only;
        menu.findItem(R.id.action_auto_hide_toolbars).isChecked = m_is_auto_hide_toolbars;

        return true
    }

    private fun initialize_saved_settings()
    {
        // TODO: Get all saved settings.
        m_night_mode_selection = resources.getInteger(R.integer.night_mode_selection_default);
        m_is_download_over_wifi_only = resources.getBoolean(R.bool.download_over_wifi_only_default);
        m_is_auto_hide_toolbars = resources.getBoolean(R.bool.auto_hide_toolbars_default);
        m_auto_hide_toolbars_delay = resources.getInteger(R.integer.auto_hide_toolbars_delay_default);
        m_is_ok_to_fetch_data = false;

        User_settings.get_night_mode_selection(this).asLiveData().observe(this) { night_mode_selection ->
            m_night_mode_selection = night_mode_selection;
        }

        User_settings.get_download_over_wifi_only(this).asLiveData().observe(this) { is_download_over_wifi_only ->
            m_is_download_over_wifi_only = is_download_over_wifi_only;
        }

        User_settings.get_auto_hide_toolbars(this).asLiveData().observe(this) { is_auto_hide_toolbars ->
            m_is_auto_hide_toolbars = is_auto_hide_toolbars;
        }

        User_settings.get_auto_hide_toolbars_delay(this).asLiveData().observe(this) { auto_hide_toolbars_delay ->
            m_auto_hide_toolbars_delay = auto_hide_toolbars_delay;
        }

        User_settings.is_ok_to_fetch_data(this).asLiveData().observe(this) { is_ok_to_fetch_data ->
            m_is_ok_to_fetch_data = is_ok_to_fetch_data;
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        val item_ID = item.itemId;

        hide_toolbars();

        if(item_ID == R.id.action_refresh_weather_data)
        {
            if(m_is_ok_to_fetch_data)
            {
                m_binding.swipeToRefresh.post { m_binding.swipeToRefresh.isRefreshing = true }
                m_weather_data.refresh();
            }

            return true;
        }

        if(item_ID == R.id.action_set_night_mode_theme)
        {
            val selections = resources.getStringArray(R.array.night_mode_options);

            MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.select_night_mode)
                    .setSingleChoiceItems(selections, m_night_mode_selection, null)
                    .setPositiveButton(resources.getString(R.string.ok)) { dialog, _->
                        val selection = (dialog as AlertDialog).listView.checkedItemPosition;
                        m_weather_data.set_night_mode_selection(this, selection);
                    }
                    .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _-> dialog.cancel(); }
                    .show();

            return true;
        }

        if(item_ID == R.id.action_download_over_wifi_only)
        {
            val changed_value = !item.isChecked;
            item.isChecked = changed_value;
            m_weather_data.set_download_over_wifi_only(this, changed_value);

            return true;
        }

        if(item_ID == R.id.action_auto_hide_toolbars)
        {
            val changed_value = !item.isChecked;
            item.isChecked = changed_value;
            m_weather_data.set_auto_hide_toolbars(this, changed_value);

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

    override fun onPause()
    {
        if(!m_last_weather_data_fetched.is_empty())
        {
            m_weather_data.set_last_weather_data_fetched(this, m_last_weather_data_fetched);
        }

        super.onPause()
    }

    override fun onResume()
    {
        super.onResume();

        val context: Context = this;
        lifecycleScope.launch {
            m_last_weather_data_fetched = User_settings.get_last_data(context).first();

            if(!m_last_weather_data_fetched.is_empty())
            {
                m_weather_data.refresh(m_last_weather_data_fetched);
            }

            if(m_is_ok_to_fetch_data)
            {
                m_weather_data.refresh();
            }
        }
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
@file:Suppress("ClassName",
               "FunctionName",
               "RedundantSemicolon",
               "PrivatePropertyName",
               "LocalVariableName")

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
import android.util.Log
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.WindowCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import net.ddns.rkdawenterprises.rkdawe_api_common.Get_weather_station_data_GET_response
import net.ddns.rkdawenterprises.rkdawe_api_common.Weather_data
import net.ddns.rkdawenterprises.weatherstationdonna.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
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

/**
 * Weather data URI preferences storage key.
 */
private const val DARK_MODE_SELECTION_KEY = "dark_mode_selection";

/**
 * Download over WiFi only preferences storage key.
 */
const val DOWNLOAD_OVER_WIFI_ONLY_KEY = "download_over_wifi_only";

/**
 * Auto-hide toolbars preferences storage key.
 */
private const val AUTO_HIDE_TOOLBARS_KEY = "auto_hide_toolbars";

/**
 * Auto-hide toolbars delay preferences storage key.
 */
private const val AUTO_HIDE_TOOLBARS_DELAY_KEY = "auto_hide_toolbars_delay";

/**
 * Last weather data fetched.
 */
private const val LAST_WEATHER_DATA_FETCHED_KEY = "last_weather_data_fetched";

class Main_activity: AppCompatActivity()
{
    companion object
    {
        private const val LOG_TAG = "Main_activity";
    }

    private lateinit var m_binding: ActivityMainBinding;

    private val m_show_hide_handler = Handler(Looper.myLooper()!!);

    private val m_weather_station_donna_data_view_model: Weather_station_donna_data_view_model by viewModels();
    private val m_weather_station_davis_data_view_model: Weather_station_davis_data_view_model by viewModels();

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState);

        initialize_saved_settings();

        m_binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(m_binding.root);

        setSupportActionBar(m_binding.toolbar);
        supportActionBar?.setHomeButtonEnabled(true);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        m_binding.scrollingContent.setOnClickListener { toggle(); }

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

        m_weather_station_donna_data_view_model.m_response.observe(this) { result->
            val fetch_status = result[0];
            if(fetch_status == "success")
            {
                val weather_data_string_JSON = result[1];
                val get_weather_data_response =
                    Get_weather_station_data_GET_response.deserialize_from_JSON(weather_data_string_JSON);
                val status = get_weather_data_response.success;
                if(status == "true")
                {
                    val weather_data = get_weather_data_response.weather_data;
                    update_UI_with_weather_data(weather_data);
                }
                else
                {
                    display_fetch_issue();
                }
            }
            else
            {
                display_fetch_issue();
            }

            if(m_binding.swipeToRefresh.isRefreshing)
            {
                m_binding.swipeToRefresh.isRefreshing = false;
            }
        }

        m_weather_station_davis_data_view_model.m_response.observe(this) { result->
            val fetch_status = result[0];
            if(fetch_status == "success")
            {
                Log.d(LOG_TAG, "Got davis data...")
            }
            else
            {
                display_fetch_issue();
            }
        }

        m_binding.swipeToRefresh.setOnRefreshListener {
            if(is_ok_to_fetch_data())
            {
                get_all_weather_data();
            }
        }
    }

    private fun display_fetch_issue()
    {
        val message = resources.getString(R.string.unable_to_get_weather_data);
        Log.d(LOG_TAG, "display_fetch_issue: $message");
        Snackbar.make(m_binding.root,
                      message,
                      Snackbar.LENGTH_LONG).setAction(R.string.ok) {}.show();
    }

    override fun onPostCreate(savedInstanceState: Bundle?)
    {
        super.onPostCreate(savedInstanceState);

        if(is_ok_to_fetch_data())
        {
            get_all_weather_data();
        }

        delayed_hide(INITIAL_HIDE_DELAY);
    }

    private fun get_all_weather_data()
    {
        m_weather_station_donna_data_view_model.get_weather_data()
        m_weather_station_davis_data_view_model.get_weather_data()
    }

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
                val selections = resources.getStringArray(R.array.dark_mode_selection);
                Log.i(LOG_TAG, "${entry.key}[${entry.value.toString()}] : ${selections[entry.value.toString().toInt()]}");
            }
            else
            {
                Log.i(LOG_TAG, "${entry.key} : ${entry.value.toString()}");
            }
        }
    }

    private fun get_dark_mode_selection(): Int
    {
        val shared_prefs = getPreferences(Context.MODE_PRIVATE);
        return shared_prefs.getInt(DARK_MODE_SELECTION_KEY,
                                   resources.getInteger(R.integer.dark_mode_selection_default));
    }

    private fun put_dark_mode_selection(index: Int)
    {
        val shared_prefs = getPreferences(Context.MODE_PRIVATE);
        val stored_index = get_dark_mode_selection();
        if(index != stored_index)
        {
            val edit = shared_prefs.edit();
            edit.putInt(DARK_MODE_SELECTION_KEY,
                        index);
            edit.apply()
        }
    }

    private fun get_download_over_wifi_only(): Boolean
    {
        val shared_prefs = getPreferences(Context.MODE_PRIVATE);
        return shared_prefs.getBoolean(DOWNLOAD_OVER_WIFI_ONLY_KEY,
                                       resources.getBoolean(R.bool.download_over_wifi_only_default));
    }

    private fun put_download_over_wifi_only(value: Boolean)
    {
        val shared_prefs = getPreferences(Context.MODE_PRIVATE);
        val stored_value = get_download_over_wifi_only();
        if(value != stored_value)
        {
            val edit = shared_prefs.edit();
            edit.putBoolean(DOWNLOAD_OVER_WIFI_ONLY_KEY,
                            value);
            edit.apply()
        }
    }

    private fun get_auto_hide_toolbars(): Boolean
    {
        val shared_prefs = getPreferences(Context.MODE_PRIVATE);
        return shared_prefs.getBoolean(AUTO_HIDE_TOOLBARS_KEY,
                                       resources.getBoolean(R.bool.auto_hide_toolbars_default));
    }

    private fun put_auto_hide_toolbars(value: Boolean)
    {
        val shared_prefs = getPreferences(Context.MODE_PRIVATE);
        val stored_value = get_auto_hide_toolbars();
        if(value != stored_value)
        {
            val edit = shared_prefs.edit();
            edit.putBoolean(AUTO_HIDE_TOOLBARS_KEY,
                            value);
            edit.apply()
        }
    }

    private fun get_auto_hide_toolbars_delay(): Int
    {
        val shared_prefs = getPreferences(Context.MODE_PRIVATE);
        return shared_prefs.getInt(AUTO_HIDE_TOOLBARS_DELAY_KEY,
                                   resources.getInteger(R.integer.auto_hide_toolbars_delay_default));
    }

    @Suppress("unused")
    private fun put_auto_hide_toolbars_delay(value: Int)
    {
        val shared_prefs = getPreferences(Context.MODE_PRIVATE);
        val stored_value = get_auto_hide_toolbars_delay();
        if(value != stored_value)
        {
            val edit = shared_prefs.edit();
            edit.putInt(AUTO_HIDE_TOOLBARS_DELAY_KEY,
                        value);
            edit.apply()
        }
    }

    /**
     * Do an initial "get" of all settings to make sure preferences is set up properly.
     */
    private fun initialize_saved_settings()
    {
        update_night_mode(get_dark_mode_selection());
        get_download_over_wifi_only();
        get_auto_hide_toolbars();
        get_auto_hide_toolbars_delay();
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        val item_ID = item.itemId;

        if(item_ID == R.id.action_refresh_weather_data)
        {
            if(is_ok_to_fetch_data())
            {
                get_all_weather_data();
            }

            return true;
        }

        if(item_ID == R.id.action_set_dark_mode_theme)
        {
            val selections = resources.getStringArray(R.array.dark_mode_selection);

            MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.select_dark_mode)
                    .setSingleChoiceItems(selections, get_dark_mode_selection(), null)
                    .setPositiveButton(resources.getString(R.string.ok)) { dialog, _->
                        val selection = (dialog as AlertDialog).listView.checkedItemPosition;
                        put_dark_mode_selection(selection);
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

    @Suppress("unused")
    private fun is_night_mode(): Boolean
    {
        var current = false;
        when(applicationContext.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK))
        {
            Configuration.UI_MODE_NIGHT_YES ->
            {
                current = true;
            }
        }

        return current;
    }

    private fun update_night_mode(selection: Int)
    {
        val selections = resources.getStringArray(R.array.dark_mode_selection);

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
        val response = m_weather_station_donna_data_view_model.m_response.value;
        val fetch_status = response?.get(0)
        if(fetch_status == "success")
        {
            val weather_data_string_JSON = response[1];
            val get_weather_data_response =
                Get_weather_station_data_GET_response.deserialize_from_JSON(weather_data_string_JSON);
            val status = get_weather_data_response.success;
            if(status == "true")
            {
                val weather_data = get_weather_data_response.weather_data;
                val shared_prefs = getPreferences(Context.MODE_PRIVATE);
                val edit = shared_prefs.edit();
                edit.putString(LAST_WEATHER_DATA_FETCHED_KEY,
                               weather_data.serialize_to_JSON());
                edit.apply()
            }
        }

        val response_davis = m_weather_station_donna_data_view_model.m_response.value;
        val fetch_status_davis = response_davis?.get(0)
        if(fetch_status_davis == "success")
        {
            val weather_data_string_JSON = response_davis[1];
            val weather_page_string_JSON = response_davis[2];
//            if(status == "true")
//            {
//                val weather_data = get_weather_data_response.weather_data;
//                val shared_prefs = getPreferences(Context.MODE_PRIVATE);
//                val edit = shared_prefs.edit();
//                edit.putString(LAST_WEATHER_DATA_FETCHED_KEY,
//                               weather_data.serialize_to_JSON());
//                edit.apply()
//            }
        }

        super.onPause()
    }

    override fun onResume()
    {
        super.onResume();

        Log.d(LOG_TAG, "onResume?+");

        val shared_prefs = getPreferences(Context.MODE_PRIVATE);
        val weather_data_JSON_string = shared_prefs.getString(LAST_WEATHER_DATA_FETCHED_KEY, null);
        if(weather_data_JSON_string != null)
        {
            val weather_data = Weather_data.deserialize_from_JSON(weather_data_JSON_string);
            update_UI_with_weather_data(weather_data);
        }
    }

    private fun update_UI_with_weather_data(weather_data: Weather_data)
    {
        m_binding.systemName.text = weather_data.system_name;

        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        formatter.timeZone = TimeZone.getTimeZone("UTC");
        val last_received_date_time_string =
            SimpleDateFormat("h:mm a EEEE, MMM d, yyyy", resources.configuration.locales[0])
                    .format(formatter.parse(weather_data.time));
        m_binding.conditionsAsOf.setText(getResources().getString(R.string.conditions_as_of_format,
                                                                  last_received_date_time_string));
    }
}
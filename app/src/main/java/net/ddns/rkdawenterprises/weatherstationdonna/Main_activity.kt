@file:Suppress("ClassName",
               "FunctionName",
               "RedundantSemicolon",
               "PrivatePropertyName",
               "LocalVariableName")

package net.ddns.rkdawenterprises.weatherstationdonna

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import android.view.*
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.webkit.URLUtil
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.view.menu.MenuBuilder
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import net.ddns.rkdawenterprises.weatherstationdonna.databinding.ActivityMainBinding
import java.net.URI


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
 * Weather data URI preferences storage key.
 */
private const val WEATHER_DATA_URI_KEY = "weather_data_uri";

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

class Main_activity: AppCompatActivity()
{
    companion object
    {
        private const val LOG_TAG = "Main_activity";
    }

    private lateinit var m_binding: ActivityMainBinding;
    private lateinit var m_scrolling_content: ConstraintLayout;

    private val m_show_hide_handler = Handler(Looper.myLooper()!!);

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState);

        initialize_saved_settings();

        m_binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(m_binding.root);

        setSupportActionBar(m_binding.toolbar);
        supportActionBar?.setHomeButtonEnabled(true);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        m_scrolling_content = m_binding.scrollingContent;
        m_scrolling_content.setOnClickListener { toggle(); }

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
    }

    override fun onPostCreate(savedInstanceState: Bundle?)
    {
        super.onPostCreate(savedInstanceState);

        delayed_hide(INITIAL_HIDE_DELAY);
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
            m_scrolling_content.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        }
        else
        {
            @Suppress("DEPRECATION")
            m_scrolling_content.systemUiVisibility =
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
            m_scrolling_content.windowInsetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        }
        else
        {
            @Suppress("DEPRECATION")
            m_scrolling_content.systemUiVisibility =
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

    private fun is_URI_valid(string_URI: String?): Boolean
    {
        string_URI ?: return false;
        return Patterns.WEB_URL.matcher(string_URI).matches() && URLUtil.isValidUrl(string_URI);
    }

    private fun validated_URI(string_URI: String?): URI?
    {
        return if(is_URI_valid(string_URI))
        {
            URI(string_URI);
        }
        else
        {
            null;
        }
    }

    private fun get_weather_data_URI(): URI?
    {
        val shared_prefs = getPreferences(Context.MODE_PRIVATE);
        val weather_data_URI = shared_prefs.getString(WEATHER_DATA_URI_KEY, null);
        return if(weather_data_URI != null)
        {
            validated_URI(weather_data_URI);
        }
        else
        {
            val weather_data_URI_default = validated_URI(resources.getString(R.string.weather_data_URI_default));
            weather_data_URI_default ?: return null;
            val edit = shared_prefs.edit();
            edit.putString(WEATHER_DATA_URI_KEY, weather_data_URI_default.toString());
            edit.apply();
            weather_data_URI_default;
        }
    }

    private fun put_weather_data_URI(path: String): Boolean
    {
        val shared_prefs = getPreferences(Context.MODE_PRIVATE);
        val stored_path = get_weather_data_URI();

        if(path != stored_path.toString())
        {
            return if(is_URI_valid(path))
            {
                val edit = shared_prefs.edit();
                edit.putString(WEATHER_DATA_URI_KEY,
                               path);
                edit.apply()

                false;
            }
            else
            {
                // The given URI is not valid.
                true;
            }
        }

        return false;
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
        get_weather_data_URI();
        get_download_over_wifi_only();
        get_auto_hide_toolbars();
        get_auto_hide_toolbars_delay();
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        val item_ID = item.itemId;

        if(item_ID == R.id.action_refresh_weather_data)
        {
            // TODO: Fetch weather data.
            Log.d(LOG_TAG, "Fetching weather data...");
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

        if(item_ID == R.id.action_set_weather_data_uri)
        {
            val edittext = EditText(this);
            MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.enter_the_data_uri)
                    .setView(edittext)
                    .setPositiveButton(R.string.ok)
                    { _, _->
                        val text = edittext.text.toString().trim();
                        if(put_weather_data_URI(text))
                        {
                            Snackbar.make(m_binding.root,
                                          R.string.the_URI_you_entered_is_not_valid,
                                          Snackbar.LENGTH_LONG).setAction(R.string.ok) {}.show();
                        }
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _-> dialog.cancel(); }
                    .show();

            edittext.setText(get_weather_data_URI().toString());
            edittext.onFocusChangeListener = OnFocusChangeListener { _, _->
                edittext.post {
                    val inputMethodManager: InputMethodManager =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.showSoftInput(edittext, InputMethodManager.SHOW_IMPLICIT)
                }
            }
            edittext.postDelayed({
                                     edittext.requestFocus();
                                     edittext.selectAll();
                                 }, 100)

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
}
@file:Suppress("ClassName",
               "FunctionName",
               "RedundantSemicolon",
               "PrivatePropertyName",
               "LocalVariableName")

package net.ddns.rkdawenterprises.weatherstationdonna

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import net.ddns.rkdawenterprises.weatherstationdonna.databinding.MainLayoutBinding
import java.net.MalformedURLException
import java.net.URI
import java.net.URISyntaxException

class Main_activity : AppCompatActivity()
{
    companion object
    {
        private const val LOG_TAG = "Main_activity";

        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300

        /**
         * Refresh period preferences storage key.
         */
        private const val REFRESH_PERIOD_INDEX_KEY = "refresh_period_index";

        /**
         * Weather data URI preferences storage key.
         */
        private const val WEATHER_DATA_URI_KEY = "weather_data_uri";

        /**
         * Weather data URI preferences storage key.
         */
        private const val WEATHER_HISTORY_URI_PREFIX_KEY = "weather_history_uri_prefix";

        private lateinit var m_all_weather_history_postfix: String;
        fun get_all_weather_history_postfix(): String
        {
            return m_all_weather_history_postfix; }

        private lateinit var m_recent_weather_history_postfix: String;
        fun get_recent_weather_history_postfix(): String
        {
            return m_recent_weather_history_postfix; }

        /**
         * User modifiable settings.
         */
        private var m_refresh_period_index: Int = Int.MAX_VALUE;
        private var m_weather_data_URI: URI? = null;
        fun get_weather_data_URI(): URI?
        {
            return m_weather_data_URI; }

        private var m_weather_history_URI_prefix: URI? = null;
        fun get_weather_history_URI_prefix(): URI?
        {
            return m_weather_history_URI_prefix; }
    }

    private lateinit var binding: MainLayoutBinding;
    private lateinit var fullscreen_content: ConstraintLayout;

    private lateinit var m_weather_data_getter: Weather_data_getter;

    private var is_fullscreen: Boolean = false;

    private val hide_handler = Handler(Looper.myLooper()!!)
    private val hide_runnable = Runnable { hide(); }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        m_all_weather_history_postfix = resources.getString(R.string.all_weather_history_postfix);
        m_recent_weather_history_postfix = resources.getString(R.string.recent_weather_history_postfix);

        binding = MainLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Show the back button in action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        // Set up the user interaction to manually show or hide the system UI.
        fullscreen_content = binding.contentScrolling.fullscreenContent
        fullscreen_content.setOnClickListener { toggle() }

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        supportActionBar?.addOnMenuVisibilityListener { visible -> on_ui_control_visibility_change(visible) }
    }

    override fun onPostCreate(savedInstanceState: Bundle?)
    {
        super.onPostCreate(savedInstanceState);

        get_saved_settings();

        m_weather_data_getter = Weather_data_getter();
        lifecycle.addObserver(object : DefaultLifecycleObserver
                              {
                                  override fun onResume(owner: LifecycleOwner)
                                  {
                                      update_refresh_period();
                                  }

                                  override fun onPause(owner: LifecycleOwner)
                                  {
                                      m_weather_data_getter.stop_update();
                                  }
                              });

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayed_hide(100);

        Log.i(LOG_TAG,
              "$packageName has started...");
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu,
                             menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        val id = item.itemId;

        if(id == R.id.action_set_refresh_period)
        {
            val refresh_period_array = resources.getStringArray(R.array.refresh_period_list);

            AlertDialog.Builder(this)
                .setTitle(R.string.enter_the_refresh_period)
                .setSingleChoiceItems(refresh_period_array,
                                      m_refresh_period_index,
                                      null)
                .setPositiveButton(R.string.ok)
                { dialog, _ ->
                    hide();
                    update_refresh_index((dialog as AlertDialog).listView.checkedItemPosition);
                }
                .setNegativeButton(R.string.cancel)
                { dialog, _ ->
                    hide();
                    dialog.cancel();
                }
                .show();

            return true;
        }

        if(id == R.id.action_set_weather_data_uri)
        {
            val edittext = EditText(this)
            edittext.setText(m_weather_data_URI.toString())
            AlertDialog.Builder(this)
                .setTitle(R.string.enter_the_data_uri)
                .setView(edittext)
                .setPositiveButton(R.string.ok)
                { _, _ ->
                    hide();
                    val text = edittext.text.toString().trim();
                    try
                    {
                        update_weather_data_URI(text);
                    }
                    catch(e: URISyntaxException)
                    {
                        Toast.makeText(this,
                                       R.string.the_URI_you_entered_is_not_valid,
                                       Toast.LENGTH_SHORT).show();
                    }
                }
                .setNegativeButton(R.string.cancel)
                { dialog, _ ->
                    hide();
                    dialog.cancel();
                }
                .show();

            return true;
        }

        if(id == R.id.action_set_weather_history_URI_prefix)
        {
            val edittext = EditText(this)
            edittext.setText(m_weather_history_URI_prefix.toString())
            AlertDialog.Builder(this)
                .setTitle(R.string.enter_the_history_uri)
                .setView(edittext)
                .setPositiveButton(R.string.ok)
                { _, _ ->
                    hide();
                    val text = edittext.text.toString().trim();
                    try
                    {
                        update_weather_history_URI_prefix(text);
                    }
                    catch(e: URISyntaxException)
                    {
                        Toast.makeText(this,
                                       R.string.the_URI_you_entered_is_not_valid,
                                       Toast.LENGTH_SHORT).show();
                    }
                }
                .setNegativeButton(R.string.cancel)
                { dialog, _ ->
                    hide();
                    dialog.cancel();
                }
                .show();

            return true;
        }

        if((id == android.R.id.home) || (id == R.id.action_exit))
        {
            Toast.makeText(this,
                           R.string.exiting,
                           Toast.LENGTH_SHORT).show();
            moveTaskToBack(true);

            // Allow menu and toast time to close.
            Handler(Looper.getMainLooper()).postDelayed({ finish() },
                                                        1000);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private val hide_operation_Runnable = Runnable {
        // Delayed removal of status and navigation bar
        if(Build.VERSION.SDK_INT >= 30)
        {
            fullscreen_content.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars());
        }
        else
        {
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            @Suppress("DEPRECATION")
            fullscreen_content.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }
    }

    private val show_runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
    }

    private fun on_ui_control_visibility_change(visible: Boolean)
    {
        if(visible)
        {
            hide_handler.removeCallbacks(hide_runnable);
            return;
        }

        if(!visible && AUTO_HIDE)
        {
            delayed_hide();
            return;
        }
    }

    private fun toggle()
    {
        if(!is_fullscreen)
        {
            hide()
        }
        else
        {
            show()
        }
    }

    private fun hide()
    {
        // Hide UI first
        supportActionBar?.hide()
        is_fullscreen = true

        // Schedule a runnable to remove the status and navigation bar after a delay
        hide_handler.removeCallbacks(show_runnable)
        hide_handler.postDelayed(hide_operation_Runnable,
                                 UI_ANIMATION_DELAY.toLong())
    }

    private fun show()
    {
        // Show the system bar
        if(Build.VERSION.SDK_INT >= 30)
        {
            fullscreen_content.windowInsetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        }
        else
        {
            @Suppress("DEPRECATION")
            fullscreen_content.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }

        is_fullscreen = false

        // Schedule a runnable to display UI elements after a delay
        hide_handler.removeCallbacks(hide_operation_Runnable)
        hide_handler.postDelayed(show_runnable,
                                 UI_ANIMATION_DELAY.toLong())

        delayed_hide()
    }

    /**
     * Schedules a call to hide() in [delay_in_milliseconds], canceling any
     * previously scheduled calls.
     */
    private fun delayed_hide(delay_in_milliseconds: Int = AUTO_HIDE_DELAY_MILLIS)
    {
        hide_handler.removeCallbacks(hide_runnable)
        hide_handler.postDelayed(hide_runnable,
                                 delay_in_milliseconds.toLong())
    }

    /*
    private fun update_settingss()
    {

        var updated = false;
        if(m_refresh_period_index == Int.MAX_VALUE)
        {
            m_refresh_period_index = resources.getInteger(R.integer.refresh_period_default);
            updated = true;
        }

         || (refresh_period_index != m_refresh_period_index))
        {
            updated = true;
        }

        if( updated )
        {
            val refresh_period_array = resources.getStringArray(R.array.refresh_period_list);
            val refresh_period_parts = refresh_period_array[m_refresh_period_index].split(" ").toTypedArray();
            try
            {
                var value = refresh_period_parts[0].toInt();

                if(refresh_period_parts[1] == "sec")
                {
                    value *= 1000
                }

                if(refresh_period_parts[1] == "min")
                {
                    value *= 60 * 1000
                }

                m_weather_data_getter.set_data_update_period(value);
            }
            catch(e: NumberFormatException)
            {
                Log.e(LOG_TAG,
                      e.toString())
            }
        }

        updated = false;
        if(m_weather_data_URI == null)
        {
            m_weather_data_URI = URI(resources.getString(R.string.weather_data_URI_default));
            updated = true;
        }

        val weather_data_URI = shared_prefs.getString(WEATHER_DATA_URI_KEY,
                                                                null)
        if((weather_data_URI == null) || (weather_data_URI != m_weather_data_URI.toString()))
        {
            val edit = shared_prefs.edit();
            edit.putString(WEATHER_DATA_URI_KEY,
                           m_weather_data_URI.toString());
            edit.apply()
            updated = true;
        }

        if( updated )
        {
            m_weather_data_getter.notify_URI_change();
        }

        updated = false;
        if(m_weather_history_URI_prefix == null)
        {
            m_weather_history_URI_prefix = URI(resources.getString(R.string.weather_history_URI_prefix_default));
            updated = true;
        }

        val weather_history_uri_prefix = shared_prefs.getString(WEATHER_HISTORY_URI_PREFIX_KEY,
                                                      null)
        if((weather_history_uri_prefix == null) || (weather_history_uri_prefix != m_weather_history_URI_prefix.toString()))
        {
            val edit = shared_prefs.edit();
            edit.putString(WEATHER_HISTORY_URI_PREFIX_KEY,
                           m_weather_history_URI_prefix.toString());
            edit.apply()
            updated = true;
        }

        if( updated )
        {
            m_weather_data_getter.notify_URI_change();
        }
    }
*/
    private fun get_saved_settings()
    {
        val shared_prefs = getPreferences(Context.MODE_PRIVATE);
        m_refresh_period_index = shared_prefs.getInt(REFRESH_PERIOD_INDEX_KEY,
                                                     resources.getInteger(R.integer.refresh_period_default));
        m_weather_data_URI = URI(shared_prefs.getString(WEATHER_DATA_URI_KEY,
                                                        resources.getString(R.string.weather_data_URI_default)));
        m_weather_history_URI_prefix = URI(shared_prefs.getString(WEATHER_HISTORY_URI_PREFIX_KEY,
                                                                  resources.getString(R.string.weather_history_URI_prefix_default)));
    }

    private fun store_refresh_period(index: Int)
    {
        val shared_prefs = getPreferences(Context.MODE_PRIVATE);
        val refresh_period_index = shared_prefs.getInt(REFRESH_PERIOD_INDEX_KEY,
                                                       resources.getInteger(R.integer.refresh_period_default));
        if(index != refresh_period_index)
        {
            val edit = shared_prefs.edit();
            edit.putInt(REFRESH_PERIOD_INDEX_KEY,
                        index);
            edit.apply()
        }
    }

    private fun store_weather_data_URI(path: String)
    {
        val shared_prefs = getPreferences(Context.MODE_PRIVATE);
        val weather_data_URI = shared_prefs.getString(WEATHER_DATA_URI_KEY,
                                                      resources.getString(R.string.weather_data_URI_default))
        if(path != weather_data_URI)
        {
            val edit = shared_prefs.edit();
            edit.putString(WEATHER_DATA_URI_KEY,
                           path);
            edit.apply()
        }
    }

    private fun store_weather_history_uri_prefix(path: String)
    {
        val shared_prefs = getPreferences(Context.MODE_PRIVATE);
        val weather_history_uri_prefix = shared_prefs.getString(WEATHER_HISTORY_URI_PREFIX_KEY,
                                                                resources.getString(R.string.weather_history_URI_prefix_default))
        if(path != weather_history_uri_prefix)
        {
            val edit = shared_prefs.edit();
            edit.putString(WEATHER_HISTORY_URI_PREFIX_KEY,
                           path);
            edit.apply()
        }
    }

    private fun update_refresh_period()
    {
        val refresh_period_array = resources.getStringArray(R.array.refresh_period_list);
        val refresh_period_parts = refresh_period_array[m_refresh_period_index].split(" ").toTypedArray();
        try
        {
            var value = refresh_period_parts[0].toLong();

            if(refresh_period_parts[1] == "sec")
            {
                value *= 1000
            }

            if(refresh_period_parts[1] == "min")
            {
                value *= 60 * 1000
            }

            m_weather_data_getter.start_update(value);
        }
        catch(e: NumberFormatException)
        {
            Log.e(LOG_TAG,
                  e.toString())
        }
    }

    private fun update_refresh_index(refresh_period_index: Int)
    {
        if(m_refresh_period_index != refresh_period_index)
        {
            m_refresh_period_index = refresh_period_index;
            store_refresh_period(m_refresh_period_index);
            update_refresh_period();
        }
    }

    private fun update_weather_data_URI(path: String)
    {
        if(m_weather_data_URI.toString() != path)
        {
            try
            {
                m_weather_data_URI = URI(path);
                store_weather_data_URI(m_weather_data_URI.toString());
                update_refresh_period();
            }
            catch(e: MalformedURLException)
            {
                Log.e(LOG_TAG,
                      e.toString());
            }
        }
    }

    private fun update_weather_history_URI_prefix(path: String)
    {
        if(m_weather_history_URI_prefix.toString() != path)
        {
            try
            {
                m_weather_history_URI_prefix = URI(path);
                store_weather_history_uri_prefix(m_weather_history_URI_prefix.toString());
                update_refresh_period();
            }
            catch(e: MalformedURLException)
            {
                Log.e(LOG_TAG,
                      e.toString());
            }
        }
    }
}

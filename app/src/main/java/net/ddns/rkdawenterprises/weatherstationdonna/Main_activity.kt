/*
 * Copyright (c) 2019-2023 RKDAW Enterprises and Ralph Williamson.
 *       email: rkdawenterprises@gmail.com
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("ClassName",
               "FunctionName",
               "RedundantSemicolon",
               "PrivatePropertyName",
               "LocalVariableName",
               "PropertyName",
               "PackageName")

package net.ddns.rkdawenterprises.weatherstationdonna

import android.annotation.SuppressLint
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import net.ddns.rkdawenterprises.weatherstationdonna.UI.Main
import net.ddns.rkdawenterprises.weatherstationdonna.UI.Main_view_model
import net.ddns.rkdawenterprises.weatherstationdonna.databinding.ActivityMainBinding
import net.ddns.rkdawenterprises.weatherstationdonna.databinding.ForecastLocationBinding
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
 * The time to delay before automatically hiding the toolbars if auto-hide is enabled.
 * Time is in milliseconds.
 */
const val AUTO_HIDE_TOOLBARS_DELAY = 3500;

const val ACCESS_LOCATION_PERMISSION_REQUEST_CODE: Int = 8026

class Main_activity: AppCompatActivity()
{
    companion object
    {
        @Suppress("unused")
        private const val LOG_TAG = "Main_activity";
    }

    private lateinit var m_main_binding: ActivityMainBinding;

    private val m_show_hide_handler = Handler(Looper.myLooper()!!);

    private val m_main_view_model: Main_view_model by viewModels();

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState);

        m_main_view_model.load_night_mode_selection(this);

        m_main_binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(m_main_binding.root);

        setSupportActionBar(m_main_binding.toolbar);
        supportActionBar?.setHomeButtonEnabled(true);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        val compose_view = m_main_binding.contentView;
        compose_view.setContent { Main(); }
        compose_view.setOnClickListener { toggle(); }

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        window.attributes.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;

        WindowCompat.setDecorFitsSystemWindows(window, false);

        // When interacting with the menu, delay any scheduled hide.
        supportActionBar?.addOnMenuVisibilityListener()
        { visible->
            if(visible)
            {
                m_show_hide_handler.removeCallbacks(hide_toolbars_runnable);
                return@addOnMenuVisibilityListener;
            }
            else
            {
                m_main_view_model.load_auto_hide_toolbars(this)
                { is_auto_hide_toolbars ->
                    if(is_auto_hide_toolbars)
                    {
                        delayed_hide();
                    }
                }
            }
        }

        m_main_view_model.snackbar_message.observe(this)
        { value ->
            if(value.size == 1)
            {
                logging_ok_snackbar(value[0])
            }
            else if(value.size == 2)
            {
                logging_ok_snackbar(value[0],
                                    value[1])
            }
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?)
    {
        super.onPostCreate(savedInstanceState);

        delayed_hide(INITIAL_HIDE_DELAY);
    }

    private val hide_toolbars_runnable = Runnable { hide_toolbars(); }

    private val show_action_bar_runnable = Runnable()
    {
        supportActionBar?.show();

        m_main_view_model.load_auto_hide_toolbars(this)
        { is_auto_hide_toolbars ->
            if(is_auto_hide_toolbars)
            {
                delayed_hide();
            }
        }
    }

    private val hide_status_and_navigation_bars_runnable = Runnable()
    {
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
    private fun delayed_hide(delay_in_milliseconds: Int = AUTO_HIDE_TOOLBARS_DELAY)
    {
        m_show_hide_handler.removeCallbacks(hide_toolbars_runnable);
        m_show_hide_handler.postDelayed(hide_toolbars_runnable, delay_in_milliseconds.toLong());
    }

    fun toggle()
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
            m_main_binding.mainView.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        }
        else
        {
            @Suppress("DEPRECATION")
            m_main_binding.mainView.systemUiVisibility =
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
            m_main_binding.mainView.windowInsetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        }
        else
        {
            @Suppress("DEPRECATION")
            m_main_binding.mainView.systemUiVisibility =
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

        m_main_view_model.load_download_over_wifi_only(this)
        { is_download_over_wifi_only ->
            menu.findItem(R.id.action_download_over_wifi_only).isChecked = is_download_over_wifi_only;
        }

        m_main_view_model.load_auto_hide_toolbars(this)
        { is_auto_hide_toolbars ->
            menu.findItem(R.id.action_auto_hide_toolbars).isChecked = is_auto_hide_toolbars;
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        val item_ID = item.itemId;

        hide_toolbars();

        if(item_ID == R.id.action_refresh_weather_data)
        {
            m_main_view_model.is_ok_to_fetch_data(this)
            { is_ok_to_fetch_data ->
                if(is_ok_to_fetch_data)
                {
                    m_main_view_model.refresh();
                }
                else
                {
                    logging_ok_snackbar(getString(R.string.not_allowed_to_get_weather_data_unless_over_wifi));
                }
            }

            return true;
        }

        if(item_ID == R.id.action_set_night_mode_theme)
        {
            val selections = resources.getStringArray(R.array.night_mode_options);

            m_main_view_model.load_night_mode_selection(this)
            { night_mode_selection ->
                MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.select_night_mode)
                        .setSingleChoiceItems(selections, night_mode_selection, null)
                        .setPositiveButton(getString(R.string.ok)) { dialog, _->
                            val selection = (dialog as AlertDialog).listView.checkedItemPosition;
                            m_main_view_model.store_night_mode_selection(this, selection);
                        }
                        .setNegativeButton(getString(R.string.cancel)) { dialog, _-> dialog.cancel(); }
                        .show();
            }

            return true;
        }

        if(item_ID == R.id.action_set_forcast_location)
        {
            m_main_view_model.load_forecast_location_setting(this)
            { forecast_location_setting ->
                val binding: ForecastLocationBinding = ForecastLocationBinding.inflate(layoutInflater);
                val text_input = binding.forecastLocationEditText;
                val use_current_location_button = binding.useCurrentLocationButton;
                val use_default_location_button = binding.useDefaultLocationButton;
                val cancellation_source = CancellationTokenSource()
                val fused_location_provider_client: FusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(this);

                text_input.setText(forecast_location_setting);

                val alert_dialog: AlertDialog =
                MaterialAlertDialogBuilder(this)
                    .setView(binding.root)
                    .setTitle(R.string.set_forecast_location)
                    .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                        m_main_view_model.store_forecast_location_setting(this, text_input.text.toString());
                        cancellation_source.cancel();
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, _->
                        cancellation_source.cancel();
                        dialog.cancel(); }
                    .show();

                use_current_location_button.setOnClickListener()
                {
                    val access_coarse_location_permission =
                        ActivityCompat.checkSelfPermission(this,
                                                           android.Manifest.permission.ACCESS_COARSE_LOCATION);
                    val access_fine_location_permission =
                        ActivityCompat.checkSelfPermission(this,
                                                           android.Manifest.permission.ACCESS_FINE_LOCATION);
                    val is_have_location_permission = !(access_coarse_location_permission != PackageManager.PERMISSION_GRANTED
                            || access_fine_location_permission != PackageManager.PERMISSION_GRANTED);
                    if(is_have_location_permission)
                    {
                        text_input.setText(getString(R.string.getting_location));
                        fused_location_provider_client.getCurrentLocation(PRIORITY_BALANCED_POWER_ACCURACY,
                                                                          cancellation_source.token)
                            .addOnSuccessListener()
                            {
                                if (it == null)
                                {
                                    text_input.setText(getString(R.string.system_did_not_give_a_location));
                                }
                                else
                                {
                                    val latitude = it.latitude;
                                    val longitude = it.longitude;
                                    text_input.setText("$latitude,$longitude");
                                    Log.d(LOG_TAG, "$latitude,$longitude");
                                }
                            }
                    }
                    else
                    {
                        alert_dialog.cancel();
                        get_location_permissions_from_user();
                    }
                }

                use_default_location_button.setOnClickListener()
                {
                    text_input.setText(getString(R.string.forecast_location_setting_default));
                }
            }

            return true;
        }

        if(item_ID == R.id.action_download_over_wifi_only)
        {
            val changed_value = !item.isChecked;
            item.isChecked = changed_value;
            m_main_view_model.store_download_over_wifi_only(this, changed_value);

            return true;
        }

        if(item_ID == R.id.action_auto_hide_toolbars)
        {
            val changed_value = !item.isChecked;
            item.isChecked = changed_value;
            m_main_view_model.store_auto_hide_toolbars(this, changed_value);

            return true;
        }

        if((item_ID == android.R.id.home) || (item_ID == R.id.action_exit))
        {
            logging_ok_snackbar(String.format("%s %s",
                                        getString(R.string.exiting),
                                        getString(R.string.app_name)));

            // Allow menu and toast time to close.
            Handler(Looper.getMainLooper()).postDelayed({
                                                            moveTaskToBack(true);
                                                            finish();
                                                        },
                                                        1000);
            return true;
        }

        if(item_ID == R.id.action_about)
        {
            m_main_view_model.show_about_dialog();
            return true;
        }

        return super.onOptionsItemSelected(item)
    }

    private fun get_location_permissions_from_user()
    {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.location_permissions))
            .setMessage(getString(R.string.for_app_to_use_current_location))
            .setPositiveButton(getString(R.string.ok))
            { _, _ ->
                ActivityCompat.requestPermissions(this,
                                                  arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                                                          android.Manifest.permission.ACCESS_COARSE_LOCATION),
                                                  ACCESS_LOCATION_PERMISSION_REQUEST_CODE);
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _-> dialog.cancel(); }
            .show();
    }
    
    private fun logging_ok_snackbar(short_message: String, long_message: String = "")
    {
        if(long_message.isNotEmpty())
        {
            Log.d(LOG_TAG, "$short_message: $long_message")
        }
        else
        {
            Log.d(LOG_TAG, "$short_message")
        }

        Snackbar.make(m_main_binding.root,
                      short_message,
                      Snackbar.LENGTH_LONG).setAction(R.string.ok) {}.show();
    }

    override fun onPause()
    {
        val data_storage = m_main_view_model.combined_response.value;
        // TODO: Only store a good response.
        if(data_storage != null)
        {
            if(!data_storage.is_empty())
            {
                if((data_storage.m_data_RKDAWE != null)
                    && (data_storage.m_data_davis != null)
                    && (data_storage.m_page_davis != null))
                {
                    m_main_view_model.store_last_weather_data_fetched(this, data_storage);
                }
            }
        }

        super.onPause()
    }

    override fun onResume()
    {
        super.onResume();

        m_main_view_model.load_last_weather_data_fetched(this)
        { data_storage ->
            if(!data_storage.is_empty())
            {
                m_main_view_model.refresh(data_storage);
            }
        }

        m_main_view_model.is_ok_to_fetch_data(this)
        { is_ok_to_fetch_data ->
            if(is_ok_to_fetch_data)
            {
                m_main_view_model.refresh();
            }
            else
            {
                logging_ok_snackbar(getString(R.string.not_allowed_to_get_weather_data_unless_over_wifi));
            }
        }
    }
}

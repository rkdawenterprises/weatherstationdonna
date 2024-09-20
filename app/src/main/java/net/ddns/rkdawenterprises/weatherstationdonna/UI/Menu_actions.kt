/*
 * Copyright (c) 2024 RKDAW Enterprises and Ralph Williamson.
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

package net.ddns.rkdawenterprises.weatherstationdonna.UI

import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewModelScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY
import kotlinx.coroutines.launch
import net.ddns.rkdawenterprises.weatherstationdonna.R
import net.ddns.rkdawenterprises.weatherstationdonna.User_settings

@Suppress("unused")
private const val LOG_TAG = "Menu_actions";

fun logging_ok_snackbar(context: Context,
                        main_view_model: Main_view_model,
                        snackbar_host_state: SnackbarHostState,
                        title_message: String,
                        logging_message: String? = null)
{
    if(logging_message != null)
    {
        Log.d(LOG_TAG,
              "$title_message: $logging_message")
    }
    else
    {
        Log.d(LOG_TAG,
              title_message)
    }

    main_view_model.viewModelScope.launch {
        snackbar_host_state.showSnackbar(message = title_message,
                                         actionLabel = context.resources.getString(R.string.OK),
                                         duration = SnackbarDuration.Long)
    }
}

fun action_exit(context: Context,
                main_view_model: Main_view_model,
                snackbar_host_state: SnackbarHostState)
{
    val message =
        "${context.resources.getString(R.string.exiting)} ${context.resources.getString(R.string.app_name)}..."
    logging_ok_snackbar(context = context,
                        main_view_model = main_view_model,
                        snackbar_host_state = snackbar_host_state,
                        title_message = message)

    Handler(Looper.getMainLooper()).postDelayed({
                                                    (context as ComponentActivity).moveTaskToBack(true);
                                                    context.finish();
                                                },
                                                1500)
}

fun action_set_dark_mode_theme(context: Context,
                               main_view_model: Main_view_model,
                               modal_dialog_host_state: Modal_dialog_host_state,
                               window_insets_controller: WindowInsetsControllerCompat)
{
    val selections = context.resources.getStringArray(R.array.dark_mode_options)
    User_settings.load_dark_mode_selection(context = context,
                                           scope = main_view_model.viewModelScope)
    { dark_mode_selection ->
        modal_dialog_host_state.show_dialog(type = Modal_dialog_host_type.RADIO,
                                            on_dismiss = {
                                                main_view_model.hide_toolbars_if_auto_hide_with_small_delay_workaround(context = context,
                                                                                                                       window_insets_controller = window_insets_controller)
                                            },
                                            on_confirm = {
                                                User_settings.store_update_dark_mode_selection(context = context,
                                                                                               scope = main_view_model.viewModelScope,
                                                                                               selection = modal_dialog_host_state.m_current_radio_selection)
                                                main_view_model.hide_toolbars_if_auto_hide_with_small_delay_workaround(context = context,
                                                                                                                       window_insets_controller = window_insets_controller)
                                            },
                                            text_confirm = context.resources.getString(R.string.OK),
                                            text_dismiss = context.resources.getString(R.string.cancel),
                                            title = context.resources.getString(R.string.select_dark_mode),
                                            radio_group = selections.toList(),
                                            radio_selected = dark_mode_selection)
    }
}

fun action_refresh_weather_data(context: Context,
                                main_view_model: Main_view_model,
                                snackbar_host_state: SnackbarHostState)
{
    User_settings.is_ok_to_fetch_data(context = context,
                                      main_view_model.viewModelScope) { is_ok_to_fetch_data ->
        if(is_ok_to_fetch_data)
        {
            main_view_model.refresh(context = context,
                                    snackbar_host_state = snackbar_host_state);
        }
        else
        {
            logging_ok_snackbar(context = context,
                                main_view_model = main_view_model,
                                snackbar_host_state = snackbar_host_state,
                                context.resources.getString(R.string.not_allowed_to_get_weather_data_unless_over_wifi))
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
fun action_set_forcast_location(context: Context,
                                main_view_model: Main_view_model,
                                snackbar_host_state: SnackbarHostState,
                                modal_dialog_host_state: Modal_dialog_host_state,
                                window_insets_controller: WindowInsetsControllerCompat,
                                location_text_field_value: MutableState<TextFieldValue>,
                                location_permissions_state: MultiplePermissionsState,
                                location_permission_dont_ask_again_state: MutableState<Boolean>)
{
    User_settings.load_forecast_location_setting(context = context,
                                                 scope = main_view_model.viewModelScope)
    { forecast_location_setting ->
        location_text_field_value.value = TextFieldValue(forecast_location_setting)
        val focus_requester = FocusRequester()
        modal_dialog_host_state.show_dialog(type = Modal_dialog_host_type.EDIT,
                                            on_dismiss = {
                                                main_view_model.hide_toolbars_if_auto_hide_with_small_delay_workaround(context = context,
                                                                                                                       window_insets_controller = window_insets_controller)
                                            },
                                            on_confirm = {
                                                User_settings.store_forecast_location_setting(context = context,
                                                                                              scope = main_view_model.viewModelScope,
                                                                                              location = location_text_field_value.value.text)
                                                main_view_model.hide_toolbars_if_auto_hide_with_small_delay_workaround(context = context,
                                                                                                                       window_insets_controller = window_insets_controller)
                                            },
                                            text_confirm = context.resources.getString(R.string.OK),
                                            text_dismiss = context.resources.getString(R.string.cancel),
                                            title = context.resources.getString(R.string.set_forecast_location),
                                            additional_buttons = listOf({
                                                                            OutlinedButton(onClick = {
                                                                                on_use_current_location(context = context,
                                                                                                        main_view_model = main_view_model,
                                                                                                        snackbar_host_state = snackbar_host_state,
                                                                                                        modal_dialog_host_state = modal_dialog_host_state,
                                                                                                        location_text_field_value = location_text_field_value,
                                                                                                        location_permissions_state = location_permissions_state,
                                                                                                        location_permission_dont_ask_again_state = location_permission_dont_ask_again_state)
                                                                            }) {
                                                                                Text(context.resources.getString(R.string.use_current))
                                                                            }
                                                                        },
                                                                        {
                                                                            OutlinedButton(onClick = {
                                                                                location_text_field_value.value =
                                                                                    TextFieldValue(context.resources.getString(R.string.forecast_location_setting_default))
                                                                            }) {
                                                                                Text(context.resources.getString(R.string.use_default))
                                                                            }
                                                                        }),
                                            edit_text = {
                                                OutlinedTextField(modifier = Modifier.focusRequester(focus_requester),
                                                                  value = location_text_field_value.value,
                                                                  placeholder = { Text(context.resources.getString(R.string.enter_forecast_location)) },
                                                                  onValueChange = {
                                                                      location_text_field_value.value = it
                                                                  },
                                                                  label = { Text(context.resources.getString(R.string.enter_forecast_location)) })
                                                LaunchedEffect(Unit) {
                                                    focus_requester.requestFocus()
                                                }
                                            })
    }
}

@OptIn(ExperimentalPermissionsApi::class)
fun on_use_current_location(context: Context,
                            main_view_model: Main_view_model,
                            snackbar_host_state: SnackbarHostState,
                            modal_dialog_host_state: Modal_dialog_host_state,
                            location_text_field_value: MutableState<TextFieldValue>,
                            location_permissions_state: MultiplePermissionsState,
                            location_permission_dont_ask_again_state: MutableState<Boolean>)
{
    if(location_permissions_state.allPermissionsGranted)
    {
        get_location_and_update(context = context,
                                modal_dialog_host_state = modal_dialog_host_state,
                                main_view_model = main_view_model,
                                snackbar_host_state = snackbar_host_state,
                                location_text = location_text_field_value)
        return
    }

    val all_permissions_revoked =
        (location_permissions_state.permissions.size == location_permissions_state.revokedPermissions.size)

    modal_dialog_host_state.m_is_modal_dialog_visible = false

    if(location_permission_dont_ask_again_state.value)
    {
        val message = if(!all_permissions_revoked)
        {
            "You've enabled COARSE location permission, but FINE location permission may give you a more accurate forcast. Please consider granting FINE location permission for ${context.resources.getString(R.string.app_name)}."
        }
        else if(location_permissions_state.shouldShowRationale)
        {
            "You clicked on \"${context.resources.getString(R.string.use_current)}\" button, but you've denied location permissions that are needed for this feature. Please Please click the \"${context.resources.getString(R.string.OK)}\" button then grant location permissions for ${context.resources.getString(R.string.app_name)}."
        }
        else
        {
            "The feature \"${context.resources.getString(R.string.use_current)}\" location needs you to grant location permissions. Please click the \"${context.resources.getString(R.string.OK)}\" button then grant location permissions for ${context.resources.getString(R.string.app_name)}."
        }

        modal_dialog_host_state.show_dialog(on_dismiss = {},
                                            on_confirm = { location_permissions_state.launchMultiplePermissionRequest() },
                                            text_confirm = context.resources.getString(R.string.OK),
                                            text_dismiss = context.resources.getString(R.string.cancel),
                                            title = context.resources.getString(R.string.location_permissions),
                                            checkbox = {
                                                Dropdown_menu_item_checkbox(text = { Text("Don't ask again") },
                                                                            checked_state = location_permission_dont_ask_again_state,
                                                                            on_checked = { checked ->
                                                                                location_permission_dont_ask_again_state.value = checked
                                                                                User_settings.store_location_permission_dont_ask_again(context = context,
                                                                                                                                       scope = main_view_model.viewModelScope,
                                                                                                                                       checked)
                                                                            },
                                                                            on_click = {
                                                                                location_permission_dont_ask_again_state.value = !location_permission_dont_ask_again_state.value
                                                                                User_settings.store_location_permission_dont_ask_again(context = context,
                                                                                                                                       scope = main_view_model.viewModelScope,
                                                                                                                                       location_permission_dont_ask_again_state.value)
                                                                            })
                                            },
                                            message = message)
    }
}

fun get_location_and_update(context: Context,
                            modal_dialog_host_state: Modal_dialog_host_state,
                            main_view_model: Main_view_model,
                            snackbar_host_state: SnackbarHostState,
                            location_text: MutableState<TextFieldValue>)
{
    Log.d(LOG_TAG,
          ">>> Getting location...")

    val access_coarse_location_permission = ActivityCompat.checkSelfPermission(context,
                                                                               android.Manifest.permission.ACCESS_COARSE_LOCATION)
    val is_access_coarse_location_permission = access_coarse_location_permission == PackageManager.PERMISSION_GRANTED
    val access_fine_location_permission = ActivityCompat.checkSelfPermission(context,
                                                                             android.Manifest.permission.ACCESS_FINE_LOCATION)
    val is_access_fine_location_permission = access_fine_location_permission == PackageManager.PERMISSION_GRANTED

    Log.d(LOG_TAG,
          ">>> course: $is_access_coarse_location_permission, fine: $is_access_fine_location_permission")

    if(is_access_coarse_location_permission || is_access_fine_location_permission)
    {
        location_text.value = TextFieldValue(context.resources.getString(R.string.getting_location))

        LocationServices.getFusedLocationProviderClient(context)
                .getCurrentLocation(PRIORITY_BALANCED_POWER_ACCURACY,
                                    null)
                .addOnSuccessListener {
                    Log.d(LOG_TAG,
                          ">>> Got location result")
                    if(it == null)
                    {
                        modal_dialog_host_state.m_is_modal_dialog_visible = false
                        logging_ok_snackbar(context = context,
                                            main_view_model = main_view_model,
                                            snackbar_host_state = snackbar_host_state,
                                            title_message = context.resources.getString(R.string.system_did_not_give_a_location))
                    }
                    else
                    {
                        val latitude = it.latitude;
                        val longitude = it.longitude;
                        Log.d(LOG_TAG,
                              "$latitude,$longitude");
                        location_text.value = TextFieldValue(context.resources.getString(R.string.location_template,
                                                                                                    latitude,
                                                                                                    longitude))
                        User_settings.store_forecast_location_setting(context = context,
                                                                      scope = main_view_model.viewModelScope,
                                                                      location = location_text.value.text)
                    }
                }
        Log.d(LOG_TAG,
              ">>> Requested location")
    }
    else
    {
        modal_dialog_host_state.m_is_modal_dialog_visible = false
        logging_ok_snackbar(context = context,
                            main_view_model = main_view_model,
                            snackbar_host_state = snackbar_host_state,
                            title_message = "Location permissions not granted for this app")
    }
}

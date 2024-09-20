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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.Typography

@Suppress("unused")
private const val LOG_TAG = "Modal_dialog_host_composable";

enum class Modal_dialog_host_type
{
    SIMPLE,
    RADIO,
    EDIT
}

data class Modal_dialog_host_data(val type: Modal_dialog_host_type = Modal_dialog_host_type.SIMPLE,
                                  val on_dismiss: () -> Unit = {},
                                  val on_confirm: () -> Unit = {},
                                  val text_confirm: String = "OK",
                                  val text_dismiss: String? = null,
                                  val icon: ImageVector? = null,
                                  val icon_description: String? = null,
                                  val title: String? = null,
                                  val message: String? = null,
                                  val radio_group: List<String>? = null,
                                  val on_radio: ((index: Int, item: String) -> Unit)? = null,
                                  val additional_buttons: List<@Composable () -> Unit>? = null,
                                  val checkbox: @Composable (() -> Unit)? = null,
                                  val edit_text: @Composable (() -> Unit)? = null)

class Modal_dialog_host_state
{
    var m_current_modal_dialog_data by mutableStateOf<Modal_dialog_host_data?>(null)
        private set

    var m_is_modal_dialog_visible by mutableStateOf(false)

    var m_current_radio_selection by mutableIntStateOf(-1)

    fun show_dialog(type: Modal_dialog_host_type = Modal_dialog_host_type.SIMPLE,
                    on_dismiss: () -> Unit,
                    on_confirm: () -> Unit,
                    text_confirm: String,
                    text_dismiss: String? = null,
                    icon: ImageVector? = null,
                    icon_description: String? = null,
                    title: String? = null,
                    message: String? = null,
                    radio_group: List<String>? = null,
                    radio_selected: Int? = null,
                    on_radio: ((index: Int, item: String) -> Unit)? = null,
                    additional_buttons: List<@Composable () -> Unit>? = null,
                    checkbox: @Composable (() -> Unit)? = null,
                    edit_text: @Composable (() -> Unit)? = null)
    {
        if(!m_is_modal_dialog_visible)
        {
            if((title == null) && (message == null) && (icon == null))
            {
                throw Exception("Dialog must have sufficient messaging")
            }

            if((radio_selected != null) && !radio_group.isNullOrEmpty())
            {
                m_current_radio_selection = radio_selected
            }

            m_current_modal_dialog_data = Modal_dialog_host_data(type = type,
                                                                 on_dismiss = on_dismiss,
                                                                 on_confirm = on_confirm,
                                                                 text_confirm = text_confirm,
                                                                 text_dismiss = text_dismiss,
                                                                 icon = icon,
                                                                 icon_description = icon_description,
                                                                 title = title,
                                                                 message = message,
                                                                 radio_group = radio_group,
                                                                 on_radio = on_radio,
                                                                 additional_buttons = additional_buttons,
                                                                 checkbox = checkbox,
                                                                 edit_text = edit_text)
            m_is_modal_dialog_visible = true
        }
    }
}

@Composable
fun Modal_dialog_host(host_state: Modal_dialog_host_state)
{
    val current_data = host_state.m_current_modal_dialog_data
        ?: return

    val on_dismiss: () -> Unit = {
        current_data.on_dismiss.invoke()
        host_state.m_is_modal_dialog_visible = false
    }

    val confirm_button: @Composable () -> Unit = {
        TextButton(onClick = {
            current_data.on_confirm.invoke()
            host_state.m_is_modal_dialog_visible = false
        }) {
            Text(current_data.text_confirm)
        }
    }

    val dismiss_button: @Composable (() -> Unit)? = if(current_data.text_dismiss != null)
    {
        {
            TextButton(onClick = { on_dismiss.invoke() }) {
                Text(current_data.text_dismiss)
            }
        }
    }
    else
    {
        null
    }

    val icon: @Composable (() -> Unit)? = if(current_data.icon != null)
    {
        {
            Icon(current_data.icon,
                 contentDescription = current_data.icon_description)
        }
    }
    else
    {
        null
    }

    val title: @Composable (() -> Unit)? = if(current_data.title != null)
    {
        { Text(text = current_data.title) }
    }
    else
    {
        null
    }

    val message: @Composable (() -> Unit)? =
        when(current_data.type)
        {
            Modal_dialog_host_type.SIMPLE ->
            {
                {
                    Column(modifier = Modifier.fillMaxWidth(),
                           horizontalAlignment = Alignment.CenterHorizontally,
                           verticalArrangement = Arrangement.spacedBy(10.dp))
                    {
                        if(current_data.message != null)
                        {
                            Text(text = current_data.message)
                        }

                        if(current_data.checkbox != null)
                        {
                            current_data.checkbox.invoke()
                        }
                    }
                }
            }

            Modal_dialog_host_type.RADIO ->
            {
                if((host_state.m_current_radio_selection != -1) && !current_data.radio_group.isNullOrEmpty())
                {
                    val style = Typography.bodyLarge
                    val on_click: (index: Int, item: String) -> Unit = { index, item ->
                        host_state.m_current_radio_selection = index
                        if(current_data.on_radio != null)
                        {
                            current_data.on_radio.invoke(index,
                                                         item)
                        }
                    }

                    {
                        Column(modifier = Modifier.fillMaxWidth(),
                               horizontalAlignment = Alignment.Start,
                               verticalArrangement = Arrangement.Center)
                        {
                            current_data.radio_group.forEachIndexed { index, item ->
                                Row(modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(selected = (host_state.m_current_radio_selection == index),
                                                onClick = { on_click.invoke(index, item) },
                                                enabled = true)
                                    Text(modifier = Modifier.padding(start = 10.dp)
                                            .clickable { on_click.invoke(index, item) },
                                         style = style,
                                         text = AnnotatedString(item))
                                }
                            }
                        }
                    }
                }
                else
                {
                    null
                }
            }

            Modal_dialog_host_type.EDIT ->
            {
                {
                    Column(modifier = Modifier.fillMaxWidth(),
                           horizontalAlignment = Alignment.CenterHorizontally,
                           verticalArrangement = Arrangement.spacedBy(10.dp))
                    {
                        if(current_data.edit_text != null)
                        {
                            current_data.edit_text.invoke()
                        }

                        if(!current_data.additional_buttons.isNullOrEmpty())
                        {
                            Row(modifier = Modifier.fillMaxWidth()
                                    .padding(start = 5.dp, end = 5.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically) {
                                current_data.additional_buttons.forEach { item ->
                                    item.invoke()
                                }
                            }
                        }

                        if(current_data.checkbox != null)
                        {
                            current_data.checkbox.invoke()
                        }
                    }
                }
            }
        }

    AlertDialog(onDismissRequest = on_dismiss,
                confirmButton = confirm_button,
                dismissButton = dismiss_button,
                icon = icon,
                title = title,
                text = message)
}


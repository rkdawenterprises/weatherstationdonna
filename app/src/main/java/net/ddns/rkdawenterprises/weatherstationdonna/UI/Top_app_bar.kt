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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.ddns.rkdawenterprises.weatherstationdonna.R

@Suppress("unused")
private const val LOG_TAG = "Top_app_bar_composable";

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Top_app_bar(scroll_behavior: TopAppBarScrollBehavior,
                is_menu_expanded: MutableState<Boolean>,
                action_menu_items: List<Action_menu_item>,
                action_bar_items: List<Action_menu_item>,
                on_arrow_back: () -> Unit,
                on_menu_expanded: (() -> Unit)? = null,
                on_menu_dismissed: (() -> Unit)? = null,
                on_action_taken: (() -> Unit)? = null)
{
    CenterAlignedTopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) },
                           colors = topAppBarColors(containerColor = MaterialTheme.colorScheme.primary,
                                                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                                                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                                                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary),
                           navigationIcon = {
                               IconButton(onClick = {
                                   is_menu_expanded.value = false
                                   on_arrow_back.invoke()
                                   if(on_action_taken != null) on_action_taken()
                               }) {
                                   Icon(imageVector = ImageVector.vectorResource(id = R.drawable.outline_arrow_back_32),
                                        contentDescription = stringResource(R.string.action_exit));
                               }
                           },
                           actions = {
                               Top_bar_actions(action_bar_items = action_bar_items,
                                               action_menu_items = action_menu_items,
                                               is_menu_expanded = is_menu_expanded,
                                               on_menu_expanded = on_menu_expanded,
                                               on_menu_dismissed = on_menu_dismissed,
                                               on_action_taken = on_action_taken);
                           },
                           scrollBehavior = scroll_behavior)
}

data class Action_menu_item(val title: Int,
                            val title_condensed: Int,
                            val description: Int,
                            val icon: Int,
                            val on_clicked: (() -> Unit)? = null,
                            val on_checked: ((checked: Boolean) -> Unit)? = null,
                            val checked_state: MutableState<Boolean>? = null)

@Composable
fun Top_bar_actions(action_bar_items: List<Action_menu_item>,
                    action_menu_items: List<Action_menu_item>,
                    is_menu_expanded: MutableState<Boolean>,
                    on_menu_expanded: (() -> Unit)? = null,
                    on_menu_dismissed: (() -> Unit)? = null,
                    on_action_taken: (() -> Unit)? = null)
{
    for(item in action_bar_items)
    {
        IconButton(onClick = {
            is_menu_expanded.value = false
            if(item.on_clicked != null) item.on_clicked.invoke()
            if(on_action_taken != null) on_action_taken()
        }) {
            Icon(imageVector = ImageVector.vectorResource(id = item.icon),
                 contentDescription = stringResource(item.description))
        }
    }

    IconButton(onClick = {
        is_menu_expanded.value = true
            if(on_menu_expanded != null) on_menu_expanded()
    })
    {
        Icon(imageVector = ImageVector.vectorResource(id = R.drawable.outline_menu_32),
             contentDescription = stringResource(R.string.action_dropdown_menu))
    }

    DropdownMenu(expanded = is_menu_expanded.value,
                 onDismissRequest = {
                     is_menu_expanded.value = false
                     if(on_menu_dismissed != null) on_menu_dismissed()
                 })
    {
        for(item in action_menu_items)
        {
            // TODO: For large screens, use non-condensed title
            if((item.on_checked != null) && (item.checked_state != null))
            {
                Dropdown_menu_item_checkbox(text = { Text(stringResource(id = item.title_condensed)) },
                                            on_click = {
                                                is_menu_expanded.value = false
                                                if(item.on_clicked != null) item.on_clicked.invoke()
                                                if(on_action_taken != null) on_action_taken()
                                            },
                                            checked_state = item.checked_state,
                                            on_checked = {
                                                is_menu_expanded.value = false
                                                item.on_checked.invoke(it)
                                                if(on_action_taken != null) on_action_taken()
                                            },
                                            leading_icon = {
                                                Icon(imageVector = ImageVector.vectorResource(id = item.icon),
                                                     contentDescription = stringResource(item.description))
                                            })
            }
            else
            {
                DropdownMenuItem(text = { Text(stringResource(id = item.title_condensed)) },
                                 onClick = {
                                     is_menu_expanded.value = false
                                     if(item.on_clicked != null) item.on_clicked.invoke()
                                     if(on_action_taken != null) on_action_taken()
                                 },
                                 leadingIcon = {
                                     Icon(imageVector = ImageVector.vectorResource(id = item.icon),
                                          contentDescription = stringResource(item.description))
                                 })
            }
        }
    }
}

val Dropdown_menu_item_text_style = TextStyle.Default.copy(platformStyle = PlatformTextStyle(includeFontPadding = false),
                                              lineHeightStyle = LineHeightStyle(alignment = LineHeightStyle.Alignment.Center,
                                                                                trim = LineHeightStyle.Trim.None),
                                              fontFamily = FontFamily.SansSerif,
                                              fontWeight = FontWeight.Medium,
                                              fontSize = 14.sp,
                                              lineHeight = 20.sp,
                                              letterSpacing = 0.1.sp)

@Composable
private fun MenuItemColors.leading_icon_color(enabled: Boolean): Color =
    if (enabled) leadingIconColor else disabledLeadingIconColor

@Composable
private fun MenuItemColors.text_color(enabled: Boolean): State<Color>
{
    return rememberUpdatedState(if (enabled) textColor else disabledTextColor)
}

@Composable
private fun MenuItemColors.trailing_icon_color(enabled: Boolean): Color =
    if (enabled) trailingIconColor else disabledTrailingIconColor

@Composable
fun Dropdown_menu_item_checkbox(modifier: Modifier = Modifier,
                                text: @Composable () -> Unit,
                                on_click: () -> Unit,
                                checked_state: MutableState<Boolean>,
                                on_checked: (checked: Boolean) -> Unit,
                                leading_icon: @Composable (() -> Unit)? = null,
                                enabled: Boolean = true,
                                colors: MenuItemColors = MenuDefaults.itemColors(),
                                content_padding: PaddingValues = MenuDefaults.DropdownMenuItemContentPadding,
                                interaction_source: MutableInteractionSource = remember { MutableInteractionSource() })
{
    Row(modifier = modifier
            .clickable(enabled = enabled,
                       onClick = on_click,
                       interactionSource = interaction_source,
                       indication = ripple())
            .fillMaxWidth()
            .sizeIn(minWidth = 112.dp,
                    maxWidth = 280.dp,
                    minHeight = 48.0.dp)
            .padding(content_padding),
        verticalAlignment = Alignment.CenterVertically) {
        ProvideTextStyle(Dropdown_menu_item_text_style) {
            if(leading_icon != null)
            {
                CompositionLocalProvider(LocalContentColor provides colors.leading_icon_color(enabled)) {
                    Box(Modifier.defaultMinSize(minWidth = 24.dp)) {
                        leading_icon()
                    }
                }
            }

            CompositionLocalProvider(LocalContentColor provides colors.text_color(enabled).value) {
                Box(Modifier.weight(1f)
                            .padding(start =
                                        if(leading_icon != null)
                                        {
                                            12.dp
                                        }
                                        else
                                        {
                                            0.dp
                                        },
                                     end = 12.dp)) {
                    text()
                }
            }

            CompositionLocalProvider(LocalContentColor provides colors.trailing_icon_color(enabled)) {
                Box(Modifier.defaultMinSize(minWidth = 24.dp)) {
                    Checkbox(checked = checked_state.value,
                             onCheckedChange = {
                                 checked_state.value = it
                                 on_checked.invoke(it)
                             })
                }
            }
        }
    }
}

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
               "PackageName",
               "unused")

package net.ddns.rkdawenterprises.weatherstationdonna.UI.theme

import android.content.Context
import android.util.TypedValue
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import net.ddns.rkdawenterprises.weatherstationdonna.R

private const val LOG_TAG = "Theme";

fun get_color(context: Context, color_attribute: Int): Color
{
    val typed_value = TypedValue();
    context.theme.resolveAttribute(color_attribute, typed_value, true);
    val color = ContextCompat.getColor(context, typed_value.resourceId);
    return Color(color);
}

data class Material_colors_extended(var material: Colors,
                                    var warning: Color,
                                    var on_warning: Color,
                                    var title_bar_text: Color,
                                    var title_bar_background: Color,
                                    var view_divider: Color,
                                    var icon_tint: Color,
                                    var text_default: Color)
{
    val primary: Color get() = material.primary
    val primaryVariant: Color get() = material.primaryVariant
    val secondary: Color get() = material.secondary
    val secondaryVariant: Color get() = material.secondaryVariant
    val background: Color get() = material.background
    val surface: Color get() = material.surface
    val error: Color get() = material.error
    val onPrimary: Color get() = material.onPrimary
    val onSecondary: Color get() = material.onSecondary
    val onBackground: Color get() = material.onBackground
    val onSurface: Color get() = material.onSurface
    val onError: Color get() = material.onError
    val isLight: Boolean get() = material.isLight
}

val Light_color_palette = Material_colors_extended(
    material = lightColors(),
    warning = Color.White,
    on_warning = Color.Black,
    title_bar_text = Color.Black,
    title_bar_background = Color.White,
    view_divider = Color.Black,
    icon_tint = Color.Black,
    text_default = Color.Black
)

val Dark_color_palette = Material_colors_extended(
    material = darkColors(),
    warning = Color.Black,
    on_warning = Color.White,
    title_bar_text = Color.White,
    title_bar_background = Color.Black,
    view_divider = Color.White,
    icon_tint = Color.White,
    text_default = Color.White
)

val Local_providable_composition_local = staticCompositionLocalOf { Light_color_palette }

@Suppress("UnusedReceiverParameter")
val MaterialTheme.material_colors_extended: Material_colors_extended
    @Composable
    @ReadOnlyComposable
    get() = Local_providable_composition_local.current

@Composable
fun Main_theme(context: Context,
               is_dark_theme: Boolean,
               content: @Composable () -> Unit)
{
    /**
     * Update the default compose colors with the values in the XML theme.
     */
    val colors = if(is_dark_theme)
    {
        Dark_color_palette.material = darkColors(primary = get_color(context, R.attr.colorPrimary),
                                                 primaryVariant = get_color(context, R.attr.colorPrimaryVariant),
                                                 onPrimary = get_color(context, R.attr.colorOnPrimary),
                                                 secondary = get_color(context, R.attr.colorSecondary),
                                                 secondaryVariant = get_color(context, R.attr.colorSecondaryVariant),
                                                 onSecondary = get_color(context, R.attr.colorOnSecondary),
                                                 error = get_color(context, R.attr.colorOnError));
        Dark_color_palette.warning = Color(ContextCompat.getColor(context, R.color.amber_800));
        Dark_color_palette.on_warning = Color.White;
        Dark_color_palette.title_bar_text = Color.White;
        Dark_color_palette.title_bar_background = Color(ContextCompat.getColor(context, R.color.gray_300));
        Dark_color_palette.view_divider = Color(ContextCompat.getColor(context, R.color.blue_gray_500));
        Dark_color_palette.icon_tint = get_color(context, androidx.appcompat.R.attr.colorControlNormal);
        Dark_color_palette.text_default = get_color(context, android.R.attr.textColorPrimary);

        Dark_color_palette
    }
    else
    {
        Light_color_palette.material = lightColors(primary = get_color(context, R.attr.colorPrimary),
                                                   primaryVariant = get_color(context, R.attr.colorPrimaryVariant),
                                                   onPrimary = get_color(context, R.attr.colorOnPrimary),
                                                   secondary = get_color(context, R.attr.colorSecondary),
                                                   secondaryVariant = get_color(context, R.attr.colorSecondaryVariant),
                                                   onSecondary = get_color(context, R.attr.colorOnSecondary),
                                                   error = get_color(context, R.attr.colorOnError));
        Light_color_palette.warning = Color(ContextCompat.getColor(context, R.color.amber_500));
        Light_color_palette.on_warning = Color.Black;
        Light_color_palette.title_bar_text = Color.Black;
        Light_color_palette.title_bar_background = Color.White;
        Light_color_palette.view_divider = Color(ContextCompat.getColor(context, R.color.blue_gray_100));
        Light_color_palette.icon_tint = get_color(context, androidx.appcompat.R.attr.colorControlNormal);
        Light_color_palette.text_default = get_color(context, android.R.attr.textColorPrimary);

        Light_color_palette
    }

    CompositionLocalProvider(Local_providable_composition_local provides colors)
    {
        MaterialTheme(
            colors = colors.material,
            typography = Main_typography,
            shapes = Main_shapes,
            content = content,
        )
    }
}

/*
 * Copyright (c) 2023 RKDAW Enterprises and Ralph Williamson.
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
               "UnnecessaryVariable")

package net.ddns.rkdawenterprises.weatherstationdonna.UI

import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import net.ddns.rkdawenterprises.weatherstationdonna.BuildConfig
import net.ddns.rkdawenterprises.weatherstationdonna.R

@Suppress("unused")
private const val LOG_TAG = "About_composable";

@Composable
fun About_dialog(on_ok: () -> Unit,
                 on_cancel: () -> Unit)
{
    val version_name = BuildConfig.VERSION_NAME;

    AlertDialog(onDismissRequest = on_cancel,
                confirmButton =
                {
                    TextButton(onClick = on_ok)
                    { Text(text =  stringResource(id = R.string.ok)) }
                },
                title = { Text(text = "${stringResource(id = R.string.app_name)} v$version_name") },
                text =
                {
                    Column()
                    {
                        Text(text = stringResource(R.string.displays_weather_data_from_donna_s_davis_vantage_vue_station));
                        Spacer(modifier = Modifier.height(10.dp));
                        Text(text = stringResource(R.string.copyright_2019_2023_rkdaw_enterprises_and_ralph_williamson));
                        Spacer(modifier = Modifier.height(10.dp));
                        Text_view_from_HTML(R.string.about_app_information_link);
                    }
                })
}

@Composable
fun Text_view_from_HTML(@StringRes string_ID: Int)
{
    val string_HTML = LocalContext.current.resources.getText(string_ID).toString();
    val text_spanned: Spanned = Html.fromHtml(string_HTML,
                          Html.FROM_HTML_MODE_LEGACY);

    AndroidView(factory =
                { context ->
                    val view = android.widget.TextView(context).apply()
                    {
                        text = text_spanned;
                    }
                    view.movementMethod = LinkMovementMethod.getInstance();
                    return@AndroidView view;
                });
}

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
               "UnnecessaryVariable")

package net.ddns.rkdawenterprises.weatherstationdonna.UI

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.ddns.rkdawenterprises.weatherstationdonna.Main_activity
import net.ddns.rkdawenterprises.weatherstationdonna.R
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.Main_theme
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.Main_typography
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.material_colors_extended

@Suppress("unused")
private const val LOG_TAG = "Main_composable";

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Main(main_activity: Main_activity,
         main_view_model: Main_view_model)
{
    val is_night_mode = main_view_model.is_application_in_night_mode(main_activity).collectAsStateWithLifecycle(false,
                                                                                                                main_activity);
    val weather_data = main_view_model.combined_response.observeAsState();
    val is_refreshing: Boolean by main_view_model.is_refreshing.collectAsStateWithLifecycle(false,
                                                                                            main_activity);
    val pull_refresh_state = rememberPullRefreshState(is_refreshing,
                                                      { main_view_model.refresh(main_activity) })
    val is_show_about_dialog: Boolean by main_view_model.is_show_about_dialog.collectAsStateWithLifecycle(false,
                                                                                                          main_activity);
    Main_theme(main_activity,
               is_night_mode.value)
    {
        Surface(modifier = Modifier)
        {
            val data_storage: Main_view_model.Data_storage? = weather_data.value;
            if(data_storage != null)
            {
                Box(modifier = Modifier.padding(5.dp).pullRefresh(pull_refresh_state).clickable { main_activity.toggle() })
                {
                    val weather_data_RKDAWE = data_storage.m_data_RKDAWE;
                    val weather_data_davis = data_storage.m_data_davis;
                    val weather_page = data_storage.m_page_davis;

                    if(is_show_about_dialog)
                    {
                        About_dialog(on_ok = main_view_model::about_dialog_ok,
                                     on_cancel = main_view_model::about_dialog_cancel);
                    }
                    
                    if((weather_data_RKDAWE != null) || (weather_data_davis != null))
                    {
                        LazyColumn(modifier = Modifier.fillMaxSize(),
                                   verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            item()
                            {
                                Spacer(modifier = Modifier.height(20.dp));
                            }

                            item()
                            {
                                Header(weather_data_RKDAWE,
                                       weather_data_davis,
                                       weather_page);
                            }

                            item()
                            {
                                Temperatures(weather_data_RKDAWE,
                                             weather_data_davis);
                            }

                            item()
                            {
                                Divider(color = MaterialTheme.material_colors_extended.view_divider,
                                        modifier = Modifier.fillMaxWidth().height(4.dp))
                            }

                            item()
                            {
                                Conditions(weather_data_RKDAWE,
                                           weather_data_davis);
                            }

                            item()
                            {
                                All_data(weather_data_RKDAWE);
                            }
                        }
                    }
                    else
                    {
                        Text(text = stringResource(id = R.string.could_not_download_server_data),
                             style = Main_typography.h1);
                    }

                    PullRefreshIndicator(is_refreshing,
                                         pull_refresh_state,
                                         Modifier.align(Alignment.TopCenter));
                }
            }
        }
    }
}

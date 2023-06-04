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
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.ddns.rkdawenterprises.davis_website.Weather_page
import net.ddns.rkdawenterprises.rkdawe_api_common.Weather_data
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
    val is_refreshing by main_view_model.is_refreshing.collectAsStateWithLifecycle();
    val pull_refresh_state = rememberPullRefreshState(is_refreshing,
                                                      { main_view_model.refresh(main_activity) })

    Main_theme(main_activity,
               is_night_mode.value) {
        val data_storage: Main_view_model.Data_storage? = weather_data.value;
        if(data_storage != null)
        {
            Box(modifier = Modifier
                .padding(5.dp)
                .pullRefresh(pull_refresh_state)
                .clickable { main_activity.toggle() })
            {
                val weather_data_RKDAWE = data_storage.m_data_RKDAWE;
                val weather_data_davis = data_storage.m_data_davis;
                val weather_page = data_storage.m_page_davis;

                if((weather_data_RKDAWE != null) || (weather_data_davis != null))
                {
                    LazyColumn(modifier = Modifier.fillMaxSize(),
                               verticalArrangement = Arrangement.spacedBy(10.dp))
                    {
                        item()
                        {
                            Spacer(modifier = Modifier.height(20.dp));
                        }

                        item()
                        {
                            Header(weather_data_RKDAWE,
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
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp))
                        }

                        item()
                        {
                            Conditions(weather_data_RKDAWE,
                                       weather_data_davis);
                        }

                        item() {
                            Text(text = "${weather_data.value?.m_data_RKDAWE?.serialize_to_JSON()}");
                        }

                        item() {
                            Text(text = "${weather_data.value?.m_data_davis?.serialize_to_JSON()}");
                        }

                        item() {
                            Text(text = "${weather_data.value?.m_page_davis?.serialize_to_JSON()}");
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

@Composable
fun Header(weather_data_RKDAWE: Weather_data?,
           weather_page: Weather_page?)
{
    val system_name = if(weather_data_RKDAWE != null)
    {
        weather_data_RKDAWE.system_name;
    }
    else if(weather_page != null)
    {
        weather_page.systemName;
    }
    else
    {
        stringResource(id = R.string.system_name_default);
    }

    TextField(value = system_name,
              modifier = Modifier.fillMaxWidth(),
              colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.material_colors_extended.primaryVariant,
                                                         disabledTextColor = MaterialTheme.material_colors_extended.onPrimary),
              shape = RectangleShape,
              singleLine = true,
              onValueChange = {},
              enabled = false,
              textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center,
                                                      fontFamily = Main_typography.h6.fontFamily,
                                                      fontWeight = Main_typography.h6.fontWeight,
                                                      fontSize = Main_typography.h6.fontSize))
}

//        m_binding.conditionsAsOf.text = resources.getString(R.string.conditions_as_of_format,
//                                                            convert_time_UTC_to_local(weather_data.time,
//                                                                                      "h:mm a EEEE, MMM d, yyyy"));

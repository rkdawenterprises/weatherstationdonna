@file:Suppress("ClassName",
               "FunctionName",
               "RedundantSemicolon",
               "PrivatePropertyName",
               "LocalVariableName",
               "PropertyName")

package net.ddns.rkdawenterprises.weatherstationdonna

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import net.ddns.rkdawenterprises.weatherstationdonna.ui.theme.WeatherStationDonnaTheme

class Main_activity: ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherStationDonnaTheme {
                Surface(modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background) {
                    Main_screen();
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Main_screen(modifier: Modifier = Modifier,
                weather_data_view_model: Weather_data_view_model = viewModel())
{
    val result = weather_data_view_model.combined_response.observeAsState();
    val is_refreshing by weather_data_view_model.is_refreshing.collectAsStateWithLifecycle();
    var manual_refresh by remember { mutableStateOf(1) }
    val coroutine_scope = rememberCoroutineScope();
    val scaffold_state = rememberScaffoldState();

    fun refresh() = coroutine_scope.launch {
        weather_data_view_model.refresh();
    }

    val pull_refresh_state = rememberPullRefreshState(is_refreshing, ::refresh)

    LaunchedEffect(key1 = manual_refresh) {
        weather_data_view_model.refresh();
    }

    Scaffold(
        modifier = Modifier,
        scaffoldState = scaffold_state) { contentPadding->
        Box(Modifier.pullRefresh(pull_refresh_state)
                    .padding(contentPadding)) {
            val triple = result.value;
            if(triple != null)
            {
                Column() {
                    Button(
                        onClick = {
                            coroutine_scope.launch {
                                val snackbar_result = scaffold_state.snackbarHostState.showSnackbar(message = "This is your message",
                                                                                                    actionLabel = "Do something")
                                when(snackbar_result)
                                {
                                    SnackbarResult.Dismissed       -> Log.d("SnackbarDemo", "Dismissed")
                                    SnackbarResult.ActionPerformed -> Log.d("SnackbarDemo", "Snackbar's button clicked")
                                }
                            }
                        }
                    ) {
                        Text("Press me")
                    }

                    IconButton(onClick = {
                        manual_refresh++
                    }) {
                        Icon(Icons.Outlined.Refresh, "Refresh")
                    }
                    val first = triple.first;
                    val second = triple.second;
                    val third = triple.third;
                    if((first != null) && (second != null) && (third != null))
                    {
                        LazyColumn {
                            items(1) {
                                Text(text = "${first[0]}, ${second[0]}, ${third[0]}");
                            }
                        }
                    }
                    else
                    {

                    }
                }
            }

            PullRefreshIndicator(
                refreshing = is_refreshing,
                state = pull_refresh_state,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun default_preview()
{
    WeatherStationDonnaTheme {
        Surface(modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background) {
            Main_screen();
        }
    }
}
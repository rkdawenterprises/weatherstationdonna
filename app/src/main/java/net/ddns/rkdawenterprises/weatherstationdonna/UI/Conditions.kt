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
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import net.ddns.rkdawenterprises.davis_website.Weather_data.get_forecast_icon_uri_for_date
import net.ddns.rkdawenterprises.davis_website.Weather_page
import net.ddns.rkdawenterprises.rkdawe_api_common.Utilities.convert_time_UTC_to_local
import net.ddns.rkdawenterprises.rkdawe_api_common.Utilities.convert_timestamp_to_local
import net.ddns.rkdawenterprises.rkdawe_api_common.Weather_data
import net.ddns.rkdawenterprises.weatherstationdonna.Main_activity
import net.ddns.rkdawenterprises.weatherstationdonna.R
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.Main_theme
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.Main_typography
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.material_colors_extended
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.cos
import kotlin.math.sin

@Suppress("unused")
private const val LOG_TAG = "Conditions_composable";

@Composable
fun Conditions(weather_data_RKDAWE: Weather_data?,
               weather_data_davis: net.ddns.rkdawenterprises.davis_website.Weather_data?)
{
    val spaced_by = 5.dp;
    val column_weights = floatArrayOf(1.9f, 1.1f, 3f);
    val icon_size = arrayOf(45.dp,50.dp);

    Column(modifier = Modifier.fillMaxSize(),
           verticalArrangement = Arrangement.spacedBy(10.dp))
    {
        Humidity(weather_data_RKDAWE,
                 weather_data_davis,
                 spaced_by,
                 column_weights,
                 icon_size);

        Divider(color = MaterialTheme.material_colors_extended.view_divider,
                modifier = Modifier.fillMaxWidth().height(2.dp));

        Wind(weather_data_RKDAWE,
             weather_data_davis,
             spaced_by,
             column_weights,
             icon_size);

        Divider(color = MaterialTheme.material_colors_extended.view_divider,
                modifier = Modifier.fillMaxWidth().height(2.dp));

        Rain(weather_data_RKDAWE,
             weather_data_davis,
             spaced_by,
             column_weights,
             icon_size);
        
        Divider(color = MaterialTheme.material_colors_extended.view_divider,
                modifier = Modifier.fillMaxWidth().height(2.dp));

        Barometer(weather_data_RKDAWE,
                  weather_data_davis,
                  spaced_by,
                  column_weights,
                  icon_size);

        Divider(color = MaterialTheme.material_colors_extended.view_divider,
                modifier = Modifier.fillMaxWidth().height(2.dp));
    }
}

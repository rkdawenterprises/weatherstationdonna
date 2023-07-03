@file:Suppress("ClassName",
               "FunctionName",
               "RedundantSemicolon",
               "PrivatePropertyName",
               "LocalVariableName",
               "PropertyName",
               "PackageName",
               "UnnecessaryVariable")

package net.ddns.rkdawenterprises.weatherstationdonna.UI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Divider
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.ddns.rkdawenterprises.rkdawe_api_common.Weather_data
import net.ddns.rkdawenterprises.weatherstationdonna.R
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.Main_typography
import net.ddns.rkdawenterprises.weatherstationdonna.UI.theme.material_colors_extended

@Suppress("unused")
private const val LOG_TAG = "Data_table_composable";

@Composable
fun Data_table(weather_data_RKDAWE: Weather_data?)
{
    Column(modifier = Modifier,
           verticalArrangement = Arrangement.spacedBy(10.dp)) {
        BasicTextField(modifier = Modifier
            .background(MaterialTheme.material_colors_extended.view_divider,
                        RectangleShape)
            .fillMaxWidth(),
                       value = stringResource(id = R.string.all_data),
                       onValueChange = {},
                       singleLine = true,
                       enabled = false,
                       textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center,
                                                               fontFamily = Main_typography.h6.fontFamily,
                                                               fontWeight = Main_typography.h6.fontWeight,
                                                               fontSize = Main_typography.h6.fontSize))
        if(weather_data_RKDAWE != null)
        {
            Simple_table(columns = listOf(Data_column(weight = 0.7f),
                                          Data_column(weight = 0.3f)),
                         modifier = Modifier,
                         separator = { Divider() },
                         space_between = 5.dp,
                         rows = listOf(arrayOf(weather_data_RKDAWE.temp,
                                               "dummy2"),
                                       arrayOf("dummy3dummy3",
                                               "dummy4"),
                                       arrayOf("dummy5dummy5",
                                               "dummy6")))
        }
    }
}

@Composable
fun Simple_table(columns: List<Data_column>,
                 modifier: Modifier = Modifier,
                 separator: @Composable (row_index: Int) -> Unit = {},
                 space_between: Dp = 5.dp,
                 rows: List<Array<String>>)
{
    rows.forEachIndexed()
    {
        index, strings ->
            Table_row(columns,
                      strings,
                      modifier,
                      space_between)
            separator(index)
    }
}

@Composable
fun Table_row(columns: List<Data_column>,
              cells: Array<String>,
              modifier: Modifier = Modifier,
              space_between: Dp = 5.dp)
{
    if(columns.size != cells.size)
    {
        throw RuntimeException("Number of columns size mismatch."  )
    }

    Row(modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(space = space_between,
                                                     alignment = Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically)
    {
        columns.forEachIndexed()
        {
            index, data_column ->
                Text(cells[index],
                     modifier = Modifier
                         .weight(data_column.weight,
                                 fill = true),
                     style = data_column.style,
                     textAlign = data_column.alignment);
        }
    }
}

data class Data_column(val alignment: TextAlign = TextAlign.Start,
                       val weight: Float = 1f,
                       val style: TextStyle = Main_typography.subtitle1)

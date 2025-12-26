package co.adityarajput.alarmetrics.views.screens.alarms

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.adityarajput.alarmetrics.R
import co.adityarajput.alarmetrics.data.alarm.Alarm
import co.adityarajput.alarmetrics.enums.Range
import co.adityarajput.alarmetrics.viewmodels.AlarmsViewModel
import co.adityarajput.alarmetrics.viewmodels.Provider
import com.himanshoe.charty.bar.BarChart
import com.himanshoe.charty.bar.config.BarChartConfig
import com.himanshoe.charty.bar.model.BarData
import com.himanshoe.charty.common.LabelConfig
import com.himanshoe.charty.common.asSolidChartColor

@Composable
fun Chart(
    alarm: Alarm,
    viewModel: AlarmsViewModel = viewModel(factory = Provider.Factory),
) {
    var range by remember { mutableStateOf(Range.WEEK) }
    var offset by remember { mutableIntStateOf(0) }
    val counts = viewModel.getChartData(alarm, range, offset).collectAsState()

    Card(
        Modifier
            .fillMaxWidth()
            .height(250.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.outline),
    ) {
        Row(
            Modifier.padding(start = dimensionResource(R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            var expanded by remember { mutableStateOf(false) }

            Text(stringResource(R.string.range), color = MaterialTheme.colorScheme.surface)
            Box {
                TextButton({ expanded = true }) {
                    Text(
                        stringResource(range.displayName),
                        color = MaterialTheme.colorScheme.surface,
                        textDecoration = TextDecoration.Underline,
                    )
                }
                DropdownMenu(expanded, { expanded = false }) {
                    Range.entries.forEach {
                        DropdownMenuItem(
                            { Text(stringResource(it.displayName)) },
                            {
                                range = it
                                expanded = false
                            },
                        )
                    }
                }
            }
        }
        if (counts.value.state == null) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val barColor = MaterialTheme.colorScheme.surface.asSolidChartColor()
            val barBackgroundColor = Color.Transparent.asSolidChartColor()

            // TODO: Try charty v3
            BarChart(
                {
                    counts.value.state!!.map {
                        BarData(
                            it.toFloat(),
                            if (it == 0) " " else it,
                            barColor,
                            barBackgroundColor,
                        )
                    }
                },
                Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_medium)),
                barChartConfig = BarChartConfig.default().copy(
                    showGridLines = false,
                    showCurvedBar = true,
                    minimumBarCount = counts.value.state!!.size,
                    cornerRadius = CornerRadius(16f, 16f),
                ),
                labelConfig = LabelConfig.default().copy(
                    showXLabel = true,
                    xAxisCharCount = 3,
                    labelTextStyle = MaterialTheme.typography.labelLarge.copy(MaterialTheme.colorScheme.surface),
                ),
                onBarClick = { i, _ ->
                    // TODO: Navigate to that subrange with offset
                    Log.d("AlarmsScreen", "Clicked bar $i")
                },
            )
        }
    }
}

package co.adityarajput.alarmetrics.views.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import co.adityarajput.alarmetrics.utils.getStartOfRange
import co.adityarajput.alarmetrics.utils.minus
import co.adityarajput.alarmetrics.utils.toDate
import co.adityarajput.alarmetrics.viewmodels.AlarmsViewModel
import co.adityarajput.alarmetrics.viewmodels.Provider
import com.himanshoe.charty.bar.BarChart
import com.himanshoe.charty.bar.config.BarChartConfig
import com.himanshoe.charty.bar.config.BarTooltip
import com.himanshoe.charty.bar.model.BarData
import com.himanshoe.charty.common.LabelConfig
import com.himanshoe.charty.common.TargetConfig
import com.himanshoe.charty.common.asSolidChartColor

@Composable
fun Chart(
    alarm: Alarm,
    viewModel: AlarmsViewModel = viewModel(factory = Provider.Factory),
) {
    var rangeDropdownExpanded by remember { mutableStateOf(false) }
    var range by remember { mutableStateOf(Range.WEEK) }

    var dateDropdownExpanded by remember { mutableStateOf(false) }
    var dateDropdownOffset by remember(range) { mutableIntStateOf(0) }
    var startDate by remember(range) {
        mutableLongStateOf(
            System.currentTimeMillis().getStartOfRange(range),
        )
    }

    val chartData = viewModel.getChartData(alarm, range, startDate).collectAsState().value

    Card(
        Modifier
            .fillMaxWidth()
            .height(320.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.outline),
    ) {
        Spacer(Modifier.height(dimensionResource(R.dimen.padding_medium)))
        Row(
            Modifier.padding(start = dimensionResource(R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(stringResource(R.string.range), color = MaterialTheme.colorScheme.surface)
            Box {
                Text(
                    stringResource(range.displayName),
                    Modifier.clickable { rangeDropdownExpanded = true },
                    MaterialTheme.colorScheme.surface,
                    textDecoration = TextDecoration.Underline,
                )
                DropdownMenu(rangeDropdownExpanded, { rangeDropdownExpanded = false }) {
                    Range.entries.forEach {
                        DropdownMenuItem(
                            { Text(stringResource(it.displayName)) },
                            {
                                range = it
                                rangeDropdownExpanded = false
                            },
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(dimensionResource(R.dimen.padding_medium)))
        Row(
            Modifier.padding(start = dimensionResource(R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(stringResource(R.string.start_date), color = MaterialTheme.colorScheme.surface)
            Box {
                Text(
                    startDate.toDate().toString(),
                    Modifier.clickable { dateDropdownExpanded = true },
                    MaterialTheme.colorScheme.surface,
                    textDecoration = TextDecoration.Underline,
                )
                DropdownMenu(
                    dateDropdownExpanded,
                    { dateDropdownExpanded = false },
                    Modifier
                        .height(300.dp)
                        .width(180.dp),
                ) {
                    val nowWithOffset = System.currentTimeMillis().minus(dateDropdownOffset, range)
                    val options = List(20) { nowWithOffset.minus(it, range).getStartOfRange(range) }
                    var lastLoadedIndex by remember { mutableIntStateOf(-1) }

                    LazyColumn(
                        Modifier
                            .height(300.dp)
                            .width(180.dp),
                    ) {
                        itemsIndexed(options) { index, timestamp ->
                            DropdownMenuItem(
                                { Text(timestamp.toDate().toString()) },
                                {
                                    startDate = timestamp
                                    dateDropdownExpanded = false
                                },
                            )
                            if (index == options.lastIndex - 2 && lastLoadedIndex != index) {
                                LaunchedEffect(index) {
                                    lastLoadedIndex = index
                                    dateDropdownOffset += 10
                                }
                            }
                        }
                    }
                }
            }
        }
        HorizontalDivider(
            Modifier.padding(dimensionResource(R.dimen.padding_medium)),
            color = MaterialTheme.colorScheme.surface,
        )
        if (chartData.snoozeTimes == null || chartData.unit == null) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val barColor = MaterialTheme.colorScheme.surface.asSolidChartColor()
            val barBackgroundColor = Color.Transparent.asSolidChartColor()

            Box(
                Modifier.fillMaxWidth(),
                Alignment.TopCenter,
            ) {
                Text(
                    stringResource(R.string.chart_title, stringResource(chartData.unit)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.surface,
                )
                BarChart(
                    {
                        chartData.snoozeTimes.map {
                            BarData(
                                it.toFloat(),
                                " ",
                                barColor,
                                barBackgroundColor,
                            )
                        }
                    },
                    Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.padding_medium)),
                    chartData.parentAverage?.toFloat(),
                    TargetConfig.default().copy(targetLineBarColors = barColor),
                    BarChartConfig.default().copy(
                        showGridLines = false,
                        showCurvedBar = true,
                        minimumBarCount = chartData.snoozeTimes.size,
                        cornerRadius = CornerRadius(16f, 16f),
                    ),
                    LabelConfig.default().copy(
                        labelTextStyle = MaterialTheme.typography.labelLarge.copy(MaterialTheme.colorScheme.surface),
                    ),
                    BarTooltip.BarTop,
                )
            }
        }
    }
}

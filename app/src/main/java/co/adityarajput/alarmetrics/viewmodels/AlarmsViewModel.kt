package co.adityarajput.alarmetrics.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.adityarajput.alarmetrics.R
import co.adityarajput.alarmetrics.data.AlarmWithStats
import co.adityarajput.alarmetrics.data.Repository
import co.adityarajput.alarmetrics.data.alarm.Alarm
import co.adityarajput.alarmetrics.enums.DialogState
import co.adityarajput.alarmetrics.enums.Range
import co.adityarajput.alarmetrics.utils.Logger
import co.adityarajput.alarmetrics.utils.getEndOfRange
import co.adityarajput.alarmetrics.utils.getStartOfRange
import co.adityarajput.alarmetrics.utils.indexIn
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.round

data class AlarmsState(val state: List<AlarmWithStats>? = null)
data class ChartState(
    val snoozeTimes: List<Int>? = null,
    val unit: Int? = null,
    val parentAverage: Int? = null,
)

class AlarmsViewModel(private val repository: Repository) : ViewModel() {
    val alarms: StateFlow<AlarmsState> =
        repository.collectStats().map { AlarmsState(it) }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AlarmsState(),
        )

    var dialogState by mutableStateOf<DialogState?>(null)

    var selectedAlarm by mutableStateOf<Alarm?>(null)

    fun getChartData(alarm: Alarm, range: Range, from: Long): StateFlow<ChartState> {
        val flow = MutableStateFlow(ChartState())

        val to = from.getEndOfRange(range)
        val parentFrom = from.getStartOfRange(range.parent)
        val parentTo = from.getEndOfRange(range.parent)

        viewModelScope.launch {
            repository.records(alarm.id, from, to)
                .combine(repository.records(alarm.id, parentFrom, parentTo)) { s, t -> s to t }
                .collect { value ->
                    val records = value.first
                    val snoozeTimes = MutableList(to.indexIn(range) + 1) { 0L }
                    records.forEach { snoozeTimes[it.firstSnooze.indexIn(range)] += it.snoozeTime }
                    val max = snoozeTimes.max()

                    val parentRecords = value.second
                    val parentSnoozeTimes = MutableList(parentTo.indexIn(range.parent) + 1) { 0L }
                    parentRecords.forEach { parentSnoozeTimes[it.firstSnooze.indexIn(range.parent)] += it.snoozeTime }
                    val average = parentSnoozeTimes.sum() /
                            max(parentSnoozeTimes.count { it > 0L } * snoozeTimes.size, 1)

                    var unit = R.string.days
                    var divisor = 86_400_000f

                    if (max < 10 * 60_000L) {
                        divisor = 1000f
                        unit = R.string.seconds
                    } else if (max < 10 * 3_600_000L) {
                        divisor = 60_000f
                        unit = R.string.minutes
                    } else if (max < 10 * 86_400_000L) {
                        divisor = 3_600_000f
                        unit = R.string.hours
                    }

                    var target: Int? = round(average / divisor).toInt()
                    // INFO: Can't display target line if it's higher than max bar value
                    if (target == 0 || average > max || range == Range.DECADE) {
                        target = null
                    }

                    flow.value = ChartState(
                        snoozeTimes.map { round(it / divisor).toInt() },
                        unit, target,
                    )
                }
        }

        return flow
    }

    fun toggleAlarm() {
        viewModelScope.launch {
            if (selectedAlarm!!.isActive) {
                Logger.i("AlarmsViewModel", "Clearing records for $selectedAlarm")
                repository.deleteAllRecordsFor(selectedAlarm!!.id)
            }

            Logger.i("AlarmsViewModel", "Toggling tracking of $selectedAlarm")
            repository.toggleTracking(selectedAlarm!!)
        }
    }

    fun deleteAlarm() {
        viewModelScope.launch {
            Logger.i("AlarmsViewModel", "Deleting $selectedAlarm")
            repository.delete(selectedAlarm!!)
        }
    }
}

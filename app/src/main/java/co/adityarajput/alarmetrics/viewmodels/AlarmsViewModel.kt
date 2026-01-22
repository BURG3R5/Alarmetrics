package co.adityarajput.alarmetrics.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.adityarajput.alarmetrics.data.AlarmWithCount
import co.adityarajput.alarmetrics.data.Repository
import co.adityarajput.alarmetrics.data.alarm.Alarm
import co.adityarajput.alarmetrics.enums.DialogState
import co.adityarajput.alarmetrics.enums.Range
import co.adityarajput.alarmetrics.utils.getEndOfRange
import co.adityarajput.alarmetrics.utils.indexIn
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AlarmsState(val state: List<AlarmWithCount>? = null)
data class ChartState(val state: List<Int>? = null)

class AlarmsViewModel(private val repository: Repository) : ViewModel() {
    val alarms: StateFlow<AlarmsState> =
        repository.aggregateRecords().map { AlarmsState(it) }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AlarmsState(),
        )

    var dialogState by mutableStateOf<DialogState?>(null)

    var selectedAlarm by mutableStateOf<Alarm?>(null)

    fun getChartData(alarm: Alarm, range: Range, from: Long): StateFlow<ChartState> {
        val flow = MutableStateFlow(ChartState())

        val to = from.getEndOfRange(range)

        viewModelScope.launch {
            repository.records(alarm.id, from, to).collect { records ->
                val counts = MutableList(to.indexIn(range) + 1) { 0 }
                records.forEach { counts[it.firstSnooze.indexIn(range)] += it.snoozeCount }
                flow.value = ChartState(counts)
            }
        }

        return flow
    }

    fun toggleAlarm() {
        viewModelScope.launch {
            if (selectedAlarm!!.isActive) {
                Log.d("AlarmsViewModel", "Clearing records for $selectedAlarm")
                repository.deleteAllRecordsFor(selectedAlarm!!.id)
            }

            Log.d("AlarmsViewModel", "Toggling tracking of $selectedAlarm")
            repository.toggleTracking(selectedAlarm!!)
        }
    }

    fun deleteAlarm() {
        viewModelScope.launch {
            Log.d("AlarmsViewModel", "Deleting $selectedAlarm")
            repository.delete(selectedAlarm!!)
        }
    }
}

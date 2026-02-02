package co.adityarajput.alarmetrics.data

import androidx.room.Embedded
import co.adityarajput.alarmetrics.data.alarm.Alarm

data class AlarmWithStats(
    @Embedded
    val alarm: Alarm,
    private val totalSnoozeTime: Long,
    private val recordsCount: Int,
    val latestFirstSnooze: Long?,
) {
    val averageSnoozeTime: Int
        get() = if (recordsCount == 0) 0 else (totalSnoozeTime / recordsCount).toInt()
}

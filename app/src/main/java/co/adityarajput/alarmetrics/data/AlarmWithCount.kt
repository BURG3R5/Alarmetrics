package co.adityarajput.alarmetrics.data

import androidx.room.Embedded
import co.adityarajput.alarmetrics.data.alarm.Alarm

data class AlarmWithCount(
    @Embedded
    val alarm: Alarm,
    val count: Int,
)

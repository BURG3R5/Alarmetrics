package co.adityarajput.alarmetrics.data.record

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity("records", [Index("alarmId", "firstSnooze", unique = true)])
data class Record(
    val alarmId: Long,
    val firstSnooze: Long = System.currentTimeMillis(),
    val lastSnooze: Long = System.currentTimeMillis(),

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
) {
    val snoozeTime get() = lastSnooze - firstSnooze
}

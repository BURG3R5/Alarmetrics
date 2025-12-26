package co.adityarajput.alarmetrics.data.record

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    "records",
    [Index(
        "alarmId", "firstSnooze",
        unique = true,
    )],
)
data class Record(
    val alarmId: Long,
    val firstSnooze: Long = System.currentTimeMillis(),
    val snoozeCount: Int = 1,

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
)

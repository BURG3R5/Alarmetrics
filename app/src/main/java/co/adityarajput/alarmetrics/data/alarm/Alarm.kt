package co.adityarajput.alarmetrics.data.alarm

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.adityarajput.alarmetrics.enums.AlarmApp

@Entity("alarms")
data class Alarm(
    val title: String,
    val app: AlarmApp,
    val isActive: Boolean = true,

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
)

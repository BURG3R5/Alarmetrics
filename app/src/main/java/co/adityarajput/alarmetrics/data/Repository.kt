package co.adityarajput.alarmetrics.data

import co.adityarajput.alarmetrics.data.alarm.Alarm
import co.adityarajput.alarmetrics.data.alarm.AlarmDao
import co.adityarajput.alarmetrics.data.record.Record
import co.adityarajput.alarmetrics.data.record.RecordDao
import co.adityarajput.alarmetrics.utils.Logger
import kotlinx.coroutines.flow.first

class Repository(private val alarmDao: AlarmDao, private val recordDao: RecordDao) {
    suspend fun create(alarm: Alarm) = alarmDao.create(alarm)

    suspend fun create(record: Record) = recordDao.create(record)

    fun alarms() = alarmDao.list()

    suspend fun getLatestRecord(alarmId: Long) = recordDao.getLatest(alarmId)

    fun records(alarmId: Long, from: Long, to: Long) = recordDao.list(alarmId, from, to)

    fun collectStats() = recordDao.collectStats()

    suspend fun toggleTracking(alarm: Alarm) = alarmDao.toggleTracking(alarm.id)

    suspend fun updateRecord(id: Long, lastSnooze: Long) = recordDao.update(id, lastSnooze)

    suspend fun delete(alarm: Alarm) {
        alarmDao.delete(alarm)
        deleteAllRecordsFor(alarm.id)
    }

    suspend fun deleteAllRecordsFor(alarmId: Long) = recordDao.deleteAllRecordsFor(alarmId)

    suspend fun trimAlarms(isDebug: Boolean) {
        var cutoff = System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000L
        if (isDebug) cutoff = System.currentTimeMillis() - 5 * 60 * 1000L

        alarms().first()
            .filter { it.isActive && (getLatestRecord(it.id)?.lastSnooze ?: 0) < cutoff }
            .apply { if (isNotEmpty()) Logger.i("Repository", "Trimming alarms: $this") }
            .forEach { delete(it) }
    }
}

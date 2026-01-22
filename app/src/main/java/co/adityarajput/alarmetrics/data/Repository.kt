package co.adityarajput.alarmetrics.data

import co.adityarajput.alarmetrics.data.alarm.Alarm
import co.adityarajput.alarmetrics.data.alarm.AlarmDao
import co.adityarajput.alarmetrics.data.record.Record
import co.adityarajput.alarmetrics.data.record.RecordDao

class Repository(private val alarmDao: AlarmDao, private val recordDao: RecordDao) {
    suspend fun create(alarm: Alarm) = alarmDao.create(alarm)

    suspend fun create(record: Record) = recordDao.create(record)

    fun alarms() = alarmDao.list()

    suspend fun getLatestRecord(alarmId: Long) = recordDao.getLatest(alarmId)

    fun records(alarmId: Long, from: Long, to: Long) = recordDao.list(alarmId, from, to)

    fun aggregateRecords() = recordDao.aggregate()

    suspend fun toggleTracking(alarm: Alarm) = alarmDao.toggleTracking(alarm.id)

    suspend fun registerSnooze(id: Long) = recordDao.registerSnooze(id)

    suspend fun delete(alarm: Alarm) {
        alarmDao.delete(alarm)
        deleteAllRecordsFor(alarm.id)
    }

    suspend fun deleteAllRecordsFor(alarmId: Long) = recordDao.deleteAllRecordsFor(alarmId)
}

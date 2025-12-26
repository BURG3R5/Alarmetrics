package co.adityarajput.alarmetrics.data.alarm

class AlarmsRepository(private val alarmDao: AlarmDao) {
    suspend fun save(alarm: Alarm) = alarmDao.save(alarm)

    fun list() = alarmDao.list()

    suspend fun toggleTracking(alarm: Alarm) = alarmDao.toggleTracking(alarm.id)

    suspend fun delete(alarm: Alarm) = alarmDao.delete(alarm)
}

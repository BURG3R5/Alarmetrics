package co.adityarajput.alarmetrics.data.record

class RecordsRepository(private val recordDao: RecordDao) {
    suspend fun create(record: Record) = recordDao.create(record)

    suspend fun getLatest(alarmId: Long) = recordDao.getLatest(alarmId)

    fun aggregate() = recordDao.aggregate()

    fun list(alarmId: Long, from: Long, to: Long) = recordDao.list(alarmId, from, to)

    suspend fun registerSnooze(id: Long) = recordDao.registerSnooze(id)

    suspend fun deleteAllRecordsFor(alarmId: Long) = recordDao.deleteAllRecordsFor(alarmId)
}

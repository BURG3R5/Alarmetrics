package co.adityarajput.alarmetrics.data.record

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import co.adityarajput.alarmetrics.data.AlarmWithCount
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun create(record: Record): Long

    @Query("SELECT * FROM records WHERE alarmId = :alarmId ORDER BY firstSnooze DESC LIMIT 1")
    suspend fun getLatest(alarmId: Long): Record?

    @Query("SELECT a.*, IFNULL(SUM(r.snoozeCount), 0) as count FROM alarms a LEFT JOIN records r ON a.id = r.alarmId GROUP BY a.id")
    fun aggregate(): Flow<List<AlarmWithCount>>

    @Query("SELECT * FROM records WHERE alarmId = :alarmId AND firstSnooze BETWEEN :from AND :to ORDER BY firstSnooze ASC")
    fun list(alarmId: Long, from: Long, to: Long): Flow<List<Record>>

    @Query("UPDATE records SET snoozeCount = snoozeCount + 1 WHERE id = :id")
    suspend fun registerSnooze(id: Long)

    @Query("DELETE FROM records WHERE alarmId = :alarmId")
    suspend fun deleteAllRecordsFor(alarmId: Long)
}

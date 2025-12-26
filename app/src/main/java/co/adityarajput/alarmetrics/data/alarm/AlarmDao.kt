package co.adityarajput.alarmetrics.data.alarm

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun save(alarm: Alarm): Long

    @Query("SELECT * FROM alarms ORDER BY id DESC")
    fun list(): Flow<List<Alarm>>

    @Query("UPDATE alarms SET isActive = 1 - isActive WHERE id = :id")
    suspend fun toggleTracking(id: Long)

    @Delete
    suspend fun delete(alarm: Alarm)
}

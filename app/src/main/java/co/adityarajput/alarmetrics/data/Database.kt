package co.adityarajput.alarmetrics.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import co.adityarajput.alarmetrics.data.alarm.Alarm
import co.adityarajput.alarmetrics.data.alarm.AlarmDao
import co.adityarajput.alarmetrics.data.record.Record
import co.adityarajput.alarmetrics.data.record.RecordDao

@Database(
    entities = [Alarm::class, Record::class],
    version = 3,
    autoMigrations = [
        AutoMigration(1, 2, AlarmetricsDatabase.DeleteTableR::class),
        AutoMigration(2, 3),
    ],
)
abstract class AlarmetricsDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
    abstract fun recordDao(): RecordDao

    @DeleteTable("records")
    class DeleteTableR : AutoMigrationSpec

    companion object {
        @Volatile
        private var instance: AlarmetricsDatabase? = null

        fun getDatabase(context: Context): AlarmetricsDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    AlarmetricsDatabase::class.java,
                    "alarmetrics_database",
                ).build().also { instance = it }
            }
        }
    }
}

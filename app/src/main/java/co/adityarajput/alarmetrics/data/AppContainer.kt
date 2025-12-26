package co.adityarajput.alarmetrics.data

import android.content.Context
import co.adityarajput.alarmetrics.data.alarm.Alarm
import co.adityarajput.alarmetrics.data.alarm.AlarmsRepository
import co.adityarajput.alarmetrics.data.record.Record
import co.adityarajput.alarmetrics.data.record.RecordsRepository
import co.adityarajput.alarmetrics.enums.AlarmApp
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class AppContainer(private val context: Context) {
    val alarmsRepository: AlarmsRepository by lazy {
        AlarmsRepository(AlarmetricsDatabase.getDatabase(context).alarmDao())
    }
    val recordsRepository: RecordsRepository by lazy {
        RecordsRepository(AlarmetricsDatabase.getDatabase(context).recordDao())
    }

    fun seedDemoData() {
        runBlocking {
            val alarmId1 = alarmsRepository.save(Alarm("Wake up", AlarmApp.GOOGLE_CLOCK))
            val alarmId2 = alarmsRepository.save(Alarm("Exercise", AlarmApp.SAMSUNG_REMINDER))
            alarmsRepository.save(Alarm("Book club", AlarmApp.GOOGLE_CALENDAR))
            for (i in 0..30) {
                recordsRepository.create(
                    Record(
                        alarmId1,
                        System.currentTimeMillis() - i * 86400000,
                        Random.nextInt(2, 7),
                    ),
                )
                recordsRepository.create(
                    Record(
                        alarmId2,
                        System.currentTimeMillis() - i * 86400000,
                        Random.nextInt(2, 15),
                    ),
                )
            }

            alarmsRepository.save(Alarm("Sleep", AlarmApp.GOOGLE_CLOCK, false))
            alarmsRepository.save(Alarm("Catch up with Neil", AlarmApp.SAMSUNG_CALENDAR, false))
        }
    }
}

package co.adityarajput.alarmetrics.data

import android.content.Context
import co.adityarajput.alarmetrics.data.alarm.Alarm
import co.adityarajput.alarmetrics.data.record.Record
import co.adityarajput.alarmetrics.enums.AlarmApp
import co.adityarajput.alarmetrics.utils.toDate
import co.adityarajput.alarmetrics.utils.toMillis
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class AppContainer(private val context: Context) {
    val repository: Repository by lazy {
        Repository(
            AlarmetricsDatabase.getDatabase(context).alarmDao(),
            AlarmetricsDatabase.getDatabase(context).recordDao(),
        )
    }

    fun seedDemoData() {
        runBlocking {
            if (repository.alarms().first().isEmpty()) {
                val alarmId1 = repository.create(Alarm("Wake up", AlarmApp.GOOGLE_CLOCK))
                val alarmId2 = repository.create(Alarm("Exercise", AlarmApp.SAMSUNG_REMINDER))
                repository.create(Alarm("Book club", AlarmApp.GOOGLE_CALENDAR))
                repository.create(Alarm("Sleep", AlarmApp.GOOGLE_CLOCK, false))
                repository.create(Alarm("Catch up with Neil", AlarmApp.SAMSUNG_CALENDAR, false))

                for (i in 0..30) {
                    val nineAm = System.currentTimeMillis()
                        .toDate()
                        .atTime(9, 0)
                        .minusDays(i.toLong())
                        .toMillis()
                    val elevenAm = nineAm + 2 * 60 * 60 * 1000

                    repository.create(
                        Record(
                            alarmId1,
                            nineAm,
                            nineAm + Random.nextInt(2, 7) * 5 * 60 * 1000,
                        ),
                    )
                    repository.create(
                        Record(
                            alarmId2,
                            elevenAm,
                            elevenAm + Random.nextInt(2, 15) * 15 * 60 * 1000,
                        ),
                    )
                }
            }
        }
    }
}

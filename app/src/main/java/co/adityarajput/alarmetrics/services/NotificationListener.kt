package co.adityarajput.alarmetrics.services

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import co.adityarajput.alarmetrics.data.AppContainer
import co.adityarajput.alarmetrics.data.alarm.Alarm
import co.adityarajput.alarmetrics.data.record.Record
import co.adityarajput.alarmetrics.enums.AlarmApp
import co.adityarajput.alarmetrics.utils.happenedToday
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NotificationListener : NotificationListenerService() {
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Default + serviceJob)
    private val alarmsRepository by lazy { AppContainer(this).alarmsRepository }
    private val recordsRepository by lazy { AppContainer(this).recordsRepository }

    @Volatile
    private var alarms: List<Alarm> = emptyList()

    override fun onCreate() {
        super.onCreate()
        Log.d("NotificationListener", "Service created")

        serviceScope.launch {
            alarmsRepository.list().collectLatest { newAlarms ->
                alarms = newAlarms
                Log.d("NotificationListener", "Alarms updated: $alarms")
            }
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d("NotificationListener", "Listener connected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val app = AlarmApp.entries.find { sbn.packageName == it.`package` } ?: return
        val title = Regex(app.pattern).find(
            (sbn.notification.extras.getString("android.title") ?: "") +
                    "\n" + (sbn.notification.extras.getCharSequence("android.text") ?: "")
        )?.groupValues?.get(1) ?: return

        serviceScope.launch {
            var alarm = alarms.find { it.title == title && it.app == app }
            var alarmId: Long
            if (alarm != null) {
                alarmId = alarm.id
                Log.d("NotificationListener", "Matched $alarm")

                if (!alarm.isActive) {
                    Log.d("NotificationListener", "Tracking is disabled")
                    return@launch
                }
            } else {
                alarm = Alarm(title, app)
                alarmId = alarmsRepository.save(alarm)
                Log.d("NotificationListener", "Created $alarm")
            }

            var record = recordsRepository.getLatest(alarmId)
            if (record != null && record.firstSnooze.happenedToday()) {
                recordsRepository.registerSnooze(record.id)
                Log.d("NotificationListener", "Updated $record")
            } else {
                record = Record(alarmId)
                recordsRepository.create(record)
                Log.d("NotificationListener", "Created $record")
            }
        }
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d("NotificationListener", "Listener disconnected")
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}

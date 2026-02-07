package co.adityarajput.alarmetrics.services

import android.content.pm.ApplicationInfo
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import co.adityarajput.alarmetrics.Constants.SETTINGS
import co.adityarajput.alarmetrics.Constants.TRIM_ALARMS
import co.adityarajput.alarmetrics.data.AppContainer
import co.adityarajput.alarmetrics.data.alarm.Alarm
import co.adityarajput.alarmetrics.data.record.Record
import co.adityarajput.alarmetrics.enums.AlarmApp
import co.adityarajput.alarmetrics.utils.Logger
import co.adityarajput.alarmetrics.utils.happenedToday
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NotificationListener : NotificationListenerService() {
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Default + serviceJob)
    private val repository by lazy { AppContainer(this).repository }
    private val sharedPreferences by lazy { getSharedPreferences(SETTINGS, MODE_PRIVATE) }

    @Volatile
    private var alarms: List<Alarm> = emptyList()

    override fun onCreate() {
        super.onCreate()
        Logger.i("NotificationListener", "Service created")

        serviceScope.launch {
            repository.alarms().collectLatest { newAlarms ->
                alarms = newAlarms
                Logger.d("NotificationListener", "Alarms updated: $alarms")
            }
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Logger.i("NotificationListener", "Listener connected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val notificationTitle = sbn.notification.extras.getString("android.title") ?: ""
        val notificationContent = sbn.notification.extras.getCharSequence("android.text") ?: ""
        Logger.d(
            "NotificationListener",
            "Notification received from ${sbn.packageName}: {$notificationTitle, $notificationContent}",
        )

        val app = AlarmApp.entries.find { sbn.packageName == it.`package` } ?: return
        val alarmTitle =
            Regex(app.pattern).find("$notificationTitle\n$notificationContent")
                ?.groupValues?.get(1) ?: return

        serviceScope.launch {
            var alarm = alarms.find { it.title == alarmTitle && it.app == app }
            var alarmId: Long
            if (alarm != null) {
                alarmId = alarm.id
                Logger.i("NotificationListener", "Matched $alarm")

                if (!alarm.isActive) {
                    Logger.d("NotificationListener", "Tracking is disabled")
                    return@launch
                }
            } else {
                alarm = Alarm(alarmTitle, app)
                alarmId = repository.create(alarm)
                Logger.i("NotificationListener", "Created $alarm")
            }

            var record = repository.getLatestRecord(alarmId)
            if (record != null && record.firstSnooze.happenedToday()) {
                repository.updateRecord(record.id, System.currentTimeMillis())
                Logger.i("NotificationListener", "Updated $record")
            } else {
                record = Record(alarmId)
                repository.create(record)
                Logger.i("NotificationListener", "Created $record")
            }

            if (sharedPreferences.getBoolean(TRIM_ALARMS, false)) {
                repository.trimAlarms(applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0)
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        val app = AlarmApp.entries.find { sbn.packageName == it.`package` } ?: return
        val title = Regex(app.pattern).find(
            (sbn.notification.extras.getString("android.title") ?: "") +
                    "\n" + (sbn.notification.extras.getCharSequence("android.text") ?: ""),
        )?.groupValues?.get(1) ?: return

        serviceScope.launch {
            val alarm =
                alarms.find { it.title == title && it.app == app && it.isActive } ?: return@launch
            Logger.i("NotificationListener", "Matched $alarm")

            val record = repository.getLatestRecord(alarm.id) ?: return@launch
            if (record.firstSnooze.happenedToday()) {
                repository.updateRecord(record.id, System.currentTimeMillis())
                Logger.i("NotificationListener", "Updated $record")
            }
        }
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Logger.i("NotificationListener", "Listener disconnected")
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}

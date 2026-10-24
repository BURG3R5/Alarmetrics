package co.adityarajput.alarmetrics.enums

import android.content.Context
import co.adityarajput.alarmetrics.R

enum class AlarmApp(val `package`: String, val displayName: String, val patterns: List<Int>) {
    ANDROID_CLOCK(
        "com.android.deskclock",
        "Android Clock",
        listOf(R.string.android_clock),
    ),
    GOOGLE_CLOCK(
        "com.google.android.deskclock",
        "Google Clock",
        listOf(R.string.google_clock),
    ),
    SAMSUNG_CLOCK(
        "com.sec.android.app.clockpackage",
        "Samsung Clock",
        listOf(R.string.samsung_clock, R.string.samsung_clock_v2),
    ),
    SAMSUNG_CALENDAR(
        "com.samsung.android.calendar",
        "Samsung Calendar",
        listOf(R.string.samsung_calendar),
    ),
    SAMSUNG_REMINDER(
        "com.samsung.android.app.reminder",
        "Samsung Reminder",
        listOf(R.string.samsung_reminder),
    );

    fun extractAlarmTitle(
        context: Context,
        notificationTitle: String,
        notificationContent: CharSequence,
    ) =
        patterns.firstNotNullOfOrNull {
            Regex(context.getString(it))
                .find("$notificationTitle\n$notificationContent")
                ?.groupValues?.get(1)
        }
}

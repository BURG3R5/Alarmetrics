package co.adityarajput.alarmetrics.enums

import co.adityarajput.alarmetrics.R

enum class AlarmApp(val `package`: String, val displayName: String, val pattern: Int) {
    ANDROID_CLOCK(
        "com.android.deskclock",
        "Android Clock",
        R.string.android_clock,
    ),
    GOOGLE_CLOCK(
        "com.google.android.deskclock",
        "Google Clock",
        R.string.google_clock,
    ),
    GOOGLE_CALENDAR(
        "com.google.android.calendar",
        "Google Calendar", R.string.google_calendar,
    ),
    SAMSUNG_CLOCK(
        "com.sec.android.app.clockpackage",
        "Samsung Clock",
        R.string.samsung_clock,
    ),
    SAMSUNG_CALENDAR(
        "com.samsung.android.calendar",
        "Samsung Calendar",
        R.string.samsung_calendar,
    ),
    SAMSUNG_REMINDER(
        "com.samsung.android.app.reminder",
        "Samsung Reminder",
        R.string.samsung_reminder,
    ),
}

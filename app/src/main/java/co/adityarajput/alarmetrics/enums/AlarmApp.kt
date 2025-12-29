package co.adityarajput.alarmetrics.enums

enum class AlarmApp(val `package`: String, val displayName: String, val pattern: String) {
    GOOGLE_CLOCK("com.google.android.deskclock", "Google Clock", "Snoozed alarm\\n(?:.*) - (.*)"),
    GOOGLE_CALENDAR("com.google.android.calendar", "Google Calendar", "(.*) \\(snoozed\\)"),
    SAMSUNG_CLOCK("com.sec.android.app.clockpackage", "Samsung Clock", "^(?!\\d+ alarms)(.*) snoozed"),
    SAMSUNG_CALENDAR("com.samsung.android.calendar", "Samsung Calendar", "(.*) \\(snoozed\\)"),
    SAMSUNG_REMINDER("com.samsung.android.app.reminder", "Samsung Reminder", "(.*) \\(snoozed\\)"),
}

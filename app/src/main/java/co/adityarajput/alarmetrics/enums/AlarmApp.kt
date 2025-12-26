package co.adityarajput.alarmetrics.enums

enum class AlarmApp(val `package`: String, val displayName: String, val pattern: String) {
    GOOGLE_CLOCK("com.google.android.deskclock", "Clock", "Snoozed alarm\\n(?:.*) - (.*)"),
    GOOGLE_CALENDAR("com.google.android.calendar", "Calendar", "(.*) \\(snoozed\\)"),
    SAMSUNG_CLOCK("com.sec.android.app.clockpackage", "Clock", "(.*) snoozed"),
    SAMSUNG_CALENDAR("com.samsung.android.calendar", "Calendar", "(.*) \\(snoozed\\)"),
    SAMSUNG_REMINDER("com.samsung.android.app.reminder", "Reminder", "(.*) \\(snoozed\\)"),
}

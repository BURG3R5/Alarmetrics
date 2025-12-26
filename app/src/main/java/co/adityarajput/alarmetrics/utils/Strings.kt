package co.adityarajput.alarmetrics.utils

fun Long.toShortHumanReadableTime(): String {
    val seconds = this / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        days > 1000 -> "1k+ days"
        days > 0 -> "$days day${if (days > 1) "s" else ""}"
        hours > 0 -> "$hours hr${if (hours > 1) "s" else ""}"
        minutes > 0 -> "$minutes min${if (minutes > 1) "s" else ""}"
        seconds > 0 -> "$seconds sec${if (seconds > 1) "s" else ""}"
        else -> "0 secs"
    }
}

fun Int.withUnit(unit: String): String {
    return when (this) {
        1 -> "1 $unit"
        else -> "$this ${unit}s"
    }
}

fun String.getLast(length: Int): String =
    if (this.length <= length) this else "..." + this.takeLast(length - 3)

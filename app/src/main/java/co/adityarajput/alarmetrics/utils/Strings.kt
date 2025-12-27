package co.adityarajput.alarmetrics.utils

fun Int.withUnit(unit: String): String {
    return when (this) {
        1 -> "1 $unit"
        else -> "$this ${unit}s"
    }
}

fun String.clipTo(length: Int): String =
    if (this.length <= length) this else this.take(length - 3) + "..."

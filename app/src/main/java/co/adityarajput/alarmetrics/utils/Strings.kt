package co.adityarajput.alarmetrics.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import co.adityarajput.alarmetrics.R

fun String.clipTo(length: Int): String =
    if (this.length <= length) this else this.take(length - 3) + "..."

@Composable
fun Int.toShortHumanReadableTime(): String {
    val seconds = this / 1000
    val minutes = seconds / 60
    val hours = minutes / 60

    return when {
        hours > 0 -> pluralStringResource(R.plurals.hour, hours, hours)
        minutes > 0 -> pluralStringResource(R.plurals.minute, minutes, minutes)
        else -> pluralStringResource(R.plurals.second, seconds, seconds)
    }
}

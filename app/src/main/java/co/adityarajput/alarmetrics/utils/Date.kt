package co.adityarajput.alarmetrics.utils

import co.adityarajput.alarmetrics.enums.Range
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun Long.indexIn(range: Range): Int {
    return when (range) {
        Range.WEEK -> this.toDate().dayOfWeek.value - 1
        Range.MONTH -> (this.toDate().dayOfMonth - 1) / 7
        Range.YEAR -> this.toDate().monthValue - 1
        Range.DECADE -> this.toDate().year % 10
    }
}

fun Long.minus(offset: Int, range: Range): Long {
    val offsetDate = when (range) {
        Range.WEEK -> this.toDate().minusWeeks(offset.toLong()).plusDays(1)
        Range.MONTH -> this.toDate().minusMonths(offset.toLong()).plusDays(1)
        Range.YEAR -> this.toDate().minusYears(offset.toLong()).plusDays(1)
        else -> this.toDate()
    }

    return offsetDate.millisAtStartOfDay() + (this % (24 * 60 * 60 * 1000))
}

fun Long.getStartOfRange(range: Range): Long {
    val startDate = when (range) {
        Range.WEEK -> this.toDate().minusDays(this.toDate().dayOfWeek.value.toLong() - 1)
        Range.MONTH -> this.toDate().withDayOfMonth(1)
        Range.YEAR -> this.toDate().withDayOfYear(1)
        Range.DECADE -> this.toDate().withYear(10 * (this.toDate().year / 10)).withDayOfYear(1)
    }

    return startDate.millisAtStartOfDay()
}

fun Long.getEndOfRange(range: Range): Long {
    val endDate = when (range) {
        Range.WEEK -> this.toDate().plusDays((7 - this.toDate().dayOfWeek.value).toLong())
        Range.MONTH -> this.toDate().withDayOfMonth(this.toDate().lengthOfMonth())
        Range.YEAR -> this.toDate().withDayOfYear(this.toDate().lengthOfYear())
        Range.DECADE -> this.toDate().withYear(10 * (this.toDate().year / 10) + 9)
            .withMonth(12)
            .withDayOfMonth(31)
    }

    return endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1
}

fun Long.happenedToday() = LocalDate.now(ZoneId.systemDefault()) == this.toDate()

fun Long.toDate(): LocalDate = Instant
    .ofEpochMilli(this)
    .atZone(ZoneId.systemDefault())
    .toLocalDate()

fun LocalDate.millisAtStartOfDay() =
    this.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

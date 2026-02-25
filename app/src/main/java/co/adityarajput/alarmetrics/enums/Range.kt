package co.adityarajput.alarmetrics.enums

import co.adityarajput.alarmetrics.R

enum class Range(val displayName: Int, val numberOfDaysInChild: Float) {
    WEEK(R.string.week, 1F),
    MONTH(R.string.month, 7F),
    YEAR(R.string.year, 30.4375F),
    DECADE(R.string.decade, 365.25F),
}

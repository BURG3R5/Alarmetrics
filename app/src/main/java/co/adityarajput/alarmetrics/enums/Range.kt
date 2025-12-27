package co.adityarajput.alarmetrics.enums

import co.adityarajput.alarmetrics.R

enum class Range(val displayName: Int) {
    WEEK(R.string.week),
    MONTH(R.string.month),
    YEAR(R.string.year),
    DECADE(R.string.decade);

    fun unit(): Range {
        return when (this) {
            DECADE -> YEAR
            YEAR -> MONTH
            MONTH -> WEEK
            WEEK -> WEEK
        }
    }
}

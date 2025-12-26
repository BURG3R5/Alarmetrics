package co.adityarajput.alarmetrics.enums

import co.adityarajput.alarmetrics.R

enum class Range(val displayName: Int) {
    WEEK(R.string.this_week),
    MONTH(R.string.this_month),
    YEAR(R.string.this_year),
    DECADE(R.string.this_decade);

    fun unit(): Range {
        return when (this) {
            DECADE -> YEAR
            YEAR -> MONTH
            MONTH -> WEEK
            WEEK -> WEEK
        }
    }
}

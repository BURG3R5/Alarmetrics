package co.adityarajput.alarmetrics.utils

fun String.clipTo(length: Int): String =
    if (this.length <= length) this else this.take(length - 3) + "..."

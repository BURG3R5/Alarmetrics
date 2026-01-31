package co.adityarajput.alarmetrics.utils

import android.util.Log
import co.adityarajput.alarmetrics.Constants

object Logger {
    val logs = ArrayDeque<String>(Constants.LOG_SIZE)

    fun d(tag: String, msg: String) {
        Log.d(tag, msg)

        if (logs.size >= Constants.LOG_SIZE) logs.removeFirst()
        logs.addLast("[${System.currentTimeMillis()}][$tag][DEBUG] $msg")
    }

    fun i(tag: String, msg: String) {
        Log.i(tag, msg)

        if (logs.size >= Constants.LOG_SIZE) logs.removeFirst()
        logs.addLast("[${System.currentTimeMillis()}][$tag][INFO] $msg")
    }
}

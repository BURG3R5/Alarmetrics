package co.adityarajput.alarmetrics.viewmodels

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import co.adityarajput.alarmetrics.Constants.BRIGHTNESS
import co.adityarajput.alarmetrics.views.Brightness

class AppearanceViewModel(val sharedPreferences: SharedPreferences) : ViewModel() {
    var brightness by mutableStateOf(Brightness.entries[sharedPreferences.getInt(BRIGHTNESS, 1)])
}

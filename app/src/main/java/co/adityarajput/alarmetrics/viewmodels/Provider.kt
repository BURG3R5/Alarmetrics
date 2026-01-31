package co.adityarajput.alarmetrics.viewmodels

import android.content.Context.MODE_PRIVATE
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.adityarajput.alarmetrics.AlarmetricsApplication
import co.adityarajput.alarmetrics.Constants.SETTINGS

object Provider {
    val Factory = viewModelFactory {
        initializer { AlarmsViewModel(alarmetricsApplication().container.repository) }
        initializer {
            AppearanceViewModel(
                alarmetricsApplication().getSharedPreferences(
                    SETTINGS,
                    MODE_PRIVATE,
                ),
            )
        }
    }
}

fun CreationExtras.alarmetricsApplication() =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as AlarmetricsApplication)

package co.adityarajput.alarmetrics.viewmodels

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.adityarajput.alarmetrics.AlarmetricsApplication

object Provider {
    val Factory = viewModelFactory {
        initializer {
            AlarmsViewModel(alarmetricsApplication().container.repository)
        }
    }
}

fun CreationExtras.alarmetricsApplication() =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as AlarmetricsApplication)

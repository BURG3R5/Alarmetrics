package co.adityarajput.alarmetrics.views.screens.permissions

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.HandlerCompat.postDelayed
import co.adityarajput.alarmetrics.R
import co.adityarajput.alarmetrics.utils.hasNotificationListenerPermission
import co.adityarajput.alarmetrics.utils.hasUnrestrictedBackgroundUsagePermission
import co.adityarajput.alarmetrics.views.Theme
import co.adityarajput.alarmetrics.views.components.AppBar
import androidx.core.net.toUri

@SuppressLint("BatteryLife")
@Composable
fun PermissionScreen(goToFiltersScreen: () -> Unit = {}) {
    val context = LocalContext.current
    var hasRequiredPermission by remember { mutableStateOf(false) }

    val watchPermissions = object : Runnable {
        override fun run() {
            hasRequiredPermission = context.hasNotificationListenerPermission()
            val hasOptionalPermission = context.hasUnrestrictedBackgroundUsagePermission()
            if (hasRequiredPermission && hasOptionalPermission) goToFiltersScreen()
            else postDelayed(Handler(Looper.getMainLooper()), this, null, 1000)
        }
    }

    Scaffold(topBar = { AppBar(stringResource(R.string.app_name), false) }) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(dimensionResource(R.dimen.padding_extra_large)),
                Arrangement.Center,
                Alignment.CenterHorizontally
            ) {
                if (!hasRequiredPermission) {
                    Text(stringResource(R.string.onboarding_info_1))
                    Button(
                        {
                            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                            context.startActivity(intent)
                            postDelayed(
                                Handler(Looper.getMainLooper()),
                                watchPermissions,
                                null,
                                1000
                            )
                        },
                        Modifier.padding(dimensionResource(R.dimen.padding_large)),
                        colors = ButtonDefaults.buttonColors(contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                    ) { Text(stringResource(R.string.grant_permission)) }
                } else {
                    Text(stringResource(R.string.onboarding_info_2))
                    Button(
                        {
                            val intent = Intent(
                                Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                                "package:${context.packageName}".toUri(),
                            )
                            context.startActivity(intent)
                        },
                        Modifier.padding(top = dimensionResource(R.dimen.padding_large)),
                        colors = ButtonDefaults.buttonColors(contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                    ) { Text(stringResource(R.string.disable_optimization)) }
                    TextButton(goToFiltersScreen) { Text(stringResource(R.string.skip)) }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PermissionScreenPreview() = Theme { PermissionScreen() }

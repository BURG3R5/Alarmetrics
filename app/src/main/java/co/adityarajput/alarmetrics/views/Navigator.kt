package co.adityarajput.alarmetrics.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import co.adityarajput.alarmetrics.utils.hasNotificationListenerPermission
import co.adityarajput.alarmetrics.views.screens.about.AboutScreen
import co.adityarajput.alarmetrics.views.screens.alarms.AlarmsScreen
import co.adityarajput.alarmetrics.views.screens.archive.ArchiveScreen
import co.adityarajput.alarmetrics.views.screens.permissions.PermissionScreen

@Composable
fun Navigator(controller: NavHostController) {
    val hasPermission = remember { controller.context.hasNotificationListenerPermission() }

    NavHost(
        controller,
        when {
            hasPermission -> Routes.ALARMS.name
            else -> Routes.PERMISSION.name
        },
    ) {
        composable(Routes.PERMISSION.name) {
            PermissionScreen {
                controller.navigate(
                    Routes.ALARMS.name,
                    NavOptions.Builder().setPopUpTo(Routes.PERMISSION.name, true).build(),
                )
            }
        }
        composable(Routes.ALARMS.name) {
            AlarmsScreen(
                { controller.navigate(Routes.ABOUT.name) },
                { controller.navigate(Routes.ARCHIVE.name) },
            )
        }
        composable(Routes.ARCHIVE.name) { ArchiveScreen({ controller.popBackStack() }) }
        composable(Routes.ABOUT.name) { AboutScreen({ controller.popBackStack() }) }
    }
}

enum class Routes {
    PERMISSION,
    ALARMS,
    ARCHIVE,
    ABOUT,
}

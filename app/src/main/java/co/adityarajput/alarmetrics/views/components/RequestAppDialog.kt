package co.adityarajput.alarmetrics.views.components

import android.app.Notification.FLAG_GROUP_SUMMARY
import android.content.ClipData
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import co.adityarajput.alarmetrics.R
import co.adityarajput.alarmetrics.services.NotificationListener
import kotlinx.coroutines.launch

@Composable
fun RequestAppDialog(hideDialog: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboard.current
    val copySuccess = stringResource(R.string.details_copy_success)
    val notifications = remember {
        NotificationListener.instance?.activeNotifications
            ?.filter { it.notification.flags and FLAG_GROUP_SUMMARY == 0 }
            ?.map {
                Notification(
                    it.packageName,
                    (it.notification.extras.getString("android.title") ?: ""),
                    (it.notification.extras.getCharSequence("android.text") ?: "").toString(),
                )
            } ?: listOf()
    }
    var selectedNotification by remember { mutableStateOf<Notification?>(null) }

    AlertDialog(
        hideDialog,
        title = { Text(stringResource(R.string.request_app)) },
        text = {
            Column {
                Text(stringResource(R.string.choose_snooze_notification))
                Column(
                    Modifier
                        .heightIn(max = 250.dp)
                        .padding(dimensionResource(R.dimen.padding_medium))
                        .selectableGroup()
                        .verticalScroll(rememberScrollState()),
                    Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
                ) {
                    notifications.forEach {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .selectable(it == selectedNotification) {
                                    selectedNotification = it
                                },
                            Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
                            Alignment.CenterVertically,
                        ) {
                            RadioButton(it == selectedNotification, null)
                            Text(
                                buildAnnotatedString {
                                    withStyle(SpanStyle(fontWeight = FontWeight.Medium)) {
                                        append(it.title)
                                    }
                                    append("\n" + it.content)
                                },
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Normal,
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                {
                    scope.launch {
                        clipboard.setClipEntry(
                            ClipData.newPlainText(
                                "details",
                                selectedNotification!!.asIssueBody(),
                            ).toClipEntry(),
                        )
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
                            Toast
                                .makeText(context, copySuccess, Toast.LENGTH_SHORT)
                                .show()
                        hideDialog()
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                "https://github.com/BURG3R5/Alarmetrics/issues/new?template=app_request.yaml".toUri(),
                            ),
                        )
                    }
                },
                enabled = selectedNotification != null,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary),
            ) { Text(stringResource(R.string.submit)) }
        },
        dismissButton = {
            TextButton(hideDialog) {
                Text(
                    stringResource(R.string.cancel),
                    fontWeight = FontWeight.Normal,
                )
            }
        },
    )
}

data class Notification(
    val packageName: String,
    val title: String,
    val content: String,
) {
    fun asIssueBody() =
        "* Package: `$packageName`\n\n* Title: \"$title\"\n\n* Content: \"$content\""
}

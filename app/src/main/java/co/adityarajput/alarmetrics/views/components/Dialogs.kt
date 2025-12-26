package co.adityarajput.alarmetrics.views.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import co.adityarajput.alarmetrics.R
import co.adityarajput.alarmetrics.views.Theme

@Composable
fun ArchiveDialog(
    isArchived: Boolean,
    onConfirm: () -> Unit,
    hideDialog: () -> Unit,
) {
    AlertDialog(
        hideDialog,
        title = {
            Text(
                stringResource(
                    R.string.dialog_title,
                    stringResource(if (isArchived) R.string.unarchive else R.string.archive),
                ),
            )
        },
        text = {
            Text(
                stringResource(
                    if (isArchived) R.string.unarchive_confirmation
                    else R.string.archive_confirmation,
                ),
            )
        },
        confirmButton = {
            TextButton(
                {
                    onConfirm()
                    hideDialog()
                },
            ) {
                Text(stringResource(if (isArchived) R.string.unarchive else R.string.archive))
            }
        },
        dismissButton = {
            TextButton(hideDialog) {
                Text(stringResource(R.string.cancel), fontWeight = FontWeight.Normal)
            }
        },
    )
}

@Composable
fun DeleteDialog(
    onConfirm: () -> Unit,
    hideDialog: () -> Unit,
) {
    AlertDialog(
        hideDialog,
        title = { Text(stringResource(R.string.dialog_title, stringResource(R.string.delete))) },
        text = { Text(stringResource(R.string.delete_confirmation)) },
        confirmButton = {
            TextButton(
                {
                    onConfirm()
                    hideDialog()
                },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.tertiary),
            ) { Text(stringResource(R.string.delete)) }
        },
        dismissButton = {
            TextButton(hideDialog) {
                Text(stringResource(R.string.cancel), fontWeight = FontWeight.Normal)
            }
        },
    )
}

@Preview
@Composable
private fun ArchiveDialogPreview() =
    Theme { ArchiveDialog(true, {}, {}) }

@Preview
@Composable
private fun DeleteDialogPreview() =
    Theme { DeleteDialog({}, {}) }

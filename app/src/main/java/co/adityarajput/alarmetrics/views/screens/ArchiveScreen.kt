package co.adityarajput.alarmetrics.views.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import co.adityarajput.alarmetrics.R
import co.adityarajput.alarmetrics.enums.DialogState
import co.adityarajput.alarmetrics.utils.clipTo
import co.adityarajput.alarmetrics.viewmodels.AlarmsViewModel
import co.adityarajput.alarmetrics.viewmodels.Provider
import co.adityarajput.alarmetrics.views.components.AppBar
import co.adityarajput.alarmetrics.views.components.ArchiveDialog
import co.adityarajput.alarmetrics.views.components.DeleteDialog
import co.adityarajput.alarmetrics.views.components.Tile

@Composable
fun ArchiveScreen(
    goBack: () -> Unit,
    viewModel: AlarmsViewModel = viewModel(factory = Provider.Factory),
) {
    val alarmsState = viewModel.alarms.collectAsState()

    Scaffold(
        topBar = {
            AppBar(
                stringResource(R.string.archive),
                true,
                goBack,
            )
        },
    ) { paddingValues ->
        if (alarmsState.value.state == null) {
            Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
        } else if (alarmsState.value.state!!.find { !it.alarm.isActive } == null) {
            Box(
                Modifier.fillMaxSize(),
                Alignment.Center,
            ) {
                Text(
                    stringResource(R.string.archive_empty),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .fillMaxSize(),
                contentPadding = paddingValues,
            ) {
                items(
                    alarmsState.value.state!!.filter { !it.alarm.isActive },
                    { it.alarm.id },
                ) {
                    Tile(
                        it.alarm.title,
                        it.alarm.app.displayName.clipTo(30),
                        null,
                        { viewModel.selectedAlarm = if (it.alarm != viewModel.selectedAlarm) it.alarm else null },
                        viewModel.selectedAlarm == it.alarm,
                        {
                            IconButton({ viewModel.dialogState = DialogState.ARCHIVE }) {
                                Icon(
                                    painterResource(R.drawable.unarchive),
                                    stringResource(
                                        R.string.alttext_toggle_button,
                                        stringResource(R.string.unarchive),
                                    ),
                                )
                            }
                            IconButton(
                                { viewModel.dialogState = DialogState.DELETE },
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = MaterialTheme.colorScheme.tertiary,
                                ),
                            ) { Icon(painterResource(R.drawable.delete), stringResource(R.string.delete)) }
                        },
                    )
                }
            }

            when (viewModel.dialogState) {
                DialogState.ARCHIVE -> ArchiveDialog(
                    !viewModel.selectedAlarm!!.isActive,
                    { viewModel.toggleAlarm() },
                    { viewModel.dialogState = null },
                )

                DialogState.DELETE -> DeleteDialog(
                    { viewModel.deleteAlarm() },
                    { viewModel.dialogState = null },
                )

                null -> {}
            }
        }
    }
}

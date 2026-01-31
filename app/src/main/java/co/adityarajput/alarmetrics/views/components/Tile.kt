package co.adityarajput.alarmetrics.views.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import co.adityarajput.alarmetrics.R
import co.adityarajput.alarmetrics.enums.AlarmApp
import co.adityarajput.alarmetrics.utils.clipTo
import co.adityarajput.alarmetrics.utils.toShortHumanReadableTime
import co.adityarajput.alarmetrics.views.Theme

@Composable
fun Tile(
    title: String,
    subtitle: String,
    trailing: String? = null,
    onClick: () -> Unit = {},
    expanded: Boolean = false,
    buttons: @Composable RowScope.() -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    Card(
        onClick,
        Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_small))
            .animateContentSize(
                tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing,
                ),
            ),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_large)),
            Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically,
            ) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        MaterialTheme.colorScheme.onSurfaceVariant,
                        11.sp,
                    ),
                )
                if (trailing != null)
                    Text(
                        trailing,
                        style = MaterialTheme.typography.bodySmall.copy(
                            MaterialTheme.colorScheme.onSurfaceVariant,
                            8.sp,
                        ),
                    )
            }
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
            )
            if (expanded) {
                content()
                Row(Modifier.fillMaxWidth(), Arrangement.End) { buttons() }
            }
        }
    }
}

@Preview
@Composable
private fun AlarmTile() = Theme {
    Tile(
        "Exercise",
        AlarmApp.GOOGLE_CLOCK.displayName.clipTo(30),
        (69 * 60 * 1000).toShortHumanReadableTime(),
        { },
        true,
        { Text("BUTTONS") },
        { Text("CONTENT") },
    )
}

@Preview
@Composable
private fun ArchivedAlarmTile() =
    Theme { Tile("Wake up", AlarmApp.GOOGLE_CLOCK.displayName.clipTo(30)) }

package com.example.mopera.ui.VideoPlayer.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.RadioButton
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.example.mopera.data.VideoFormat

@Composable
fun VideoPlayerQualityOptions(
    modifier: Modifier = Modifier,
    formats: List<VideoFormat>,
    focusRequester: FocusRequester,
    onFormatSelected: (VideoFormat) -> Unit
) {
    val menuFocusRequester = remember { FocusRequester() }

    Column(modifier = modifier) {
        IconButton(
            onClick = {
                VideoPlayerSettingsMenuState.showQualityControls = false
                focusRequester.requestFocus()
            },
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.colors(
                containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                contentColor = Color.Black,
                focusedContainerColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            border = ButtonDefaults.border(
                focusedBorder = Border(BorderStroke(2.dp, MaterialTheme.colorScheme.border))
            )
        ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Black)
        }

        if (formats.isEmpty()) {
            Text("No available formats", style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground)
        } else {
            Text(
                text = "Choose Quality",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            formats.forEachIndexed { index, format ->
                QualityOption(
                    label = format.resolution,
                    selected = VideoPlayerSettingsMenuState.selectedQuality?.resolution == format.resolution,
                    onClick = {
                        onFormatSelected(format)
                        VideoPlayerSettingsMenuState.selectedQuality = format
                    },
                    modifier = if (index == 0) Modifier.focusRequester(menuFocusRequester) else Modifier
                )
            }
        }

        LaunchedEffect(Unit) {
            menuFocusRequester.requestFocus()
        }
    }
}

@Composable
fun QualityOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        colors = ClickableSurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(BorderStroke(2.dp, MaterialTheme.colorScheme.border))
        ),
        shape = ClickableSurfaceDefaults.shape(MaterialTheme.shapes.small),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth(0.33f)
                .height(55.dp)
        ) {
            Text(
                text = label.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(start = 8.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            RadioButton(
                selected = selected,
                onClick = onClick,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

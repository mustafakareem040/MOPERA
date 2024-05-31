package com.example.mopera.ui.VideoPlayer.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.media3.common.MediaItem
import androidx.tv.material3.Border
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.RadioButton
import androidx.tv.material3.Surface
import androidx.tv.material3.Switch
import androidx.tv.material3.Text

@Composable
fun VideoPlayerSubtitlesOptions(
    modifier: Modifier = Modifier,
    tracks: List<MediaItem.SubtitleConfiguration>,
    focusRequester: FocusRequester,
    onTrackSelected: (MediaItem.SubtitleConfiguration?) -> Unit
) {
    val menuFocusRequester = remember { FocusRequester() }
    Column(modifier = modifier) {
        IconButton(
            onClick = {
                VideoPlayerSettingsMenuState.showSubtitlesControls = false
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

        if (tracks.isEmpty()) {
            Text("No available subtitles", style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground)
        } else {
            Text(
                text = "Choose subtitles",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            tracks.forEachIndexed { index, track ->
                track.label?.let { label ->
                    SubtitleOption(
                        label = label,
                        selected = VideoPlayerSettingsMenuState.selectedTrack == track,
                        onClick = {
                            VideoPlayerSettingsMenuState.selectedTrack = track
                            onTrackSelected(track)
                        },
                        modifier = if (index == 0) {
                            Modifier.focusRequester(menuFocusRequester)
                        } else {
                            Modifier
                        }
                    )
                }
            }
            Spacer(modifier=Modifier.height(20.dp))
            SubtitleSwitch(VideoPlayerSettingsMenuState.selectedTrack != null) {
                VideoPlayerSettingsMenuState.selectedTrack = null
                onTrackSelected(VideoPlayerSettingsMenuState.selectedTrack)
            }
            LaunchedEffect(Unit) {
                menuFocusRequester.requestFocus()
            }
        }
    }
}

@Composable
fun SubtitleSwitch(subtitlesEnabled: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        colors = ClickableSurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = ClickableSurfaceDefaults.shape(MaterialTheme.shapes.small),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(BorderStroke(2.dp, MaterialTheme.colorScheme.border))
        ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
        modifier = Modifier
            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth(0.33f)
                .height(55.dp)
        ) {
            Text(
                text = "Show subtitles",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            Switch(
                checked = subtitlesEnabled,
                onCheckedChange = null,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}

@Composable
fun SubtitleOption(
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

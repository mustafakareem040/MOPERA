package com.example.mopera.ui.VideoPlayer.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface

@Composable
fun VideoPlayerControlsIcon(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    contentDescription: String? = null,
    onClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }


    Surface(
        modifier = modifier.size(55.dp),
        onClick = onClick,
        shape = ClickableSurfaceDefaults.shape(shape = CircleShape),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
            focusedContainerColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(BorderStroke(2.dp, MaterialTheme.colorScheme.border))
        ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.15f),
        interactionSource = interactionSource
    ) {
        Icon(
            icon,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentDescription = contentDescription,
            tint = LocalContentColor.current
        )
    }
}

package com.example.mopera.ui.screens.homeScreen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mopera.data.Video

/** Artwork of the focused title, fading into the background. */
@Composable
fun HeroBackdrop(video: Video?, modifier: Modifier = Modifier) {
    val background = MaterialTheme.colorScheme.surface
    Box(modifier) {
        Crossfade(
            targetState = video?.backdropUrl,
            label = "backdrop",
            modifier = Modifier.fillMaxSize()
        ) { url ->
            if (url != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(url)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    // Posters are tall; show their upper middle, where the faces usually are.
                    alignment = BiasAlignment(0f, -0.45f),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Box(
            Modifier
                .matchParentSize()
                .background(
                    Brush.horizontalGradient(
                        0f to background,
                        0.5f to background.copy(alpha = 0.55f),
                        1f to Color.Transparent
                    )
                )
        )
        Box(
            Modifier
                .matchParentSize()
                .background(Brush.verticalGradient(0.45f to Color.Transparent, 1f to background))
        )
    }
}

/** Name of the focused title and one short line about it. No description. */
@Composable
fun HeroInfo(video: Video?, modifier: Modifier = Modifier) {
    Crossfade(targetState = video, label = "hero", modifier = modifier) { shown ->
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
            if (shown != null) {
                Text(
                    text = shown.title,
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = metaLine(shown),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                    maxLines = 1
                )
            }
        }
    }
}

private fun metaLine(video: Video): String = buildList {
    video.year?.trim()?.takeIf { it.isNotEmpty() && it != "0" }?.let(::add)
    video.stars?.trim()?.takeIf { (it.toFloatOrNull() ?: 0f) > 0f }?.let { add("⭐ $it") }
    add(if (video.isSeries) "Series" else "Movie")
    val season = video.season?.trim()?.toIntOrNull() ?: 0
    val episode = video.episodeNummer?.trim()?.toIntOrNull() ?: 0
    if (video.isSeries && season > 0 && episode > 0) add("S$season E$episode")
}.joinToString("  •  ")

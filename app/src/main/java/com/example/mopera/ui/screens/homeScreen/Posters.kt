package com.example.mopera.ui.screens.homeScreen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.PivotOffsets
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.itemsIndexed
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.Border
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mopera.data.Video

internal val SidePadding = 48.dp
private val PosterWidth = 124.dp
private val GridPosterMinWidth = 110.dp
private val PosterSpacing = 14.dp
private val GridRowSpacing = 20.dp

// Height taken by a row's title above its posters.
private val RowTitleSpace = 40.dp

// A whole row: title, posters and the room their focus zoom needs below.
private val RowHeight = RowTitleSpace + PosterWidth * 1.5f + 12.dp
private val RowSpacing = 8.dp

// Start fetching the next page while this many posters are still ahead.
private const val LoadAhead = 14

internal fun cardKey(section: String, video: Video) = "$section:${video.nb}"

/**
 * A [FocusRequester] for the element [key]. While it is on screen [focus] can send focus
 * to it, and it takes focus by itself when [FocusMemory.pendingKey] points at it.
 */
@Composable
internal fun rememberRestorableFocusRequester(key: String, focus: FocusMemory): FocusRequester {
    val requester = remember { FocusRequester() }
    DisposableEffect(key, focus) {
        focus.register(key, requester)
        onDispose { focus.unregister(key, requester) }
    }
    if (focus.pendingKey == key) {
        LaunchedEffect(Unit) {
            // Wait a frame: a card that just appeared only starts drawing its focus highlight then.
            withFrameNanos { }
            runCatching { requester.requestFocus() }
        }
    }
    return requester
}

@Composable
fun PosterCard(
    video: Video,
    focusKey: String,
    focus: FocusMemory,
    onFocused: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = MaterialTheme.shapes.small
    Card(
        onClick = onClick,
        modifier = modifier
            .aspectRatio(2f / 3f)
            .focusRequester(rememberRestorableFocusRequester(focusKey, focus))
            .onFocusChanged { if (it.isFocused) onFocused() },
        shape = CardDefaults.shape(shape),
        colors = CardDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        scale = CardDefaults.scale(focusedScale = 1.08f),
        border = CardDefaults.border(
            focusedBorder = Border(BorderStroke(2.dp, MaterialTheme.colorScheme.border), shape = shape)
        ),
    ) {
        PosterImage(video)
    }
}

@Composable
private fun PosterImage(video: Video) {
    val url = video.posterUrl
    var failed by remember(url) { mutableStateOf(url == null) }
    Box(Modifier.fillMaxSize()) {
        if (failed) {
            // No artwork: show the name instead of an empty box.
            Text(
                text = video.title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(8.dp)
            )
        } else {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .crossfade(true)
                    .build(),
                contentDescription = video.title,
                contentScale = ContentScale.Crop,
                onError = { failed = true },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun PosterPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier
            .aspectRatio(2f / 3f)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
    )
}

/** One titled row of posters. */
data class PosterRowModel(val section: String, val title: String, val feed: VideoFeed)

@Composable
fun PosterRows(
    rows: List<PosterRowModel>,
    focus: FocusMemory,
    onFocused: (Video) -> Unit,
    onClick: (Video) -> Unit,
    onRetry: () -> Unit,
) {
    // A row that failed is left out; while it loads it shows placeholders.
    val visible = rows.filter { it.feed.items.isNotEmpty() || !it.feed.failed }
    if (visible.isEmpty()) {
        LoadError(onRetry)
        return
    }
    // The focused row always sits right under the hero; the rows above it slide out of view.
    // There are only a few rows, so they are all kept on screen (no lazy column), which also
    // keeps moving between them simple and reliable.
    var focusedRow by rememberSaveable { mutableIntStateOf(0) }
    val shift by animateDpAsState(
        targetValue = (RowHeight + RowSpacing) * focusedRow.coerceAtMost(visible.lastIndex),
        label = "rows"
    )
    Box(
        Modifier
            .fillMaxSize()
            .clipToBounds()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(align = Alignment.Top, unbounded = true)
                .offset { IntOffset(0, -shift.roundToPx()) },
            verticalArrangement = Arrangement.spacedBy(RowSpacing)
        ) {
            visible.forEachIndexed { index, row ->
                key(row.section) {
                    PosterRow(
                        row = row,
                        above = visible.getOrNull(index - 1)?.section,
                        below = visible.getOrNull(index + 1)?.section,
                        focus = focus,
                        onFocused = onFocused,
                        onClick = onClick,
                        modifier = Modifier.onFocusChanged { if (it.hasFocus) focusedRow = index }
                    )
                }
            }
        }
    }
}

@Composable
private fun PosterRow(
    row: PosterRowModel,
    above: String?,
    below: String?,
    focus: FocusMemory,
    onFocused: (Video) -> Unit,
    onClick: (Video) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.height(RowHeight)) {
        Text(
            text = row.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(start = SidePadding)
                .height(RowTitleSpace - 12.dp)
        )
        val videos = row.feed.items
        if (videos.isEmpty()) {
            Row(
                modifier = Modifier.padding(start = SidePadding, top = 12.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(PosterSpacing)
            ) {
                repeat(8) { PosterPlaceholder(Modifier.width(PosterWidth)) }
            }
        } else BoxWithConstraints {
            TvLazyRow(
                contentPadding = PaddingValues(horizontal = SidePadding, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(PosterSpacing),
                // The focused poster stays at the left edge, like the first one.
                pivotOffsets = PivotOffsets(parentFraction = (SidePadding / maxWidth).coerceIn(0f, 0.5f)),
            ) {
                items(videos, key = { it.nb }) { video ->
                    val key = cardKey(row.section, video)
                    PosterCard(
                        video = video,
                        focusKey = key,
                        focus = focus,
                        onFocused = {
                            focus.focused(key, row.section, HomeTab.HOME.name)
                            onFocused(video)
                        },
                        onClick = { onClick(video) },
                        modifier = Modifier
                            .width(PosterWidth)
                            .focusProperties {
                                // Up from the first row goes to the open tab. Between rows,
                                // focus goes back to the poster you were on in that row.
                                up = if (above == null) {
                                    focus.requesterFor(tabKey(HomeTab.HOME))
                                } else {
                                    focus.requesterFor(focus.lastIn(above))
                                }
                                if (below != null) down = focus.requesterFor(focus.lastIn(below))
                            }
                    )
                }
            }
        }
    }
}

/** Every title of a feed in a grid that loads more as you scroll. */
@Composable
fun PosterGrid(
    feed: VideoFeed,
    tab: HomeTab,
    focus: FocusMemory,
    onFocused: (Video) -> Unit,
    onClick: (Video) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
) {
    val videos = feed.items
    if (videos.isEmpty()) {
        if (feed.failed) LoadError(onRetry) else PlaceholderGrid()
    } else BoxWithConstraints(Modifier.fillMaxSize()) {
        val columns = ((maxWidth - SidePadding * 2 + PosterSpacing) / (GridPosterMinWidth + PosterSpacing))
            .toInt()
            .coerceAtLeast(1)
        TvLazyVerticalGrid(
            columns = TvGridCells.Fixed(columns),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = SidePadding, end = SidePadding, top = 12.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(GridRowSpacing),
            horizontalArrangement = Arrangement.spacedBy(PosterSpacing),
            pivotOffsets = PivotOffsets(parentFraction = (12.dp / maxHeight).coerceIn(0f, 0.5f)),
        ) {
            itemsIndexed(videos, key = { _, video -> video.nb }) { index, video ->
                val key = cardKey(tab.name, video)
                PosterCard(
                    video = video,
                    focusKey = key,
                    focus = focus,
                    onFocused = {
                        focus.focused(key, tab.name)
                        onFocused(video)
                        if (index >= videos.size - LoadAhead) onLoadMore()
                    },
                    onClick = { onClick(video) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusProperties {
                            // Up from the first line goes to the open tab.
                            if (index < columns) up = focus.requesterFor(tabKey(tab))
                        }
                )
            }
        }
    }
}

@Composable
private fun PlaceholderGrid() {
    Column(
        modifier = Modifier.padding(start = SidePadding, end = SidePadding, top = 12.dp),
        verticalArrangement = Arrangement.spacedBy(GridRowSpacing)
    ) {
        repeat(2) {
            Row(horizontalArrangement = Arrangement.spacedBy(PosterSpacing)) {
                repeat(7) { PosterPlaceholder(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun LoadError(onRetry: () -> Unit) {
    Column(modifier = Modifier.padding(start = SidePadding, top = 24.dp)) {
        Text(
            text = "Couldn't load. Check your connection and try again.",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            shape = ButtonDefaults.shape(MaterialTheme.shapes.small)
        ) {
            Text("Retry")
        }
    }
}

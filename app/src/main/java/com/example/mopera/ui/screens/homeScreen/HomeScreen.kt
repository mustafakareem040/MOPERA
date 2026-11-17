package com.example.mopera.ui.screens.homeScreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import com.example.mopera.data.Video
import kotlinx.coroutines.delay

private const val SectionHomeMovies = "home-movies"
private const val SectionHomeSeries = "home-series"

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenVideo: (Video) -> Unit,
    onOpenSearch: () -> Unit,
) {
    val tab = viewModel.tab
    val focus = viewModel.focus
    val hero = rememberDebounced(viewModel.heroFor(tab))
    val savedStates = rememberSaveableStateHolder()
    val rows = remember(viewModel) {
        listOf(
            PosterRowModel(SectionHomeMovies, "Latest Movies", viewModel.movies),
            PosterRowModel(SectionHomeSeries, "Latest Series", viewModel.series),
        )
    }

    // Back from the player, the episodes or search: focus returns to where it was.
    LaunchedEffect(Unit) {
        focus.pendingKey = focus.lastKey
    }

    // Nothing focused yet (first launch, or after Retry): start on the first poster.
    val firstPoster = when (tab) {
        HomeTab.HOME -> rows.firstNotNullOfOrNull { row ->
            row.feed.items.firstOrNull()?.let { cardKey(row.section, it) }
        }
        HomeTab.MOVIES -> viewModel.movies.items.firstOrNull()?.let { cardKey(tab.name, it) }
        HomeTab.SERIES -> viewModel.series.items.firstOrNull()?.let { cardKey(tab.name, it) }
    }
    LaunchedEffect(firstPoster) {
        if (focus.lastKey == null && firstPoster != null) focus.pendingKey = firstPoster
    }

    // A focus request gets a few frames to land, so it can't steal focus later on.
    val pendingKey = focus.pendingKey
    LaunchedEffect(pendingKey) {
        if (pendingKey != null) {
            repeat(4) { withFrameNanos { } }
            focus.pendingKey = null
        }
    }

    BackHandler(enabled = tab != HomeTab.HOME) {
        viewModel.selectTab(HomeTab.HOME)
        focus.pendingKey = tabKey(HomeTab.HOME)
    }

    val onRetry = {
        focus.forget()
        viewModel.retry()
    }

    HomeLayout(
        tab = tab,
        hero = hero,
        focus = focus,
        onTabSelected = viewModel::selectTab,
        onSearch = onOpenSearch,
        // Down from the top bar: the poster you were on in this tab, or the first one.
        contentEntry = { focus.requesterFor(focus.lastIn(tab.name), firstPoster) }
    ) {
        // Each tab keeps its own scroll position.
        savedStates.SaveableStateProvider(tab.name) {
            when (tab) {
                HomeTab.HOME -> PosterRows(
                    rows = rows,
                    focus = focus,
                    onFocused = { viewModel.onVideoFocused(HomeTab.HOME, it) },
                    onClick = onOpenVideo,
                    onRetry = onRetry
                )
                HomeTab.MOVIES -> PosterGrid(
                    feed = viewModel.movies,
                    tab = HomeTab.MOVIES,
                    focus = focus,
                    onFocused = { viewModel.onVideoFocused(HomeTab.MOVIES, it) },
                    onClick = onOpenVideo,
                    onLoadMore = { viewModel.loadMore(viewModel.movies) },
                    onRetry = onRetry
                )
                HomeTab.SERIES -> PosterGrid(
                    feed = viewModel.series,
                    tab = HomeTab.SERIES,
                    focus = focus,
                    onFocused = { viewModel.onVideoFocused(HomeTab.SERIES, it) },
                    onClick = onOpenVideo,
                    onLoadMore = { viewModel.loadMore(viewModel.series) },
                    onRetry = onRetry
                )
            }
        }
    }
}

@Composable
fun HomeLayout(
    tab: HomeTab,
    hero: Video?,
    focus: FocusMemory,
    onTabSelected: (HomeTab) -> Unit,
    onSearch: () -> Unit,
    contentEntry: () -> FocusRequester,
    content: @Composable () -> Unit,
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        HeroBackdrop(
            video = hero,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxWidth(0.7f)
                .fillMaxHeight(0.68f)
        )
        Column(Modifier.fillMaxSize()) {
            TopBar(
                selected = tab,
                focus = focus,
                onTabSelected = onTabSelected,
                onSearch = onSearch,
                contentEntry = contentEntry
            )
            HeroInfo(
                video = hero,
                modifier = Modifier
                    .padding(start = SidePadding, end = SidePadding, bottom = 18.dp)
                    .fillMaxWidth(0.6f)
                    .height(116.dp)
            )
            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                content()
            }
        }
    }
}

/** Follows [value], but only once it has stopped changing for a moment. */
@Composable
private fun <T> rememberDebounced(value: T, delayMillis: Long = 200): T {
    var current by remember { mutableStateOf(value) }
    LaunchedEffect(value) {
        if (current != null) delay(delayMillis)
        current = value
    }
    return current
}

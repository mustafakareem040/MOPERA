package com.example.mopera.ui.screens.homeScreen

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mopera.api.BrowseAPI
import com.example.mopera.data.MediaType
import com.example.mopera.data.Video
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

enum class HomeTab(val label: String) {
    HOME("Home"),
    MOVIES("Movies"),
    SERIES("Series"),
}

/** A paged list of titles, newest first. */
@Stable
class VideoFeed(
    val type: MediaType,
    private val loadPage: suspend (MediaType, Int) -> List<Video>,
) {
    var items by mutableStateOf<List<Video>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var failed by mutableStateOf(false)
        private set

    private var nextPage = 0
    private var endReached = false

    val canLoadMore: Boolean get() = !isLoading && !endReached

    suspend fun loadNextPage() {
        if (!canLoadMore) return
        isLoading = true
        failed = false
        try {
            val page = loadPage(type, nextPage)
            val seen = items.mapTo(HashSet()) { it.nb }
            // Lazy lists key posters by id, so a repeated title must not be added twice.
            val fresh = page.filter { seen.add(it.nb) }
            endReached = fresh.isEmpty()
            items = items + fresh
            nextPage++
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            failed = true
        } finally {
            isLoading = false
        }
    }
}

/**
 * Remembers where focus was, so it can go back there: when the screen returns,
 * when you come down from the top bar, or when you move back into a row.
 */
@Stable
class FocusMemory {
    /** The element that had focus last. */
    var lastKey: String? = null
        private set

    /** The element that should take focus as soon as it is on screen. */
    var pendingKey by mutableStateOf<String?>(null)

    private val onScreen = HashMap<String, FocusRequester>()
    private val lastInGroup = HashMap<String, String>()

    fun register(key: String, requester: FocusRequester) {
        onScreen[key] = requester
    }

    fun unregister(key: String, requester: FocusRequester) {
        if (onScreen[key] === requester) onScreen.remove(key)
    }

    /** Records that [key] got focus, as the latest element of each of [groups]. */
    fun focused(key: String, vararg groups: String) {
        lastKey = key
        groups.forEach { lastInGroup[it] = key }
    }

    fun forget() {
        lastKey = null
    }

    fun lastIn(group: String): String? = lastInGroup[group]

    /** The first of [keys] that is on screen, or the default focus search. */
    fun requesterFor(vararg keys: String?): FocusRequester =
        keys.firstNotNullOfOrNull { key -> key?.let { onScreen[it] } } ?: FocusRequester.Default
}

class HomeViewModel(
    loadPage: suspend (MediaType, Int) -> List<Video> = BrowseAPI::latest,
) : ViewModel() {
    val movies = VideoFeed(MediaType.MOVIE, loadPage)
    val series = VideoFeed(MediaType.SERIES, loadPage)
    val focus = FocusMemory()

    var tab by mutableStateOf(HomeTab.HOME)
        private set

    private val focusedByTab = mutableStateMapOf<HomeTab, Video>()

    init {
        retry()
    }

    fun selectTab(tab: HomeTab) {
        this.tab = tab
    }

    /** The title shown big at the top: the last focused one, or the first in the tab. */
    fun heroFor(tab: HomeTab): Video? = focusedByTab[tab] ?: when (tab) {
        HomeTab.HOME -> movies.items.firstOrNull() ?: series.items.firstOrNull()
        HomeTab.MOVIES -> movies.items.firstOrNull()
        HomeTab.SERIES -> series.items.firstOrNull()
    }

    fun onVideoFocused(tab: HomeTab, video: Video) {
        focusedByTab[tab] = video
    }

    fun loadMore(feed: VideoFeed) {
        if (feed.canLoadMore) viewModelScope.launch { feed.loadNextPage() }
    }

    fun retry() {
        listOf(movies, series).filter { it.items.isEmpty() }.forEach(::loadMore)
    }
}

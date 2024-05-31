package com.example.mopera.ui.screens.searchScreen

import MediaSearchSuggestion
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.rememberTvLazyGridState
import androidx.tv.material3.Border
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.mopera.api.MediaSearchResult.fetchMovies
import com.example.mopera.api.MediaSearchResult.fetchSeries
import com.example.mopera.api.SearchSuggestionsAPI.fetch
import com.example.mopera.model.Node
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch





@Composable
fun SearchScreen(navController: NavHostController) {
    var suggestionList by remember { mutableStateOf<List<String>?>(null) }
    var movies by remember { mutableStateOf<List<MediaSearchSuggestion>?>(null) }
    var series by remember { mutableStateOf<List<MediaSearchSuggestion>?>(null) }
    val scroller = rememberTvLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Node.value) {
        coroutineScope.launch {
            val suggestionJob = launch(Dispatchers.IO) {
                suggestionList = fetch(Node.value)
            }
            val moviesJob = launch(Dispatchers.IO) {
                movies = fetchMovies(Node.value)
            }
            val seriesJob = launch(Dispatchers.IO) {
                series = fetchSeries(Node.value)
            }
            suggestionJob.join()
            moviesJob.join()
            seriesJob.join()
            scroller.scrollToItem(0)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalArrangement = Arrangement.Absolute.Left
    ) {
        var idx: Int
        TvLazyVerticalGrid(columns = TvGridCells.Adaptive(minSize = 200.dp),
            modifier = Modifier.fillMaxWidth(0.5f), state = scroller) {
            val itemsCount = (movies?.size ?: 0) + (series?.size ?: 0)
            if (itemsCount == 0) {
                item {
                    Text("Loading...", Modifier.fillMaxWidth(), textAlign = TextAlign.Center, style = MaterialTheme.typography.titleMedium)
                }
            } else {
                items(itemsCount) { index ->
                    idx = index / 2
                    val movie = if (index % 2 == 0 && idx < (movies?.size ?: 0)) {
                        movies?.get(idx)
                    } else if (idx < (series?.size ?: 0)) {
                        series?.get(idx)
                    } else if (idx < (movies?.size ?: 0)) {
                        movies?.get(idx)
                    } else null

                    movie?.let {
                        Title(navController = navController, movie = it)
                    }
                }
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .width(2.dp)
                .background(MaterialTheme.colorScheme.borderVariant)
        )
        Column(modifier = Modifier.padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Keyboard()
            suggestionList?.take(4
            )?.forEach { item ->
                Button(
                    onClick = {
                        Node.setSearchString(item)
                    },
                    colors = ButtonDefaults.colors(containerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent),
                    border = ButtonDefaults.border(
                        focusedBorder = Border(BorderStroke(2.dp, MaterialTheme.colorScheme.border))
                    ),
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Text(
                        item,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        textAlign = TextAlign.Right,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

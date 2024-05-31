package com.example.mopera.ui.screens.SearchScreen

import MovieDescription
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
import com.example.mopera.api.SearchSuggestions.fetch
import com.example.mopera.model.Node
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope


fun String.insert(index: Int, charToInsert: Char): String {
    if (index == this.length) return this + charToInsert
    return StringBuilder(this).insert(index, charToInsert).toString()
}


@Composable
fun SearchScreen(navController: NavHostController) {
    var suggestionList by remember { mutableStateOf(listOf<String>()) }
    var movies by remember { mutableStateOf(listOf<MovieDescription>()) }
    var series by remember { mutableStateOf(listOf<MovieDescription>()) }
    val scroller = rememberTvLazyGridState()
    LaunchedEffect(Node.value) {
        val fetchedResults = coroutineScope {
            val fetchedSuggestionList = async(Dispatchers.IO) { fetch(Node.value) }
            val fetchedMovies = async(Dispatchers.IO) { fetchMovies(Node.value) }
            val fetchedSeries = async(Dispatchers.IO) { fetchSeries(Node.value) }
            Triple(
                fetchedSuggestionList.await(),
                fetchedMovies.await(),
                fetchedSeries.await()
            )
        }

        suggestionList = fetchedResults.first
        movies = fetchedResults.second
        series = fetchedResults.third

        scroller.scrollToItem(0)
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
            items(movies.size + series.size) { index ->
                if (index < movies.size + series.size) {
                    idx = index / 2
                    val movie = if (index % 2 == 0 && idx < movies.size) {
                        movies[idx]
                    }
                    else if (idx < series.size) {
                        series[idx]
                    }
                    else if (idx < movies.size) {
                        movies[idx]
                    }
                    else null

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
            suggestionList.take(3).forEach { item ->
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

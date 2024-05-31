package com.example.mopera.ui.screens.episodesScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.*
import com.example.mopera.api.EpisodesAPI
import com.example.mopera.data.Episode
import com.example.mopera.ui.components.ErrorScreen
import com.example.mopera.ui.components.LoadState
import com.example.mopera.ui.components.LoadingScreen
import kotlinx.coroutines.launch

@Composable
fun EpisodesUI(nb: Int, navController: NavController) {
    val viewModel = remember {   EpisodesViewModel() }
    var data by remember { mutableStateOf<Map<Int, List<Episode>>>(emptyMap()) }
    var state by remember { mutableStateOf(LoadState.LOADING) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                data = EpisodesAPI.fetch(nb)
                state = LoadState.SUCCESS
            } catch (e: Exception) {
                state = LoadState.ERROR
            }
        }
    }

    when (state) {
        LoadState.LOADING -> LoadingScreen()
        LoadState.SUCCESS -> EpisodesContent(seasons = data, navController = navController,
            viewModel=viewModel)
        LoadState.ERROR -> ErrorScreen(message = "Something wrong happened")
    }
}

@Composable
fun EpisodesContent(seasons: Map<Int, List<Episode>>, navController: NavController,
                    viewModel: EpisodesViewModel) {
    val gridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    var selectedReference by remember { mutableIntStateOf(0) }

    val groupedItems by remember(viewModel.selectedSeason) { derivedStateOf {
        seasons[viewModel.selectedSeason]?.chunked(100) ?: emptyList() } }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        TvLazyRow(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 200.dp)) {
            items(seasons.size) { season ->
                SeasonButton(season+1, viewModel)
            }
        }
        Row(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)) {
            TvLazyColumn(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                .width(200.dp)
                .padding(8.dp)) {
                item {
                    Text(text = "References", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onBackground)
                }
                items(groupedItems) { group ->
                    ReferenceButton(group.first().episodeNummer,
                        lastIndex = group.last().episodeNummer,
                        isSelected = viewModel.selectedEpisode in group.first().episodeNummer .. group.last().episodeNummer)
                    {
                        selectedReference = group.first().episodeNummer.toInt()
                        coroutineScope.launch {
                            gridState.scrollToItem(selectedReference)
                        }
                    }
                }
            }

            LazyVerticalGrid(columns = GridCells.Fixed(4), state = gridState, modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)) {
                items(groupedItems.flatten()) { item ->
                    EpisodeButton(item.episodeNummer, viewModel) {
                        navController.popBackStack()
                        navController.navigate("movie/${item.nb}")
                    }
                }
            }
        }
    }
}

@Composable
fun SeasonButton(season: Int, viewModel: EpisodesViewModel) {
    val focusRequester = remember { FocusRequester() }
    Button(
        onClick = { viewModel.selectedSeason = season },
        colors = ButtonDefaults.colors(containerColor = Color.Transparent, focusedContainerColor = Color.Black),
        shape = ButtonDefaults.shape(RoundedCornerShape(4.dp)),
        border = ButtonDefaults.border(if (viewModel.selectedSeason == season)
            Border(BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)) else Border.None),
        modifier = Modifier
            .padding(4.dp)
            .width(150.dp)
            .onFocusEvent { if (it.isFocused) {
                viewModel.selectedSeason = season
            }
            }
            .focusRequester(focusRequester)
    ) {
        Text(
            text = "Season $season",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
    if (season == 1) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}

@Composable
fun ReferenceButton(index: Short, lastIndex: Short, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.colors(containerColor = Color.Transparent, focusedContainerColor = Color.Black),
        shape = ButtonDefaults.shape(RoundedCornerShape(4.dp)),
        scale = ButtonDefaults.scale(scale = if (isSelected) 1.3f else 1f),
        border = ButtonDefaults.border(focusedBorder = Border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary))),
        modifier = Modifier
            .padding(4.dp)
            .width(150.dp)
            .onFocusEvent { if (it.isFocused) onClick() }
    ) {
        Text(
            text = "$index-$lastIndex",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun EpisodeButton(item: Short, viewModel: EpisodesViewModel, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.colors(containerColor = MaterialTheme.colorScheme.surface, focusedContainerColor = Color.Black),
        shape = ButtonDefaults.shape(RoundedCornerShape(4.dp)),
        border = ButtonDefaults.border(focusedBorder = Border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary))),
        modifier = Modifier
            .padding(4.dp)
            .size(75.dp, 40.dp)
            .onFocusEvent {
                if (it.isFocused)
                    viewModel.selectedEpisode = item
            }
    ) {
        Text(
            text = item.toString(),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

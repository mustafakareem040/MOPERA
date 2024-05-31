package com.example.mopera.ui.screens.EpisodesScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.material3.Border
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.mopera.api.EpisodesAPI
import com.example.mopera.data.Episode
import com.example.mopera.ui.components.ErrorScreen
import com.example.mopera.ui.components.LoadState
import com.example.mopera.ui.components.LoadingScreen
import kotlinx.coroutines.launch


@Composable
fun EpisodesUI(nb: Int, navController: NavController) {
    var data: Map<Int, List<Episode>> by remember { mutableMapOf() }
    val (state, setState) = remember { mutableStateOf(LoadState.LOADING) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                data = EpisodesAPI.fetch(nb)
                setState(LoadState.SUCCESS)
            } catch (e: Exception) {
                setState(LoadState.ERROR)
            }
        }
    }
    when (state) {
        LoadState.LOADING -> LoadingScreen()
        LoadState.SUCCESS -> EpisodesContent(seasons = data, navController = navController)
        LoadState.ERROR -> ErrorScreen(message = "Something wrong happened")
    }
}





@Composable
fun EpisodesContent(seasons: Map<Int, List<Episode>>, navController: NavController) {
    val gridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    var selectedSeason by remember(seasons) { mutableIntStateOf(if (seasons.isEmpty()) 0 else 1) }
    var selectedReference by remember { mutableIntStateOf(0) }

    val groupedItems = remember(selectedSeason) { seasons[selectedSeason]?.chunked(100) ?: emptyList() }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        TvLazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            items(seasons.size) { seasonIndex ->
                val isSelected = selectedSeason == seasonIndex + 1
                Button(
                    onClick = { /* Scroll to corresponding episodes */ },
                    colors = ButtonDefaults.colors(
                        containerColor = Color.Transparent,
                        focusedContainerColor = Color.Black
                    ),
                    shape = ButtonDefaults.shape(RoundedCornerShape(4.dp)),
                    border = ButtonDefaults.border(if (isSelected) {
                        Border(BorderStroke(2.dp, MaterialTheme.colorScheme.border))
                    } else {
                        Border.None
                    }),
                    modifier = Modifier
                        .padding(4.dp)
                        .width(150.dp)
                        .onFocusEvent {
                            if (it.isFocused) selectedSeason = seasonIndex + 1
                        }
                ) {
                    Text(
                        text = "Season ${seasonIndex + 1}",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
        Row(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(200.dp)
                    .padding(8.dp)
            ) {
                item {
                    Text(
                        text = "References",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
                itemsIndexed(groupedItems) { index, _ ->
                    ReferenceButton(
                        index = index,
                        isSelected = selectedReference == index,
                        onFocus = {
                            selectedReference = index
                            coroutineScope.launch {
                                gridState.animateScrollToItem(index * 100)
                            }
                        }
                    )
                }
            }

            // Episodes Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                state = gridState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                items(groupedItems.flatten()) { item ->
                    EpisodeButton(item.episodeNummer) {
                        navController.popBackStack()

                        navController.navigate("movie/${item.nb}")
                    }
                }
            }
        }
    }
}

@Composable
fun ReferenceButton(index: Int, isSelected: Boolean, onFocus: () -> Unit) {
    Button(
        onClick = { /* TODO: change focus to first corresponding episode */ },
        colors = ButtonDefaults.colors(
            containerColor = Color.Transparent,
            focusedContainerColor = Color.Black
        ),
        shape = ButtonDefaults.shape(RoundedCornerShape(4.dp)),
        border = ButtonDefaults.border(if (isSelected) {
            Border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary))
        } else {
            Border.None
        }),
        modifier = Modifier
            .padding(4.dp)
            .width(150.dp)
            .onFocusEvent {
                if (it.isFocused) onFocus()
            }
    ) {
        Text(
            text = "${index * 100}",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun EpisodeButton(item: Short, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = Color.Black
        ),
        shape = ButtonDefaults.shape(RoundedCornerShape(4.dp)),
        border = ButtonDefaults.border(
            focusedBorder = Border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary))
        ),
        modifier = Modifier
            .padding(4.dp)
            .size(75.dp, 40.dp)
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

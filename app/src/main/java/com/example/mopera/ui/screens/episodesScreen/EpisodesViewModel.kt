package com.example.mopera.ui.screens.episodesScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class EpisodesViewModel {
    var selectedSeason by mutableIntStateOf(1)
    var selectedEpisode: Short by mutableStateOf(1)
}
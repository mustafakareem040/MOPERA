package com.example.mopera

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import com.example.mopera.api.MediaDetailsAPI
import com.example.mopera.api.VideoFormatAPI
import com.example.mopera.data.Media
import com.example.mopera.ui.screens.episodesScreen.EpisodesUI
import com.example.mopera.ui.components.LoadState
import com.example.mopera.ui.components.LoadingScreen
import com.example.mopera.ui.theme.MOPERATheme
import com.example.mopera.ui.player.VideoPlayerScreenContent
import com.example.mopera.ui.screens.searchScreen.SearchScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MOPERATheme {
                MOPERAApp()
            }
        }
    }

    @Composable
    fun MOPERAApp() {
        var showSplashScreen by remember { mutableStateOf(true) }
        val navController = rememberNavController()

        LaunchedEffect(showSplashScreen) {
            delay(2000)
            showSplashScreen = false
        }
        MOPERATheme {
                if (showSplashScreen) {
                    SplashScreen()
                } else {
                    MainScreen(navController)
                }
        }
    }

    @Composable
    fun SplashScreen() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.banner),
                contentDescription = "MOPERA banner"
            )
        }
    }

    @Composable
    fun MainScreen(navController: NavHostController) {
        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SearchScreen(navController = navController)
                }
            }
            composable(
                route = "series/{nb}",
                arguments = listOf(navArgument("nb") { type = NavType.IntType })
            ) {
                val key = it.arguments?.getInt("nb") ?: return@composable
                Surface(modifier = Modifier.fillMaxSize()) {
                    EpisodesUI(nb = key, navController = navController)
                }
            }
            composable(
                route = "movie/{nb}",
                arguments = listOf(navArgument("nb") { type = NavType.IntType })
            ) {
                val key = it.arguments?.getInt("nb") ?: return@composable
                var movie by remember { mutableStateOf<Media?>(null) }
                val state = remember(movie) {
                    if (movie == null) LoadState.LOADING else LoadState.SUCCESS}
                LaunchedEffect(key) {
                    movie = fetchMovieData(key)
                }
                if (state == LoadState.LOADING) LoadingScreen()
                else {
                    movie?.let { movieData ->
                        VideoPlayerScreenContent(movieData) {
                            navController.popBackStack()
                            if (movieData.details.kind == "1")
                                navController.navigate("home")
                            else navController.navigate("series/$key")
                        }
                    }
                }
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val config = Configuration(newBase.resources.configuration)
        val locale = Locale("en")
        Locale.setDefault(locale)
        config.setLayoutDirection(locale)
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    private suspend fun fetchMovieData(key: Int): Media? {
        return withContext(Dispatchers.IO) {
            coroutineScope {
                val detailsDeferred = async { MediaDetailsAPI.fetch(key) }
                val formatsDeferred = async { VideoFormatAPI.fetch(key) }
                val detailData = detailsDeferred.await()
                detailData?.run { Media(detailData, formatsDeferred.await()) }
            }
        }
    }
}
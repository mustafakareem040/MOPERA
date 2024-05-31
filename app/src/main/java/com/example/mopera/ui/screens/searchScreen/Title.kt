package com.example.mopera.ui.screens.SearchScreen

import MovieDescription
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.CompactCard
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.mopera.R
import com.example.mopera.api.MediaDetailsAPI
import com.example.mopera.api.Utilities
import com.example.mopera.data.MediaType
import kotlinx.coroutines.launch

@Composable
fun ImageCard(url: String) {
    val context = LocalContext.current
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(url)
            .placeholder(R.drawable.spinner)
            .crossfade(true)
            .allowHardware(true)
            .error(R.drawable.picture_failed_to_load)
            .build()
    )

    Image(
        painter = painter,
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier.width(220.dp).height(200.dp)
    )
}

@Composable
fun Title(movie: MovieDescription, navController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    CompactCard(
        onClick = {
            coroutineScope.launch {
                val details = MediaDetailsAPI.fetch(movie.nb.toInt())
                if (details == null) {
                    Toast.makeText(context, "Show doesn't exist", Toast.LENGTH_LONG).show()
                } else {
                    navController.navigate("movie/${movie.nb}")
                }
            }
        },
        subtitle = {
                   Row {
                       Row(
                           modifier = Modifier
                               .size(200.dp, 24.dp)
                               .padding(horizontal = 8.dp),
                           horizontalArrangement = Arrangement.SpaceBetween,
                           verticalAlignment = Alignment.CenterVertically
                       ) {
                           Text(text = "⭐${movie.stars}", color =  MaterialTheme.colorScheme.onSurfaceVariant)
                           Text(text = if (movie.kind == MediaType.MOVIE) "Movie" else "Series", color = MaterialTheme.colorScheme.onSurfaceVariant)
                           Text(text = movie.year, color = MaterialTheme.colorScheme.onSurfaceVariant)
                           Text(text = if (movie.kind == MediaType.MOVIE) Utilities.convert(movie.duration) else "S${movie.season}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                       }
                   }
        },
        image = { ImageCard(movie.imgMediumThumbObjUrl) },
        title = {
            Text(
                text = movie.en_title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.widthIn(max = 120.dp).padding(horizontal = 8.dp)
            )
        },
        scale = CardDefaults.scale(focusedScale = 1.035f),
        colors = CardDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier
            .background(Color.Transparent)
            .padding(8.dp)
    )
}
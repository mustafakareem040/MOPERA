package com.example.mopera.ui.components

import MovieDescription
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Text
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.mopera.R
import com.example.mopera.api.MediaDetailsAPI
import com.example.mopera.api.Utilities.convert
import com.example.mopera.data.MediaType
import com.example.mopera.ui.theme.BLUE50
import com.example.mopera.ui.theme.RED30
import com.example.mopera.ui.theme.WHITESMOKE
import kotlinx.coroutines.runBlocking


@Composable
fun ImageCard(url: String) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .placeholder(R.drawable.spinner)
            .crossfade(500)
            .error(R.drawable.picture_failed_to_load)
            .build()
    )
    Image(
        painter = painter, contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier= Modifier
            .size(200.dp, 300.dp)
            .alpha(0.9f)
    )
}
@Composable
fun Title(movie: MovieDescription, navController: NavHostController) {
    val context  = LocalContext.current
    Card(onClick = {
        runBlocking {
            val d = MediaDetailsAPI.fetch(movie.nb.toInt())
            if (d == null) Toast.makeText(context, "Show doesn't exist", Toast.LENGTH_LONG)
                .show()
            else navController.navigate("movie/${movie.nb}")
        }},
        scale = CardDefaults.scale(focusedScale = 1.035f),
        colors = CardDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier
            .wrapContentWidth()
            .background(Color.Transparent)
            .padding(10.dp)) {
        Box(
        contentAlignment = Alignment.BottomStart) {
            ImageCard(movie.imgMediumThumbObjUrl)
            Text(movie.en_title, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black,
                modifier = Modifier
                    .width(150.dp)
                    .padding(5.dp),
                style = TextStyle(shadow =
                    Shadow(
                        color = Color.Black,
                        offset = Offset(4f, 4f),
                        blurRadius = 8f
                    )
                )
            )
        }
        Row(
            modifier = Modifier
                .size(200.dp, 24.dp)
                .background(
                    if (movie.kind == MediaType.MOVIE) BLUE50 else RED30
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "⭐${movie.stars}", color = WHITESMOKE)
            Text(text = if (movie.kind == MediaType.MOVIE) "Movie" else "Series", color = WHITESMOKE)
            Text(text = movie.year, color = WHITESMOKE)
            Text(text = if (movie.kind == MediaType.MOVIE) convert(movie.duration) else "S${movie.season}", color = WHITESMOKE)
        }
    }
}
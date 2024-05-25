package com.example.mopera.ui.components

import MovieDescription
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Text
import com.example.mopera.R
import com.example.mopera.api.MediaSearchResult.fetchMovies
import com.example.mopera.api.MediaSearchResult.fetchSeries
import com.example.mopera.api.SearchSuggestions.fetch
import com.example.mopera.ui.assets.KeyButton
import com.example.mopera.ui.assets.KeyIcon
import com.example.mopera.ui.model.KeysModel
import com.example.mopera.ui.model.Node
import com.example.mopera.ui.theme.BLUE40
import com.example.mopera.ui.theme.DARK80
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope


fun String.insert(index: Int, charToInsert: Char): String {
    if (index == this.length) return this + charToInsert
    return StringBuilder(this).insert(index, charToInsert).toString()
}




@Composable
fun Keyboard() {
    Column {
        Text(
            text = Node.value,
            modifier = Modifier
                .padding(12.dp)
                .width(460.dp),
            color = Color.White,
            style = TextStyle(
                fontSize = 26.sp,
                textDirection = if (Node.isArabic) TextDirection.Rtl else TextDirection.Ltr
            )
        )
        Spacer(modifier = Modifier.height(2.dp).width(460.dp).background(BLUE40))

        val keyRows = if (Node.isArabic) {
            listOf(KeysModel.ROW1_AR, KeysModel.ROW2_AR, KeysModel.ROW3_AR, KeysModel.ROW4_AR)
        } else {
            listOf(KeysModel.ROW1, KeysModel.ROW2, KeysModel.ROW3, KeysModel.ROW4)
        }
        keyRows.forEachIndexed { index, keyRow ->
            Row {
                if (index < keyRows.size - 1) {
                    Row {
                        keyRow.forEach { letter ->
                            KeyButton(letter = letter, onKeyPressed = Node::addChar)
                        }
                    }
                } else {
                    keyRow.forEach { letter ->
                        KeyButton(letter = letter, onKeyPressed = Node::addChar)
                    }
                    KeyIcon(
                        painter = painterResource(id = R.drawable.switch_language),
                        contentDescription = "language",
                        40.dp,
                        Node::changeLanguage
                    )
                    KeyIcon(
                        painter = painterResource(id = R.drawable.backspace),
                        contentDescription = "backspace",
                        onKeyPressed = Node::backspace
                    )
                }
            }
        }
        Row {
            KeyIcon(
                painter = painterResource(id = R.drawable.arrow_left),
                contentDescription = "arrow_left",
                40.dp,
                Node::subIndex
            )
            KeyIcon(
                painter = painterResource(id = R.drawable.arrow_right),
                contentDescription = "arrow_right",
                onKeyPressed = Node::incIndex
            )
            KeyIcon(
                painter = painterResource(id = R.drawable.space),
                contentDescription = "space",
                width = 286.dp,
                onKeyPressed = Node::addSpace
            )
            KeyIcon(
                vector = Icons.AutoMirrored.Default.ArrowForward,
                contentDescription = "Search",
                width = 80.dp,
                onKeyPressed = Node::search
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun KeyboardButtons(navController: NavHostController) {
    var suggestionList by remember { mutableStateOf(listOf<String>()) }
    var movies by remember { mutableStateOf(listOf<MovieDescription>()) }
    var series by remember { mutableStateOf(listOf<MovieDescription>()) }
    val scroller1 = rememberLazyListState()
    val scroller2 = rememberLazyListState()

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

        // Update state with the fetched results
        suggestionList = fetchedResults.first
        movies = fetchedResults.second
        series = fetchedResults.third

        // Scroll to the top after data has been fetched
        scroller1.scrollToItem(0)
        scroller2.scrollToItem(0)
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(DARK80),
        horizontalArrangement = Arrangement.Absolute.Left
    ) {
        Row(
            modifier = Modifier.wrapContentWidth(),
            horizontalArrangement = Arrangement.Absolute.Left
        ) {
            LazyColumn(modifier = Modifier.wrapContentWidth(), state = scroller1) {
                items(movies.size) { index ->
                    Title(navController = navController, movie = movies[index])
                }
            }
            LazyColumn(modifier = Modifier.wrapContentWidth(), state = scroller2) {
                items(series.size) { index ->
                    Title(navController = navController, movie = series[index])
                }
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .width(2.dp)
                .background(Color(0xFF36454F))
        )
        Column(modifier = Modifier.padding(5.dp)) {
            Keyboard()
            suggestionList.take(3).forEach { item ->
                Button(
                    onClick = {
                        Node.setSearchString(item)
                    },
                    colors = ButtonDefaults.colors(containerColor = Color.Transparent)
                ) {
                    Text(
                        item,
                        color = Color.White,
                        textAlign = TextAlign.Right,
                        fontSize = 24.sp
                    )
                }
            }
        }
    }
}

package com.example.mopera.ui.assets

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.Text
import com.example.mopera.ui.theme.DARK70
import com.example.mopera.ui.theme.DARK90
import kotlin.reflect.KFunction0

@Stable
@Composable
inline fun KeyButton(letter: Char, crossinline onKeyPressed: (Char) -> Unit) {
    IconButton(
        onClick = {
            onKeyPressed(letter)  },
        colors = ButtonDefaults.colors(
            containerColor = DARK70,
            contentColor = Color.White,
            focusedContainerColor = DARK90,
        ),
        modifier = Modifier
            .padding(4.dp)
            .width(40.dp)


    ) {
        Text(text = letter.toString(),
        )
    }
}
@Stable
@Composable
fun KeyIcon(
    painter: Painter, contentDescription: String, width: Dp = 40.dp,
    onKeyPressed: KFunction0<Unit>
) {
    IconButton(
        onClick = onKeyPressed,
        colors = ButtonDefaults.colors(
            containerColor = DARK70,
            contentColor = Color.White,
            focusedContainerColor = DARK90,
        ),
        modifier = Modifier
            .padding(4.dp)
            .width(width)
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
        )
    }
}
@Stable
@Composable
fun KeyIcon(vector: ImageVector, contentDescription: String, width: Dp = 40.dp,
                   onKeyPressed: KFunction0<Unit>) {
    IconButton(
        onClick = onKeyPressed,
        colors = ButtonDefaults.colors(
            containerColor = DARK70,
            contentColor = Color.White,
            focusedContainerColor = DARK90,
        ),
        modifier = Modifier
            .padding(4.dp)
            .width(width)
    ) {
        Icon(
            imageVector = vector,
            contentDescription = contentDescription
        )


    }
}
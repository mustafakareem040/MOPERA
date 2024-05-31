package com.example.mopera.ui.screens.searchScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.mopera.R
import com.example.mopera.model.Node

@Composable
fun Keyboard() {
    Text(
        text = Node.value,
        modifier = Modifier
            .padding(12.dp)
            .width(410.dp),
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.titleMedium.copy(
            textDirection = if (Node.isArabic) TextDirection.Rtl else TextDirection.Ltr
        )
    )
    Spacer(
        modifier = Modifier
            .height(2.dp)
            .width(410.dp)
            .background(MaterialTheme.colorScheme.border)
    )


    Node.getRows().forEachIndexed { index, keyRow ->
        if (index < 3) {
            Row {
                keyRow.forEach {
                    KeyButton(letter = it, onKeyPressed = Node::addChar)
                }
            }
        } else {
            Row {
                keyRow.forEach {
                    KeyButton(letter = it, onKeyPressed = Node::addChar)
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
            width = 266.dp,
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
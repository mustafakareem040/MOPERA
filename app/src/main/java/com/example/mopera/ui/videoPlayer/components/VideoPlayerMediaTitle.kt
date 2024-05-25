package com.example.mopera.ui.videoPlayer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.mopera.R

enum class VideoPlayerMediaTitleType { AD, LIVE, DEFAULT }

@Stable
@Composable
fun VideoPlayerMediaTitle(
    title: String,
    secondaryText: String,
    tertiaryText: String,
    modifier: Modifier = Modifier,
    type: VideoPlayerMediaTitleType = VideoPlayerMediaTitleType.DEFAULT
) {
    val subTitle = buildString {
        append(secondaryText)
        if (secondaryText.isNotEmpty() && tertiaryText.isNotEmpty()) append(" • ")
        append(tertiaryText)
    }
    Column(modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.headlineMedium
        , color = Color.White)
        Spacer(Modifier.height(4.dp))
        Row {
            when (type) {
                VideoPlayerMediaTitleType.AD -> {
                    Text(
                        text = stringResource(R.string.ad),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        modifier = Modifier
                            .background(Color(0xFFFBC02D), shape = RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                            .alignByBaseline()
                    )
                    Spacer(Modifier.width(8.dp))
                }

                VideoPlayerMediaTitleType.LIVE -> {
                    Text(
                        text = stringResource(R.string.live),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.inverseSurface,
                        modifier = Modifier
                            .background(Color(0xFFCC0000), shape = RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                            .alignByBaseline()
                    )

                    Spacer(Modifier.width(8.dp))
                }

                VideoPlayerMediaTitleType.DEFAULT -> {}
            }

            Text(
                text = subTitle,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                modifier = Modifier.alignByBaseline()
            )
        }
    }
}

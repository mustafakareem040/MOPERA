package com.example.mopera.ui.VideoPlayer

import android.net.Uri
import android.util.TypedValue.COMPLEX_UNIT_SP
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesomeMotion
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.CaptionStyleCompat
import androidx.media3.ui.PlayerView
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.mopera.data.Media
import com.example.mopera.utils.handleDPadKeyEvents
import com.example.mopera.ui.VideoPlayer.components.VideoPlayerControlsIcon
import com.example.mopera.ui.VideoPlayer.components.VideoPlayerMainFrame
import com.example.mopera.ui.VideoPlayer.components.VideoPlayerMediaTitle
import com.example.mopera.ui.VideoPlayer.components.VideoPlayerOverlay
import com.example.mopera.ui.VideoPlayer.components.VideoPlayerPulse
import com.example.mopera.ui.VideoPlayer.components.VideoPlayerPulseState
import com.example.mopera.ui.VideoPlayer.components.VideoPlayerQualityOptions
import com.example.mopera.ui.VideoPlayer.components.VideoPlayerSeeker
import com.example.mopera.ui.VideoPlayer.components.VideoPlayerSettingsMenuState
import com.example.mopera.ui.VideoPlayer.components.VideoPlayerState
import com.example.mopera.ui.VideoPlayer.components.VideoPlayerSubtitlesOptions
import com.example.mopera.ui.VideoPlayer.components.rememberVideoPlayerPulseState
import com.example.mopera.ui.VideoPlayer.components.rememberVideoPlayerState
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds


@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerScreenContent(mediaDetails: Media, onBackPressed: () -> Unit) {
    val context = LocalContext.current
    val videoPlayerState = rememberVideoPlayerState(hideSeconds = 4)
    val controlsFocusRequester = remember { FocusRequester() }
    val skipIntroFocusRequester = remember { FocusRequester() }
    val trackers = remember { mutableListOf<MediaItem.SubtitleConfiguration>() }
    val databaseProvider: DatabaseProvider = remember { StandaloneDatabaseProvider(context) }
    val simpleCache = remember {
        SimpleCache(context.cacheDir, LeastRecentlyUsedCacheEvictor(300 * 1024 * 1024), databaseProvider)
    }
    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setLoadControl(DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                    30000,
                    150000,
                    1500,
                    3000
                ).build())
            .setMediaSourceFactory(DefaultMediaSourceFactory(CacheDataSource.Factory()
                .setCache(simpleCache)
                .setUpstreamDataSourceFactory(DefaultDataSource.Factory(context))
                .setCacheWriteDataSinkFactory(null)))
            .build().apply {
                playWhenReady = true
                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
            }
    }

    LaunchedEffect(Unit) {
        mediaDetails.details.translations?.forEach { item ->
            if (trackers.any { it.label == item.name }) return@forEach
            val mimeType = when (item.extention) {
                "srt" -> MimeTypes.APPLICATION_SUBRIP
                "vtt" -> MimeTypes.TEXT_VTT
                "ass" -> MimeTypes.TEXT_SSA
                else -> MimeTypes.TEXT_UNKNOWN
            }
            trackers.add(
                MediaItem.SubtitleConfiguration.Builder(Uri.parse(item.file))
                    .setMimeType(mimeType)
                    .setLanguage(item.type)
                    .setLabel(item.name)
                    .setId(item.id.toString())
                    .setRoleFlags(C.ROLE_FLAG_SUBTITLE)
                    .build()
            )
        }

        val mediaItem = MediaItem.Builder()
            .setUri(mediaDetails.formats.last().videoUrl)
            .setSubtitleConfigurations(trackers)
            .build()

        exoPlayer.setMediaItem(mediaItem)
        VideoPlayerSettingsMenuState.selectedTrack = trackers.lastOrNull()
        VideoPlayerSettingsMenuState.selectedQuality = mediaDetails.formats.last()
        exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
            .buildUpon()
            .setPreferredTextLanguage(VideoPlayerSettingsMenuState.selectedTrack?.language)
            .setPreferredVideoMimeType(MimeTypes.VIDEO_MP4)
            .setForceHighestSupportedBitrate(true)
            .build()
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
        videoPlayerState.showControls()
    }

    var contentCurrentPosition by remember { mutableLongStateOf(0L) }
    var isPlaying by remember { mutableStateOf(exoPlayer.isPlaying) }
    val introSkippingList = remember { mediaDetails.details.introSkipping }
    val showSkipButton = remember {
        derivedStateOf {
            introSkippingList?.any { skip ->
                contentCurrentPosition in (skip.start * 1000).toLong()..(skip.end * 1000).toLong()
            } ?: false
        }
    }

    val skipEndTime by remember {
        derivedStateOf {
            introSkippingList?.find { skip ->
                contentCurrentPosition in (skip.start * 1000).toLong()..(skip.end * 1000).toLong()
            }?.end?.toLong()?.times(1000) ?: 0L
        }
    }

    LaunchedEffect(exoPlayer) {
        while (true) {
            delay(200)
            contentCurrentPosition = exoPlayer.currentPosition
            isPlaying = exoPlayer.isPlaying
        }
    }

    BackHandler(onBack = onBackPressed)

    val pulseState = rememberVideoPlayerPulseState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .dPadEvents(exoPlayer, videoPlayerState, pulseState)
    ) {
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    useController = false
                    player = exoPlayer
                    subtitleView?.setBackgroundColor(0x00000000)
                    subtitleView?.setFixedTextSize(COMPLEX_UNIT_SP, 40f)
                    subtitleView?.setStyle(
                        CaptionStyleCompat(
                            0xFFFFFFFF.toInt(),
                            0x00000000,
                            0x00000000,
                            CaptionStyleCompat.EDGE_TYPE_DROP_SHADOW,
                            0xFF000000.toInt(),
                            null
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxSize().background(Color.Black),
            update = {
                it.player = exoPlayer
            },
            onRelease = {
                exoPlayer.release()
                simpleCache.release()
            }
        )

        VideoPlayerOverlay(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .focusable()
                .dPadEvents(exoPlayer, videoPlayerState, pulseState),
            state = videoPlayerState,
            focusRequester = controlsFocusRequester,
            centerButton = { VideoPlayerPulse(pulseState) },
            subtitles = {},
            controls = {
                VideoPlayerControls(
                    mediaDetails,
                    isPlaying,
                    contentCurrentPosition,
                    exoPlayer,
                    videoPlayerState,
                    controlsFocusRequester
                )
            }
        )

        if (VideoPlayerSettingsMenuState.showSubtitlesControls) {
            exoPlayer.pause()
            videoPlayerState.showControls(Int.MAX_VALUE)
            VideoPlayerSubtitlesOptions(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(20.dp, 2.dp, 20.dp, 0.dp),
                tracks = trackers,
                focusRequester = controlsFocusRequester,
                onTrackSelected = { track ->
                    exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                        .buildUpon()
                        .setPreferredTextLanguage(track?.language)
                        .build()
                    videoPlayerState.showControls(Int.MAX_VALUE)
                }
            )
        } else videoPlayerState.hideControls()

        if (VideoPlayerSettingsMenuState.showQualityControls) {
            exoPlayer.pause()
            videoPlayerState.showControls(Int.MAX_VALUE)
            VideoPlayerQualityOptions(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(20.dp, 2.dp, 20.dp, 0.dp),
                formats = mediaDetails.formats,
                focusRequester = controlsFocusRequester,
                onFormatSelected = { format ->
                    val currentMediaItem = exoPlayer.currentMediaItem ?: return@VideoPlayerQualityOptions
                    val currentPosition = exoPlayer.currentPosition
                    val newMediaItem = currentMediaItem.buildUpon()
                        .setUri(format.videoUrl)
                        .build()
                    exoPlayer.setMediaItem(newMediaItem)
                    exoPlayer.seekTo(currentPosition)
                    exoPlayer.prepare()
                    videoPlayerState.showControls(Int.MAX_VALUE)
                }
            )
        }

        if (showSkipButton.value) {
            Button(
                onClick = {
                    exoPlayer.seekTo(skipEndTime)
                    exoPlayer.play()
                },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(top = 140.dp, start = 50.dp)
                    .focusRequester(skipIntroFocusRequester)
                    .handleDPadKeyEvents(
                        onDown = {
                            if (videoPlayerState.controlsVisible.value)
                                controlsFocusRequester.requestFocus()
                            else videoPlayerState.showControls()
                        },
                        onRight = {
                            if (videoPlayerState.controlsVisible.value)
                                controlsFocusRequester.requestFocus()
                            else videoPlayerState.showControls()
                        }
                    ),
                shape = ButtonDefaults.shape(
                    shape = RoundedCornerShape(4.dp),
                    focusedShape = RoundedCornerShape(4.dp)
                )
            ) {
                Text(
                    text = "Skip Intro",
                    fontSize = 21.sp
                )
            }
            LaunchedEffect(Unit) {
                skipIntroFocusRequester.requestFocus()
            }
        }
    }
}



@Composable
fun VideoPlayerControls(
    movieDetails: Media,
    isPlaying: Boolean,
    contentCurrentPosition: Long,
    exoPlayer: ExoPlayer,
    state: VideoPlayerState,
    focusRequester: FocusRequester
) {
    val onPlayPauseToggle = { shouldPlay: Boolean ->
        if (shouldPlay) {
            exoPlayer.play()
            if (state.controlsVisible.value)
                state.showControls()
            else
                state.hideControls()
        } else {
            exoPlayer.pause()
            state.showControls(Int.MAX_VALUE)
        }
    }

    VideoPlayerMainFrame(
        mediaTitle = {
            VideoPlayerMediaTitle(
                title = movieDetails.details.enTitle,
                secondaryText = movieDetails.details.year,
                tertiaryText = movieDetails.details.arTitle
            )
        },
        mediaActions = {
            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                VideoPlayerControlsIcon(
                    icon = Icons.Default.AutoAwesomeMotion,
                    contentDescription = ""
                )
                VideoPlayerControlsIcon(
                    modifier = Modifier.padding(start = 12.dp),
                    icon = Icons.Default.ClosedCaption,
                    contentDescription = "",
                    onClick = {
                        VideoPlayerSettingsMenuState.showSubtitlesControls = !VideoPlayerSettingsMenuState.showSubtitlesControls
                    }
                )
                VideoPlayerControlsIcon(
                    modifier = Modifier.padding(start = 12.dp),
                    icon = Icons.Default.Settings,
                    contentDescription = "",
                    onClick = {
                        VideoPlayerSettingsMenuState.showQualityControls = !VideoPlayerSettingsMenuState.showQualityControls
                    }
                )
            }
        },
        seeker = {
            VideoPlayerSeeker(
                focusRequester,
                state,
                isPlaying,
                onPlayPauseToggle,
                contentProgress = contentCurrentPosition.milliseconds,
                contentDuration = exoPlayer.duration.milliseconds
            )
        },
        more = null
    )
}

@OptIn(UnstableApi::class)
private fun Modifier.dPadEvents(
    exoPlayer: ExoPlayer,
    videoPlayerState: VideoPlayerState,
    pulseState: VideoPlayerPulseState
): Modifier = this.handleDPadKeyEvents(
    onLeft = {
        if (videoPlayerState.controlsVisible.value) {
            videoPlayerState.showControls()
            return@handleDPadKeyEvents
        }
        exoPlayer.seekBack()
        pulseState.setType(VideoPlayerPulse.Type.BACK)
    },
    onRight = {
        if (videoPlayerState.controlsVisible.value) {
            videoPlayerState.showControls()
            return@handleDPadKeyEvents
        }
        exoPlayer.seekForward()
        pulseState.setType(VideoPlayerPulse.Type.FORWARD)
    },
    onUp = { videoPlayerState.showControls() },
    onDown = { videoPlayerState.showControls() },
    onEnter = {
        if (!videoPlayerState.controlsVisible.value) {
            exoPlayer.pause()
            videoPlayerState.showControls(Int.MAX_VALUE)
        }
        else {
            exoPlayer.play()
            videoPlayerState.showControls()
        }
    }
)


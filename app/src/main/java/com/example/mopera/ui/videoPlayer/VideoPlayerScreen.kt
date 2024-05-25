package com.example.mopera.ui.videoPlayer

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
import androidx.media3.exoplayer.LoadControl
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.CaptionStyleCompat
import androidx.media3.ui.PlayerView
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Text
import com.example.mopera.data.Media
import com.example.mopera.ui.theme.DARK80
import com.example.mopera.ui.videoPlayer.components.VideoPlayerControlsIcon
import com.example.mopera.ui.videoPlayer.components.VideoPlayerMainFrame
import com.example.mopera.ui.videoPlayer.components.VideoPlayerMediaTitle
import com.example.mopera.ui.videoPlayer.components.VideoPlayerMediaTitleType
import com.example.mopera.ui.videoPlayer.components.VideoPlayerOverlay
import com.example.mopera.ui.videoPlayer.components.VideoPlayerPulse
import com.example.mopera.ui.videoPlayer.components.VideoPlayerPulseState
import com.example.mopera.ui.videoPlayer.components.VideoPlayerQualityOptions
import com.example.mopera.ui.videoPlayer.components.VideoPlayerSeeker
import com.example.mopera.ui.videoPlayer.components.VideoPlayerSettingsMenuState
import com.example.mopera.ui.videoPlayer.components.VideoPlayerState
import com.example.mopera.ui.videoPlayer.components.VideoPlayerSubtitlesOptions
import com.example.mopera.ui.videoPlayer.components.rememberVideoPlayerPulseState
import com.example.mopera.ui.videoPlayer.components.rememberVideoPlayerState
import com.example.mopera.ui.utils.handleDPadKeyEvents
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
    val cacheSize: Long = 100 * 1024 * 1024 // 100MB
    val databaseProvider: DatabaseProvider = StandaloneDatabaseProvider(context)
    val simpleCache = remember {
        SimpleCache(context.cacheDir, LeastRecentlyUsedCacheEvictor(cacheSize), databaseProvider)
    }

    val cacheDataSourceFactory = CacheDataSource.Factory()
        .setCache(simpleCache)
        .setUpstreamDataSourceFactory(DefaultDataSource.Factory(context))
        .setCacheWriteDataSinkFactory(null)

    val loadControl: LoadControl = DefaultLoadControl.Builder()
        .setBufferDurationsMs(
            DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
            DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
            DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
            DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
        )
        .build()

    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setLoadControl(loadControl)
            .setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSourceFactory))
            .build()
    }


    LaunchedEffect(exoPlayer, mediaDetails) {
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
                    .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                    .build()
            )
        }

        val mediaItem = MediaItem.Builder()
            .setUri(mediaDetails.formats.last().videoUrl)
            .setSubtitleConfigurations(trackers)
            .build()

        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
            .buildUpon()
            .setPreferredTextLanguage(trackers.lastOrNull()?.language)
            .setPreferredVideoMimeType(MimeTypes.VIDEO_MP4)
            .setForceHighestSupportedBitrate(true)
            .build()

        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
        videoPlayerState.showControls()
    }

    var contentCurrentPosition by remember { mutableLongStateOf(0L) }
    var isPlaying by remember { mutableStateOf(exoPlayer.isPlaying) }
    val introSkippingList = mediaDetails.details.introSkipping
    val showSkipButton = remember {
        derivedStateOf {
            introSkippingList?.any { skip ->
                contentCurrentPosition in (skip.start.toDouble() * 1000).toLong()..(skip.end.toDouble() * 1000).toLong()
            } ?: false
        }
    }

    val skipEndTime by remember {
        derivedStateOf {
            introSkippingList?.find { skip ->
                contentCurrentPosition in (skip.start.toDouble() * 1000).toLong()..(skip.end.toDouble() * 1000).toLong()
            }?.end?.toDouble()?.toLong()?.times(1000) ?: 0L
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
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
            VideoPlayerSubtitlesOptions(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxHeight()
                    .background(DARK80)
                    .padding(20.dp, 2.dp, 20.dp, 0.dp),
                tracks = trackers,
                onTrackSelected = { track ->
                    val currentPosition = exoPlayer.currentPosition
                    val playWhenReady = exoPlayer.playWhenReady
                    exoPlayer.seekTo(currentPosition)
                    exoPlayer.playWhenReady = playWhenReady
                    exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                        .buildUpon()
                        .setPreferredTextLanguage(track.language)
                        .build()
                    exoPlayer.prepare()
                }
            )
        }

        if (VideoPlayerSettingsMenuState.showQualityControls) {
            VideoPlayerQualityOptions(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxHeight()
                    .background(DARK80)
                    .padding(20.dp, 2.dp, 20.dp, 0.dp),
                formats = mediaDetails.formats,
                onFormatSelected = { format ->
                    val currentMediaItem = exoPlayer.currentMediaItem ?: return@VideoPlayerQualityOptions
                    val currentPosition = exoPlayer.currentPosition
                    val playWhenReady = exoPlayer.playWhenReady
                    val newMediaItem = currentMediaItem.buildUpon()
                        .setUri(format.videoUrl)
                        .build()
                    exoPlayer.setMediaItem(newMediaItem)
                    exoPlayer.seekTo(currentPosition)
                    exoPlayer.playWhenReady = playWhenReady
                    exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                        .buildUpon()
                        .build()
                    exoPlayer.prepare()
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
                            skipIntroFocusRequester.freeFocus()
                            if (videoPlayerState.controlsVisible)
                                controlsFocusRequester.requestFocus()
                            else videoPlayerState.showControls()
                        },
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
            if (state.controlsVisible)
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
                tertiaryText = movieDetails.details.arTitle,
                type = VideoPlayerMediaTitleType.DEFAULT
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
    onLeft = {if (videoPlayerState.controlsVisible) return@handleDPadKeyEvents
        exoPlayer.seekBack()
        pulseState.setType(VideoPlayerPulse.Type.BACK)
    },
    onRight = {
        if (videoPlayerState.controlsVisible) return@handleDPadKeyEvents
        exoPlayer.seekForward()
        pulseState.setType(VideoPlayerPulse.Type.FORWARD)
    },
    onUp = { videoPlayerState.showControls() },
    onDown = { videoPlayerState.showControls() },
    onEnter = {
        if (!videoPlayerState.controlsVisible) {
            exoPlayer.pause()
            videoPlayerState.showControls(Int.MAX_VALUE)
        }
        else {
            exoPlayer.play()
            videoPlayerState.showControls()
        }
    }
)


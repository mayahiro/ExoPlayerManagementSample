package xyz.mayahiro.exoplayermanagementsample.player

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.util.Log
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlin.math.round

object PlayerManager {
    interface PlayerEventListener {
        fun onRemainingTimeChange(remainingTimeSec: Long)
        fun onStateEnded()
    }

    private val handler = Handler()
    private var enableHandler = false

    // pool
    private val playerStateMap = hashMapOf<String, PlayerState>()
    private val playerEventListeners = hashMapOf<String, ArrayList<PlayerEventListener>>()

    // get
    fun getPlayer(context: Context, uri: String, volume: Float = 0f, loop: Boolean = true, listener: PlayerEventListener? = null): Player {
        val key = uriToKey(uri)
        if (!playerStateMap.containsKey(key)) {
            val player = createPlayer(context, uri).also {
                it.playWhenReady = false
                it.repeatMode = if (loop) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF
                it.volume = volume
            }

            playerStateMap[key] = PlayerState(player, 0, PlayerState.State.PAUSE, volume, loop)

            playerEventListeners[key] = arrayListOf()
        }

        val playerState = playerStateMap[key]
        playerState!!.referenceCount++

        listener?.let { playerEventListeners[key]!!.add(it) }

        if (!enableHandler) {
            enableHandler = true
            handler.postDelayed({ updatePlayerLastPosition() }, 250)
        }

        return playerState.player
    }

    // play
    fun playPlayer(uri: String) {
        val key = uriToKey(uri)
        playerStateMap[key]?.let {
            it.player.playWhenReady = true
            it.state = PlayerState.State.PLAY
        }
    }

    fun playAllPlayer() {
        playerStateMap.forEach {
            if (it.value.state == PlayerState.State.PLAY) {
                it.value.player.playWhenReady = true
            }
        }

        if (!enableHandler) {
            enableHandler = true
            handler.postDelayed({ updatePlayerLastPosition() }, 250)
        }
    }

    // pause
    fun pausePlayer(uri: String) {
        val key = uriToKey(uri)
        playerStateMap[key]?.let {
            it.player.playWhenReady = false
            it.state = PlayerState.State.PAUSE
        }
    }

    fun pauseAllPlayer() {
        playerStateMap.forEach {
            it.value.player.playWhenReady = false
        }
    }

    fun replayPlayer(uri: String) {
        val key = uriToKey(uri)
        playerStateMap[key]?.let {
            it.player.seekTo(0)
            playPlayer(uri)

            if (!enableHandler) {
                enableHandler = true
                handler.postDelayed({ updatePlayerLastPosition() }, 250)
            }
        }
    }

    fun releasePlayer(uri: String, listener: PlayerEventListener? = null) {
        val key = uriToKey(uri)
        if (playerStateMap.containsKey(key)) {
            val playerState = playerStateMap[key]
            playerState!!.referenceCount--
            listener?.let { playerEventListeners[key]?.remove(it) }
            if (playerState.referenceCount <= 0) {
                playerState.player.release()
                playerStateMap.remove(key)
                playerEventListeners.remove(key)
            }
        }
    }

    fun resetAll() {
        playerStateMap.forEach {
            it.value.player.release()
        }
        playerStateMap.clear()
        playerEventListeners.clear()
    }

    // re create player
    // Playerがすぐ死ぬので再作成するしかない…
    fun reCreatePlayer(context: Context, uri: String, volume: Float = 0f, loop: Boolean = true): Player {
        val key = uriToKey(uri)
        var playerState = playerStateMap[key]

        if (playerState == null) {
            playerState = PlayerState(createPlayer(context, uri), 1, PlayerState.State.PAUSE, volume, loop)
        } else {
            playerState.player.release()
            val player = createPlayer(context, uri).also {
                it.repeatMode = if (playerState.loop) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF
                it.volume = playerState.volume
                it.seekTo(playerState.lastPositionSec * 1000)
            }
            playerState.player = player
        }

        return playerState.player
    }

    private fun createPlayer(context: Context, uri: String): SimpleExoPlayer {
        val key = uriToKey(uri)

        val player = SimpleExoPlayer.Builder(context).build()
        val dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, context.applicationInfo.name))
        val mediaSource = HlsMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(uri))

        player.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)
                if (playbackState == Player.STATE_ENDED && playWhenReady && player.currentPosition != 0L) {
                    val listeners = playerEventListeners[key]
                    listeners!!.forEach { it.onStateEnded() }
                }
            }
        })
        player.prepare(mediaSource)

        return player
    }

    private fun updatePlayerLastPosition() {
        var hasPlayingPlayer = false
        playerStateMap.forEach {
            val currentPositionSec = round(it.value.player.currentPosition / 1000.0).toLong()
            if (it.value.lastPositionSec != currentPositionSec) {
                it.value.lastPositionSec = currentPositionSec
                playerEventListeners[it.key]?.forEach { listener ->
                    listener.onRemainingTimeChange(round(it.value.player.duration / 1000.0).toLong() - currentPositionSec)
                }
            }

            if (it.value.player.playWhenReady) {
                hasPlayingPlayer = true
            }
        }

        if (playerStateMap.count() != 0 && hasPlayingPlayer) {
            enableHandler = true
            handler.postDelayed({ updatePlayerLastPosition() }, 250)
        } else {
            enableHandler = false
        }
    }

    private fun uriToKey(uri: String): String = uri // File(Uri.parse(uri).pathSegments.last()).absoluteFile.nameWithoutExtension
}

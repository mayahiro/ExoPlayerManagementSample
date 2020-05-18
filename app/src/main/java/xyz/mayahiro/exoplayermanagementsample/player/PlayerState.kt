package xyz.mayahiro.exoplayermanagementsample.player

import com.google.android.exoplayer2.Player

class PlayerState(
    var player: Player,
    var referenceCount: Int,
    var state: State,
    var volume: Float,
    var loop: Boolean,
    var lastPositionSec: Long = 0L
) {
    enum class State {
        PLAY,
        PAUSE
    }
}

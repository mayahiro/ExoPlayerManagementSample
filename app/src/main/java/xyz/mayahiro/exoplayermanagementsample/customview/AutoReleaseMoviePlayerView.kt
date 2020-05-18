package xyz.mayahiro.exoplayermanagementsample.customview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import xyz.mayahiro.exoplayermanagementsample.GlideApp
import xyz.mayahiro.exoplayermanagementsample.databinding.ViewMoviePlayerBinding
import xyz.mayahiro.exoplayermanagementsample.player.PlayerManager

class AutoReleaseMoviePlayerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {
    private var movieUri: String? = null

    private var binding: ViewMoviePlayerBinding = ViewMoviePlayerBinding.inflate(LayoutInflater.from(context), this, true)
    private val playerEventListener = object : PlayerManager.PlayerEventListener {
        override fun onRemainingTimeChange(remainingTimeSec: Long) {
            // 残り時間表示
        }

        override fun onStateEnded() {
            movieUri?.let { PlayerManager.pausePlayer(it) }
            binding.watchAgainButton.visibility = View.VISIBLE
        }
    }

    init {
        binding.watchAgainButton.setOnClickListener {
            movieUri?.let {
                binding.watchAgainButton.visibility = View.GONE
                PlayerManager.replayPlayer(it)
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        movieUri?.let {
            val player = PlayerManager.getPlayer(context, it, 1f, true, playerEventListener)
            binding.playerView.player = player
            PlayerManager.playPlayer(it)
        }

        binding.watchAgainButton.visibility = View.GONE
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        movieUri?.let { PlayerManager.releasePlayer(it, playerEventListener) }
    }

    fun setMovieUri(uri: String) {
        movieUri = uri

        if (isAttachedToWindow) {
            movieUri?.let {
                val player = PlayerManager.getPlayer(context, it, 1f, true, playerEventListener)
                binding.playerView.player = player
                PlayerManager.playPlayer(it)
            }

            binding.watchAgainButton.visibility = View.GONE
        }
    }

    fun setArt(uri: String) {
        GlideApp.with(context)
            .asBitmap()
            .load(uri)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                    // nothing
                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    binding.playerView.defaultArtwork = BitmapDrawable(context.resources, resource)
                    binding.playerView.useArtwork = true
                }
            })
    }

    fun reCreatePlayer() {
        movieUri?.let {
            binding.playerView.player = PlayerManager.reCreatePlayer(context, it)
            PlayerManager.playPlayer(it)
        }
    }
}

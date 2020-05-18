package xyz.mayahiro.exoplayermanagementsample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import xyz.mayahiro.exoplayermanagementsample.customview.AutoReleaseMoviePlayerView

class MovieActivity : AppCompatActivity() {
    companion object {
        private const val KEY_EXTRA_MOVIE_URI = "key_extra_movie_uri"

        fun createIntent(context: Context, movieUri: String) = Intent(context, MovieActivity::class.java).also {
            it.putExtra(KEY_EXTRA_MOVIE_URI, movieUri)
        }
    }

    private val movieUri: String by lazy {
        intent.getStringExtra(KEY_EXTRA_MOVIE_URI)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        findViewById<AutoReleaseMoviePlayerView>(R.id.movie_player_view).let {
            it.setMovieUri(movieUri)
        }
    }
}

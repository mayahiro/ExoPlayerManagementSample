package xyz.mayahiro.exoplayermanagementsample.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.mayahiro.exoplayermanagementsample.MovieActivity
import xyz.mayahiro.exoplayermanagementsample.databinding.RecyclerItemMovieCardBinding

class MoviesAdapter(context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val layoutInflater = LayoutInflater.from(context)

    private val movieUrl = "https://devstreaming-cdn.apple.com/videos/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = MovieCardViewHolder(RecyclerItemMovieCardBinding.inflate(layoutInflater, parent, false))

    override fun getItemCount(): Int = 100

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MovieCardViewHolder).bind("$movieUrl?dummy=${position % 10}")
    }

    inner class MovieCardViewHolder(private val binding: RecyclerItemMovieCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(url: String) {
            binding.moviePlayerView.setMovieUri(url)

            itemView.setOnClickListener {
                itemView.context.startActivity(MovieActivity.createIntent(itemView.context, url))
            }
        }

        fun reCreatePlayer() {
            binding.moviePlayerView.reCreatePlayer()
        }
    }
}

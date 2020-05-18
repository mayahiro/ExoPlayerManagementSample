package xyz.mayahiro.exoplayermanagementsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import xyz.mayahiro.exoplayermanagementsample.player.PlayerManager
import xyz.mayahiro.exoplayermanagementsample.recyclerview.MoviesAdapter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<RecyclerView>(R.id.recycler_view).let {
            it.adapter = MoviesAdapter(this)
            it.layoutManager = LinearLayoutManager(this)
        }
    }

    override fun onResume() {
        super.onResume()

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)

        val first = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        val last = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()

        (first..last).forEach {
            val viewHolder = recyclerView.findViewHolderForLayoutPosition(it)
            if (viewHolder is MoviesAdapter.MovieCardViewHolder) {
                viewHolder.reCreatePlayer()
            }
        }
    }

    override fun onPause() {
        PlayerManager.pauseAllPlayer()
        super.onPause()
    }
}

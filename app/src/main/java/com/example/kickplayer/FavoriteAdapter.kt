package com.example.kickplayer

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kickplayer.api.StreamFetcher
import com.example.kickplayer.databinding.ItemFavoriteBinding
import com.example.kickplayer.util.FavoriteManager

class FavoriteAdapter(
    private val context: Context,
    private var favorites: MutableList<String>,
    private val onStreamerClick: (String) -> Unit,
    private val onLongClick: (String) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    private val fetcher = StreamFetcher()
    private val statusMap = mutableMapOf<String, Boolean>()

    class ViewHolder(val binding: ItemFavoriteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val streamer = favorites[position]
        holder.binding.streamerName.text = streamer.capitalize()

        // Update status UI
        val isLive = statusMap[streamer] ?: false
        holder.binding.statusBadge.apply {
            text = if (isLive) context.getString(R.string.live_badge) else context.getString(R.string.offline_badge)
            setBackgroundResource(if (isLive) R.drawable.badge_bg_live else R.drawable.badge_bg_offline)
        }

        // Fetch status if not already fetched
        if (!statusMap.containsKey(streamer)) {
            fetcher.isLive(streamer) { live ->
                statusMap[streamer] = live
                (context as? MainActivity)?.runOnUiThread {
                    notifyItemChanged(position)
                }
            }
        }

        holder.itemView.setOnClickListener { onStreamerClick(streamer) }
        holder.itemView.setOnLongClickListener {
            onLongClick(streamer)
            true
        }
    }

    override fun getItemCount() = favorites.size

    fun updateList(newList: MutableList<String>) {
        favorites = newList
        statusMap.clear()
        notifyDataSetChanged()
    }
}

package com.example.playlistmaker.ui.media.favorite.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.search.model.Track

class FavoriteAdapter (
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<FavoriteViewHolder>() {

    var tracks = ArrayList<Track>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.track_item, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(tracks[holder.adapterPosition])
        }
    }

    override fun getItemCount() = tracks.size

    fun interface OnItemClickListener {
        fun onItemClick(track: Track)
    }
}
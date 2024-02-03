package com.example.playlistmaker.ui.media.playlists.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.PlaylistItemBinding
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.util.DiffCallback

class PlaylistsAdapter(
    private val onItemClickListener: OnItemClickListener
): RecyclerView.Adapter<PlaylistsViewHolder>() {

    private var playlists = ArrayList<Playlist>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        val itemBinding = PlaylistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistsViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(playlists[holder.adapterPosition].playlistId)
        }
    }

    fun setPlaylists(newPlaylists: List<Playlist>) {
        val diffCallback = DiffCallback(playlists, newPlaylists)
        val diffPlaylists = DiffUtil.calculateDiff(diffCallback)
        playlists.clear()
        playlists.addAll(newPlaylists)
        diffPlaylists.dispatchUpdatesTo(this)
    }
    fun interface OnItemClickListener {
        fun onItemClick(playlistId: Int)
    }

}
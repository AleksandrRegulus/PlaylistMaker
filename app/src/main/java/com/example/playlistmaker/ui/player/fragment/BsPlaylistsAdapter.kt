package com.example.playlistmaker.ui.player.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.PlaylistBsItemBinding
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.util.DiffCallback

class BsPlaylistsAdapter(
    private val onItemClickListener: OnItemClickListener
): RecyclerView.Adapter<BsPlaylistsViewHolder>() {

    private var playlists = ArrayList<Playlist>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BsPlaylistsViewHolder {
        val itemBinding = PlaylistBsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BsPlaylistsViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: BsPlaylistsViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(holder.adapterPosition)
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
        fun onItemClick(position: Int)
    }
}
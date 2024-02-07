package com.example.playlistmaker.ui.playlist.fragment

import com.example.playlistmaker.ui.search.fragment.TracksAdapter
import com.example.playlistmaker.ui.search.fragment.TracksViewHolder

class PlaylistTracksAdapter(
    private val onItemClickListener: OnItemClickListener,
    private val onItemLongClickListener: OnItemLongClickListener
) : TracksAdapter(onItemClickListener) {

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        holder.itemView.setOnLongClickListener {
            onItemLongClickListener.onItemLongClick(tracks[holder.adapterPosition].trackId, holder.adapterPosition)
            return@setOnLongClickListener true
        }
    }

    fun interface OnItemLongClickListener {
        fun onItemLongClick(trackId: Int, position: Int)
    }

}


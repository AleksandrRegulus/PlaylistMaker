package com.example.playlistmaker.ui.search.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.TrackItemBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.DiffCallback

open class TracksAdapter(
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<TracksViewHolder>() {

    var tracks = ArrayList<Track>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val itemBinding = TrackItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TracksViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(tracks[holder.adapterPosition], holder.adapterPosition)
        }
    }

    fun setTracks(newTracks: List<Track>) {
        val diffCallback = DiffCallback(tracks, newTracks)
        val diffTracks = DiffUtil.calculateDiff(diffCallback)
        tracks.clear()
        tracks.addAll(newTracks)
        diffTracks.dispatchUpdatesTo(this)
    }

    override fun getItemCount() = tracks.size

    interface OnItemClickListener {
        fun onItemClick(track: Track, position: Int)
    }
}
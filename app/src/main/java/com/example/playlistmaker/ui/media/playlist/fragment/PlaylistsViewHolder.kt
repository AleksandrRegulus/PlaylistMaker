package com.example.playlistmaker.ui.media.playlist.fragment

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistItemBinding
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.util.StringUtil

class PlaylistsViewHolder(private val itemBinding: PlaylistItemBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(playlist: Playlist) {

        if (playlist.posterUri == "") {
            itemBinding.posterIv.scaleType = ImageView.ScaleType.CENTER
            itemBinding.posterIv.setImageResource(R.drawable.playlist_placeholder)
        } else {
            itemBinding.posterIv.scaleType = ImageView.ScaleType.CENTER_CROP
            itemBinding.posterIv.setImageURI(playlist.posterUri.toUri())
        }

        itemBinding.playlistNameTv.text = playlist.playlistName

        itemBinding.numberTracksTv.text = itemView.context
            .getString(
                R.string.playlist_num_tracks,
                playlist.numberOfTracks,
                StringUtil.getNumTracksString(playlist.numberOfTracks)
            )
    }

}
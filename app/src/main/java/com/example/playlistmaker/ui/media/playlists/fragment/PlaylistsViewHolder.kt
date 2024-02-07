package com.example.playlistmaker.ui.media.playlists.fragment

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
        with(itemBinding) {
            if (playlist.posterUri == "") {
                posterIv.scaleType = ImageView.ScaleType.CENTER
                posterIv.setImageResource(R.drawable.playlist_placeholder)
            } else {
                posterIv.scaleType = ImageView.ScaleType.CENTER_CROP
                posterIv.setImageURI(playlist.posterUri.toUri())
            }

            playlistNameTv.text = playlist.playlistName
            numberTracksTv.text = itemView.context
                .getString(
                    R.string.playlist_num_tracks,
                    playlist.numberOfTracks,
                    StringUtil.getNumTracksString(playlist.numberOfTracks)
                )
        }
    }

}
package com.example.playlistmaker.ui.search.fragment

import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackItemBinding
import com.example.playlistmaker.domain.search.model.Track

class TracksViewHolder(private val itemBinding: TrackItemBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(model: Track) {
        with(itemBinding) {
            trackName.text = model.trackName
            artistName.text = model.artistName
            trackTime.text = model.trackTime

            Glide.with(itemView)
                .load(model.coverArtworkUrl100)
                .placeholder(R.drawable.track_placeholder)
                .fitCenter()
                .transform(
                    RoundedCorners(
                        TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            itemView.resources.getFloat(R.dimen.small_poster_rounded_corner),
                            itemView.resources.displayMetrics
                        ).toInt()
                    )
                )
                .into(poster)
        }
    }

}
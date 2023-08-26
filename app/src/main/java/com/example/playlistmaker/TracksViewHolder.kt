package com.example.playlistmaker

import android.annotation.SuppressLint
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TracksViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {

    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
    private val poster: ImageView = itemView.findViewById(R.id.poster)

    @SuppressLint("SuspiciousIndentation")
    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = model.trackTimeMillis
        trackTime.text = model.getTimeFromMillis()

        val roundedCorner = 2f  // величина скругления углов картинки в dp

        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.track_placeholder)
            .fitCenter()
            .transform(
                RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        roundedCorner,
                        itemView.resources.displayMetrics
                    ).toInt()
                )
            )
            .into(poster)
    }

}
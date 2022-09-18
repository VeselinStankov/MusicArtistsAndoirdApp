package com.example.navigationtask.artists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.navigationtask.R
import com.example.navigationtask.data.network.Artist
import com.example.navigationtask.databinding.ItemArtistBinding

class ArtistsAdapter(private val clickListener: ClickListener) :
    RecyclerView.Adapter<ArtistsAdapter.ArtistViewHolder>() {

    private var artists = mutableListOf<Artist>()

    inner class ArtistViewHolder(private val binding: ItemArtistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val artistItem = artists[position]
            binding.txtArtistName.text = artistItem.name
            binding.txtGenre.text = artistItem.genre
            Glide.with(itemView.context)
                .load(artistItem.image)
                .placeholder(R.drawable.ic_artist)
                .into(binding.imgSong)
            binding.grpArtist.setOnClickListener {
                clickListener.onArtistClicked(artistItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemArtistBinding.inflate(layoutInflater, parent, false)
        return ArtistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) = holder.bind(position)

    override fun getItemCount(): Int = artists.size

    fun updateArtistsFeedData(data: List<Artist>) {
        val diffUtil = ArtistDiff(artists, data)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        artists = data as MutableList<Artist>
        diffResult.dispatchUpdatesTo(this)
    }

    interface ClickListener {
        fun onArtistClicked(artist: Artist)
    }
}

class ArtistDiff(
    private val oldList: List<Artist>,
    private val newList: List<Artist>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}
package com.example.navigationtask.songs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.navigationtask.R
import com.example.navigationtask.data.network.Song
import com.example.navigationtask.databinding.ItemSongBinding

class SongsAdapter(private val clickListener: ClickListener) :
    RecyclerView.Adapter<SongsAdapter.SongViewHolder>() {

    private var songs = mutableListOf<Song>()

    inner class SongViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val songItem = songs[position]
            binding.txtSongTitle.text = songItem.songTitle
            binding.txtSongArtistName.text = songItem.artistName
            Glide.with(itemView.context)
                .load(songItem.songCover)
                .placeholder(R.drawable.ic_song)
                .into(binding.imgSong)
            binding.grpSong.setOnClickListener {
                clickListener.onSongCLicked(songItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSongBinding.inflate(layoutInflater, parent, false)
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) = holder.bind(position)

    override fun getItemCount(): Int = songs.size

    fun updateSongsFeedData(data: List<Song>) {
        val diffUtil = SongDiff(songs, data)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        songs = data as MutableList<Song>
        diffResult.dispatchUpdatesTo(this)
    }

    interface ClickListener {
        fun onSongCLicked(song: Song)
    }
}

class SongDiff(
    private val oldList: List<Song>,
    private val newList: List<Song>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}
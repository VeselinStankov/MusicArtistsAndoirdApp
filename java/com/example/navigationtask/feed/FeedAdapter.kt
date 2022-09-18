package com.example.navigationtask.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.navigationtask.R
import com.example.navigationtask.artists.mappers.ArtistUIToDomainMapper
import com.example.navigationtask.data.network.Artist
import com.example.navigationtask.data.network.Song
import com.example.navigationtask.databinding.ItemFeedMusicArtistBinding
import com.example.navigationtask.databinding.ItemFeedSectionHeaderBinding
import com.example.navigationtask.databinding.ItemFeedSongBinding
import com.example.navigationtask.databinding.ItemFeedWelcomeBinding
import com.example.navigationtask.songs.mappers.SongUIToDomainMapper
import java.lang.RuntimeException

const val WELCOME_CARD = 0
const val HEADER = 1
const val SONG = 2
const val ARTIST = 3

class FeedAdapter(
    private var feedData: MutableList<DataItem>,
    private val isSharingEnabled: Boolean,
    private val clickListener: ClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class WelcomeCardViewHolder(private val binding: ItemFeedWelcomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val welcomeCardItem = feedData[position] as DataItem.WelcomeCard
            binding.txtEmail.text = welcomeCardItem.email
            if (isSharingEnabled) {
                binding.btnShare.setOnClickListener {
                    clickListener.onShareClicked()
                }
            } else {
                binding.btnShare.isVisible = false
            }
        }
    }

    inner class HeaderViewHolder(private val binding: ItemFeedSectionHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val headerItem = feedData[position] as DataItem.Header

            when (headerItem.type) {
                HeaderType.SONGS_VIEW_ALL -> {
                    binding.txtMainHeader.setText(R.string.song_main_header)
                    binding.txtSecondaryHeader.setText(R.string.song_secondary_header)
                }
                HeaderType.ARTISTS_VIEW_ALL -> {
                    binding.txtMainHeader.setText(R.string.artist_main_header)
                    binding.txtSecondaryHeader.setText(R.string.artist_secondary_header)
                }
                else -> {
                    throw RuntimeException(
                        "Unknown type. You (The developer) probably" +
                                " forgot to implement a view holder for your new data type."
                    )
                }
            }

            binding.txtViewAll.setOnClickListener {
                when (headerItem.type) {
                    HeaderType.SONGS_VIEW_ALL -> clickListener.onViewAllSongsClicked()
                    HeaderType.ARTISTS_VIEW_ALL -> clickListener.onViewAllArtistsClicked()
                    else -> {
                        throw RuntimeException(
                            "Unknown type. You (The developer) probably" +
                                    " forgot to implement a view holder for your new data type."
                        )
                    }
                }
            }
        }
    }

    inner class SongViewHolder(private val binding: ItemFeedSongBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val songItem = feedData[position] as DataItem.Song
            binding.txtSongTitle.text = songItem.title
            binding.txtSongArtistName.text = songItem.artistName
            binding.txtSongArtistName.text = songItem.artistName
            Glide.with(itemView.context)
                .load(songItem.songCover)
                .placeholder(R.drawable.ic_song)
                .into(binding.imgSong)

            binding.grpSong.setOnClickListener {
                clickListener.onSongClicked(SongUIToDomainMapper().invoke(songItem))
            }
        }
    }

    inner class ArtistViewHolder(private val binding: ItemFeedMusicArtistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val artistItem = feedData[position] as DataItem.Artist
            binding.txtArtistName.text = artistItem.name
            binding.txtArtistAlbumCount.text = artistItem.albumCount.toString()
            Glide.with(itemView.context)
                .load(artistItem.image)
                .placeholder(R.drawable.ic_artist)
                .into(binding.imgArtist)

            binding.grpArtist.setOnClickListener {
                clickListener.onArtistClicked(ArtistUIToDomainMapper().invoke(artistItem))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            WELCOME_CARD -> {
                val binding = ItemFeedWelcomeBinding.inflate(layoutInflater, parent, false)
                WelcomeCardViewHolder(binding)
            }
            HEADER -> {
                val binding = ItemFeedSectionHeaderBinding.inflate(layoutInflater, parent, false)
                HeaderViewHolder(binding)
            }
            SONG -> {
                val binding = ItemFeedSongBinding.inflate(layoutInflater, parent, false)
                SongViewHolder(binding)
            }
            ARTIST -> {
                val binding = ItemFeedMusicArtistBinding.inflate(layoutInflater, parent, false)
                ArtistViewHolder(binding)
            }
            else -> {
                throw RuntimeException(
                    "You (The developer) probably" +
                            " forgot to implement a view holder for your new data type."
                )
            }
        }
    }

    override fun getItemCount(): Int = feedData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is WelcomeCardViewHolder -> {
                holder.bind(position)
            }
            is HeaderViewHolder -> {
                holder.bind(position)
            }
            is SongViewHolder -> {
                holder.bind(position)
            }
            is ArtistViewHolder -> {
                holder.bind(position)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (feedData[position]) {
            is DataItem.WelcomeCard -> WELCOME_CARD
            is DataItem.Header -> HEADER
            is DataItem.Song -> SONG
            is DataItem.Artist -> ARTIST
        }
    }

    interface ClickListener {
        fun onShareClicked()

        fun onViewAllSongsClicked()

        fun onViewAllArtistsClicked()

        fun onSongClicked(song: Song)

        fun onArtistClicked(artist: Artist)
    }

    fun updateFeedData(data: List<DataItem>) {
        val diffUtil = FeedDiff(feedData, data)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        feedData = data as MutableList<DataItem>
        diffResult.dispatchUpdatesTo(this)
    }
}

sealed class DataItem {
    data class WelcomeCard(
        val email: String
    ) : DataItem()

    data class Header(
        val type: HeaderType,
    ) : DataItem()

    data class Song(
        val id: Long,
        val title: String,
        val artistName: String,
        val songCover: String,
        val firstFanCover: String,
        val secondFanCover: String,
        val thirdFanCover: String,
        val fourthFanCover: String,
        val likes: Int,
        val genre: String,
        val views: Int,
        val comments: Int,
        val lyrics: String
    ) : DataItem()

    data class Artist(
        val id: Long,
        val name: String,
        val albumCount: Int,
        val image: String,
        val genre: String,
        val firstFanArt: String,
        val secondFanArt: String,
        val thirdFanArt: String,
        val fourthFanArt: String,
        val country: String,
        val gender: Boolean,
        val fans: Int,
        val description: String
    ) : DataItem()
}

enum class HeaderType {
    SONGS_VIEW_ALL,
    ARTISTS_VIEW_ALL
}

class FeedDiff(
    private val oldList: List<DataItem>,
    private val newList: List<DataItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return if (oldItem::class == newItem::class) {
            when (oldItem) {
                is DataItem.WelcomeCard -> oldItem.email == (newItem as DataItem.WelcomeCard).email
                is DataItem.Header -> oldItem.type == (newItem as DataItem.Header).type
                is DataItem.Song -> oldItem.id == (newItem as DataItem.Song).id
                is DataItem.Artist -> oldItem.id == (newItem as DataItem.Artist).id
            }
        } else {
            false
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}
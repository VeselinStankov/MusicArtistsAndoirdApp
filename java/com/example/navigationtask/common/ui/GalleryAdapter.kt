package com.example.navigationtask.common.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.navigationtask.R
import com.example.navigationtask.databinding.ItemGalleryBinding

class GalleryAdapter(private val photos: List<String>) :
    RecyclerView.Adapter<GalleryAdapter.PhotosViewHolder>() {

    inner class PhotosViewHolder(private val binding: ItemGalleryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val photo = photos[position]
            Glide.with(itemView.context)
                .load(photo)
                .placeholder(R.drawable.ic_loading_details_error)
                .into(binding.imgPhoto)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemGalleryBinding.inflate(layoutInflater, parent, false)
        return PhotosViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) = holder.bind(position)

    override fun getItemCount(): Int = photos.size
}
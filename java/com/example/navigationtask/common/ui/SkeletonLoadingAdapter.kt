package com.example.navigationtask.common.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.navigationtask.databinding.ItemLoadingSkeletonBinding
import com.example.navigationtask.common.ui.SkeletonLoadingAdapter.ViewHolder

class SkeletonLoadingAdapter : RecyclerView.Adapter<ViewHolder>() {

    var itemsCount: Int = 0
        set(value) {
            if (field == value) return
            val oldValue = field
            if (oldValue > value) {
                notifyItemRangeRemoved(0, oldValue - value)
            } else {
                notifyItemRangeInserted(0, value - oldValue)
            }
            field = value
        }

    inner class ViewHolder(binding: ItemLoadingSkeletonBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemLoadingSkeletonBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {}

    override fun getItemCount(): Int = itemsCount
}
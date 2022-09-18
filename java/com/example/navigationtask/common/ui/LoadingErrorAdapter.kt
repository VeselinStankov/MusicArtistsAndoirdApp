package com.example.navigationtask.common.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.example.navigationtask.databinding.ItemLoadingErrorBinding

class LoadingErrorAdapter(
    @StringRes private val errorTextRes: Int,
    private val clickListener: ClickListener
) :
    RecyclerView.Adapter<LoadingErrorAdapter.LoadingErrorViewHolder>() {

    var isVisible = false
        set(value) {
            if (value != field) {
                field = value
                if (value) {
                    notifyItemInserted(0)
                } else {
                    notifyItemRemoved(0)
                }
            }
        }

    inner class LoadingErrorViewHolder(private val binding: ItemLoadingErrorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.txtLoadingError.setText(errorTextRes)
            binding.btnRetry.setOnClickListener {
                if (isVisible) {
                    clickListener.onRetryClicked()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadingErrorViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemLoadingErrorBinding.inflate(layoutInflater, parent, false)
        return LoadingErrorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LoadingErrorViewHolder, position: Int) = holder.bind()

    override fun getItemCount(): Int = if (isVisible) 1 else 0

    interface ClickListener {
        fun onRetryClicked()
    }
}
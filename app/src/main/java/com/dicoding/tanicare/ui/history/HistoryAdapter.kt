package com.dicoding.tanicare.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.tanicare.databinding.ItemHistoryBinding
import com.dicoding.tanicare.data.local.HistoryEntity
import com.bumptech.glide.Glide
import com.dicoding.tanicare.R


class HistoryAdapter :
    ListAdapter<HistoryEntity, HistoryAdapter.HistoryViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HistoryEntity>() {
            override fun areItemsTheSame(oldItem: HistoryEntity, newItem: HistoryEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: HistoryEntity,
                newItem: HistoryEntity
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
        val animation =
            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.item_animation_fade_in)
        holder.itemView.startAnimation(animation)
    }


    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(history: HistoryEntity) {
            binding.tvLabel.text = history.diseaseName
            binding.tvAccuracy.text = "Accuracy: ${history.accuracy}"
            binding.tvTimestamp.text = android.text.format.DateFormat.format(
                "yyyy-MM-dd HH:mm:ss",
                history.timestamp
            )

            Glide.with(binding.ivHistoryImage.context)
                .load(history.imagePath)
                .centerCrop()
                .into(binding.ivHistoryImage)
        }
    }

}

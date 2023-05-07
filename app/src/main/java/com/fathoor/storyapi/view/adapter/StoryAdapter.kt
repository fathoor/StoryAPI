package com.fathoor.storyapi.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fathoor.storyapi.databinding.ItemStoryBinding
import com.fathoor.storyapi.model.remote.response.Story
import com.fathoor.storyapi.view.helper.StoryDiffCallback

class StoryAdapter : PagingDataAdapter<Story, StoryAdapter.ViewHolder>(StoryDiffCallback()) {
    private var onClickCallback: OnClickCallback? = null

    interface OnClickCallback { fun onClicked(data: Story) }

    fun setOnClickCallback(onClickCallback: OnClickCallback?) { this.onClickCallback = onClickCallback }

    class ViewHolder(val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { story ->
            holder.apply {
                binding.apply {
                    Glide.with(itemView.context)
                        .load(story.photoUrl)
                        .into(ivItemPhoto)
                    tvItemName.text = story.name
                    tvItemDescription.text = story.description
                }

                itemView.setOnClickListener { onClickCallback?.onClicked(story) }
            }
        }
    }
}
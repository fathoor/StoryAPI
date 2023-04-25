package com.fathoor.storyapi.view.helper

import androidx.recyclerview.widget.DiffUtil
import com.fathoor.storyapi.model.remote.response.Story

class StoryDiffCallback : DiffUtil.ItemCallback<Story>() {
    override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
        return oldItem == newItem
    }
}
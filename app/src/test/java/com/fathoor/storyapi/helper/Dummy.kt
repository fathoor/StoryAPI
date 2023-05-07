package com.fathoor.storyapi.helper

import com.fathoor.storyapi.model.remote.response.Story

object Dummy {
    fun generateStoryDummy(): List<Story> {
        val stories = arrayListOf<Story>()
        for (i in 1..10) {
            val story = Story(
                "story-$i",
                "test",
                "description",
                "https://picsum.photos/200",
                (-6.8957643).toFloat(),
                (107.6338462).toFloat()
            )
            stories.add(story)
        }
        return stories
    }
}
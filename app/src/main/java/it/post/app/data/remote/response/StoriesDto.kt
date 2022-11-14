package it.post.app.data.remote.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StoriesDto(
    @Json(name = "listStory") val stories: List<StoryDto>,
)

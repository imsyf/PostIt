package it.post.app.data.remote.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DetailsDto(
    val story: StoryDto,
)

package it.post.app.data.remote.response

import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class StoryDto(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: Date,
    val lat: Double?,
    val lon: Double?,
)

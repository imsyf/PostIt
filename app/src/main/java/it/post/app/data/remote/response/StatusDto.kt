package it.post.app.data.remote.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StatusDto(
    val error: Boolean,
    val message: String,
)

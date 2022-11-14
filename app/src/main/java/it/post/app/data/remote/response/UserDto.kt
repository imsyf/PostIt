package it.post.app.data.remote.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDto(
    val userId: String,
    val name: String,
    val token: String,
)

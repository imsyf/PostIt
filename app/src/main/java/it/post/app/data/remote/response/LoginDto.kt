package it.post.app.data.remote.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginDto(
    @Json(name = "loginResult") val user: UserDto,
)

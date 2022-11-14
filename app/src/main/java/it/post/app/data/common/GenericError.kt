package it.post.app.data.common

import com.haroldadmin.cnradapter.NetworkResponse
import it.post.app.data.remote.response.StatusDto

data class GenericError(
    private val _message: String?,
) {
    val message: String = _message ?: "Unknown error"

    companion object {
        fun <S> fromNetworkResponse(response: NetworkResponse.Error<S, StatusDto>): GenericError {
            // Prioritize error message from remote API over other forms of throwable
            val message = response.body?.message ?: response.error?.localizedMessage
            return GenericError(message)
        }

        fun fromThrowable(throwable: Throwable): GenericError = GenericError(throwable.message)
    }
}

package it.post.app.data

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import com.haroldadmin.cnradapter.NetworkResponse
import it.post.app.data.common.GenericError
import it.post.app.data.local.PreferenceManager
import it.post.app.data.remote.StoryApi
import it.post.app.data.remote.response.StoryDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class StoryRepository(
    private val storyApi: StoryApi,
    private val preferenceManager: PreferenceManager,
) {
    val isUserLoggedInFlow: Flow<Boolean> = preferenceManager.tokenFlow
        .map { it != null }

    suspend fun login(email: String, password: String): Result<String, GenericError> {
        return when (val response = storyApi.login(email, password)) {
            is NetworkResponse.Success -> {
                preferenceManager.saveToken(response.body.user.token)
                Ok(response.body.user.name)
            }
            is NetworkResponse.Error -> Err(GenericError.fromNetworkResponse(response))
        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
    ): Result<Unit, GenericError> {
        return when (val response = storyApi.register(name, email, password)) {
            is NetworkResponse.Success -> Ok(Unit)
            is NetworkResponse.Error -> Err(GenericError.fromNetworkResponse(response))
        }
    }

    suspend fun upload(
        description: String,
        photo: File,
    ): Result<Unit, GenericError> {
        val token = preferenceManager.tokenFlow.first()

        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("description", description)
            .addPart(
                MultipartBody.Part.createFormData(
                    "photo",
                    photo.name,
                    photo.asRequestBody("image/*".toMediaTypeOrNull()),
                ),
            )
            .build()

        return when (val response = storyApi.upload("Bearer $token", body)) {
            is NetworkResponse.Success -> Ok(Unit)
            is NetworkResponse.Error -> Err(GenericError.fromNetworkResponse(response))
        }
    }

    suspend fun getStories(page: Int, size: Int): Result<List<StoryDto>, GenericError> {
        val token = preferenceManager.tokenFlow.first()

        return when (val response = storyApi.getStories("Bearer $token", page, size)) {
            is NetworkResponse.Success -> Ok(response.body.stories)
            is NetworkResponse.Error -> Err(GenericError.fromNetworkResponse(response))
        }
    }

    suspend fun getStory(storyId: String): Result<StoryDto, GenericError> {
        val token = preferenceManager.tokenFlow.first()

        return when (val response = storyApi.getStory("Bearer $token", storyId)) {
            is NetworkResponse.Success -> Ok(response.body.story)
            is NetworkResponse.Error -> Err(GenericError.fromNetworkResponse(response))
        }
    }

    suspend fun logout(): Result<Unit, GenericError> =
        runCatching {
            preferenceManager.deleteToken()
        }.mapError {
            GenericError.fromThrowable(it)
        }
}

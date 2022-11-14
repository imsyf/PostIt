package it.post.app.data.remote

import com.haroldadmin.cnradapter.NetworkResponse
import it.post.app.data.remote.response.DetailsDto
import it.post.app.data.remote.response.LoginDto
import it.post.app.data.remote.response.StatusDto
import it.post.app.data.remote.response.StoriesDto
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface StoryApi {

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): NetworkResponse<LoginDto, StatusDto>

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): NetworkResponse<Unit, StatusDto>

    @POST("stories")
    suspend fun upload(
        @Header("Authorization") authorization: String,
        @Body body: RequestBody,
    ): NetworkResponse<Unit, StatusDto>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") authorization: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10,
    ): NetworkResponse<StoriesDto, StatusDto>

    @GET("stories/{id}")
    suspend fun getStory(
        @Header("Authorization") authorization: String,
        @Path("id") storyId: String,
    ): NetworkResponse<DetailsDto, StatusDto>
}

package dicoding.zulfikar.storyapp.data.remote.retrofit

import dicoding.zulfikar.storyapp.data.remote.response.LoginResponse
import dicoding.zulfikar.storyapp.data.remote.response.MessageResponse
import dicoding.zulfikar.storyapp.data.remote.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*


interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): MessageResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getStoriesLocation(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 100,
        @Query("location") location: Int = 1
    ): StoryResponse

    @GET("stories")
    suspend fun getPagingStories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): StoryResponse

    @Multipart
    @POST("stories")
    suspend fun uploadImage(
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): MessageResponse
    @Multipart
    @POST("stories")
    suspend fun uploadWithoutLocation(
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): MessageResponse
}
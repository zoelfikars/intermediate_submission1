package dicoding.zulfikar.storyapp.data

import com.google.gson.Gson
import dicoding.zulfikar.storyapp.data.remote.Result
import dicoding.zulfikar.storyapp.data.remote.response.MessageResponse
import dicoding.zulfikar.storyapp.data.remote.response.StoryResponse
import dicoding.zulfikar.storyapp.data.remote.retrofit.ApiService

class StoryRepository private constructor(
    private val apiService: ApiService
) {
    suspend fun getStories(): Result<StoryResponse> {
        try {
            val stories = apiService.getStories()
            if (stories.error) {
                val errorMessage = stories.message
                return Result.Error(Exception(errorMessage))
            }
            return Result.Success(stories)
        } catch (e: retrofit2.HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, MessageResponse::class.java)
            val errorMessage = errorBody.message
            return Result.Error(Exception(errorMessage))
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(
                    apiService
                )
            }.also { instance = it }
    }
}
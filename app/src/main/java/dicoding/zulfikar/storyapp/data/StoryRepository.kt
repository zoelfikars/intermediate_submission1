package dicoding.zulfikar.storyapp.data

import com.google.gson.Gson
import dicoding.zulfikar.storyapp.data.remote.Result
import dicoding.zulfikar.storyapp.data.remote.response.LoginResponse
import dicoding.zulfikar.storyapp.data.remote.response.MessageResponse
import dicoding.zulfikar.storyapp.data.remote.response.StoryResponse
import dicoding.zulfikar.storyapp.data.remote.retrofit.ApiConfig
import dicoding.zulfikar.storyapp.data.remote.retrofit.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class StoryRepository private constructor(
    private var apiService: ApiService
) {
    suspend fun getStories(token: String): Result<StoryResponse> {
        if (token.isNotEmpty()) {
            apiService = ApiConfig.getApiService(token)
        }
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

    suspend fun uploadImage(
        imageFile: File,
        description: String
    ): Result<MessageResponse> {
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        return try {
            val successResponse = apiService.uploadImage(multipartBody, requestBody)
            Result.Success(successResponse)
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, MessageResponse::class.java)
            val errorMessage = errorBody.message
            Result.Error(Exception(errorMessage))
        }
    }

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        try {
            val loginResponse = apiService.login(email, password)

            if (loginResponse.error) {
                val errorMessage = loginResponse.message
                return Result.Error(Exception(errorMessage))
            }
            return Result.Success(loginResponse)
        } catch (e: retrofit2.HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, MessageResponse::class.java)
            val errorMessage = errorBody.message
            return Result.Error(Exception(errorMessage))
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<MessageResponse> {
        try {
            val registerResponse = apiService.register(name, email, password)

            if (registerResponse.error) {
                val errorMessage = registerResponse.message
                return Result.Error(Exception(errorMessage))
            }

            return Result.Success(registerResponse)
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
package dicoding.zulfikar.storyapp.data

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.gson.Gson
import dicoding.zulfikar.storyapp.data.local.room.StoryDatabase
import dicoding.zulfikar.storyapp.data.remote.Result
import dicoding.zulfikar.storyapp.data.remote.response.ListStoryItem
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
    private var apiService: ApiService,
    private val storyDatabase: StoryDatabase
) {
    fun getStoryPaging(token: String): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, token, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getPagingStory()
            }
        ).liveData
    }
    @SuppressLint("SuspiciousIndentation")
    suspend fun getStoryLocation(token: String): Result<StoryResponse> {
        if(token.isNotEmpty()) {
            apiService = ApiConfig.getApiService(token)
        }
        try {
            val listResponse = apiService.getStoriesLocation()

                if (listResponse.error) {
                    val errorMessage = listResponse.message
                    return Result.Error(Exception(errorMessage))
                }
            return Result.Success(listResponse)
        } catch (e: retrofit2.HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, MessageResponse::class.java)
            val errorMessage = errorBody.message
            return Result.Error(Exception(errorMessage))
        }
    }
    suspend fun uploadImage(
        imageFile: File,
        description: String,
        lat: Double?,
        lon: Double?,
        location: Boolean,
        token: String,
    ): Result<MessageResponse> {
        if(token.isNotEmpty()) {
            apiService = ApiConfig.getApiService(token)
        }
        val descriptionPart = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())

        val latPart = lat.toString().toRequestBody("text/plain".toMediaType())
        val lonPart = lon.toString().toRequestBody("text/plain".toMediaType())

        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        return try {
            if(location) {
                val successResponse = apiService.uploadImage(multipartBody, descriptionPart, latPart, lonPart)
                Result.Success(successResponse)
            } else {
                val successResponse = apiService.uploadWithoutLocation(multipartBody, descriptionPart)
                Result.Success(successResponse)
            }
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
            apiService: ApiService,
            storyDatabase: StoryDatabase
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(
                    apiService, storyDatabase
                )
            }.also { instance = it }
    }
}
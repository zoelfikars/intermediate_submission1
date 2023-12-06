package dicoding.zulfikar.storyapp.data

import com.google.gson.Gson
import dicoding.zulfikar.storyapp.data.remote.Result
import dicoding.zulfikar.storyapp.data.remote.response.MessageResponse
import dicoding.zulfikar.storyapp.data.remote.retrofit.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class UploadRepository private constructor(
    private val apiService: ApiService
) {
    suspend fun uploadImage(imageFile: File, description: String): Result<MessageResponse> {
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

    companion object {
        @Volatile
        private var instance: UploadRepository? = null
        fun getInstance(apiService: ApiService) =
            instance ?: synchronized(this) {
                instance ?: UploadRepository(apiService)
            }.also { instance = it }
    }
}
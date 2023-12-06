package dicoding.zulfikar.storyapp.data

import com.google.gson.Gson
import dicoding.zulfikar.storyapp.data.models.UserModel
import dicoding.zulfikar.storyapp.data.pref.UserPreference
import dicoding.zulfikar.storyapp.data.remote.Result
import dicoding.zulfikar.storyapp.data.remote.response.LoginResponse
import dicoding.zulfikar.storyapp.data.remote.response.MessageResponse
import dicoding.zulfikar.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow


class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        try {
            val loginResponse = apiService.login(email, password)

            if (loginResponse.error) {
                val errorMessage = loginResponse.message
                return Result.Error(Exception(errorMessage))
            }
            saveSession(loginResponse.loginResult)
            return Result.Success(loginResponse)
        } catch (e: retrofit2.HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, MessageResponse::class.java)
            val errorMessage = errorBody.message
            return Result.Error(Exception(errorMessage))
        }
    }

    suspend fun saveSession(loginResult: UserModel) {
        userPreference.saveSession(loginResult)
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

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(
                    userPreference,
                    apiService
                )
            }.also { instance = it }
    }
}

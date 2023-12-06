package dicoding.zulfikar.storyapp.data.di

import android.content.Context
import android.util.Log
import dicoding.zulfikar.storyapp.data.StoryRepository
import dicoding.zulfikar.storyapp.data.UploadRepository
import dicoding.zulfikar.storyapp.data.UserRepository
import dicoding.zulfikar.storyapp.data.pref.UserPreference
import dicoding.zulfikar.storyapp.data.pref.dataStore
import dicoding.zulfikar.storyapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val token = runBlocking { userPreference.getSession().first().token }
        Log.d("INI DI INJECTION REPOSITORY", "tokennya : $token")
        val apiService = ApiConfig.getApiService(token)
        return UserRepository.getInstance(userPreference, apiService)
    }
    fun provideStoryRepository(context: Context): StoryRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val token = runBlocking { userPreference.getSession().first().token }
        Log.d("INI DI INJECTION REPOSITORY", "tokennya : $token")
        val apiService = ApiConfig.getApiService(token)
        return StoryRepository.getInstance(apiService)
    }
    fun provideUploadRepository(context: Context): UploadRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val token = runBlocking { userPreference.getSession().first().token }
        Log.d("INI DI INJECTION REPOSITORY", "tokennya : $token")
        val apiService = ApiConfig.getApiService(token)
        return UploadRepository.getInstance(apiService)
    }
}
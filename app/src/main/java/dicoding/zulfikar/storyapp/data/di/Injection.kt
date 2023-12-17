package dicoding.zulfikar.storyapp.data.di

import android.content.Context
import dicoding.zulfikar.storyapp.data.StoryRepository
import dicoding.zulfikar.storyapp.data.local.room.StoryDatabase
import dicoding.zulfikar.storyapp.data.pref.UserPreference
import dicoding.zulfikar.storyapp.data.pref.dataStore
import dicoding.zulfikar.storyapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val token = runBlocking{pref.getSession().first().token}
        val storyDatabase = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService(token)
        return StoryRepository.getInstance(apiService, storyDatabase)
    }
}
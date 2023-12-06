package dicoding.zulfikar.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dicoding.zulfikar.storyapp.data.StoryRepository
import dicoding.zulfikar.storyapp.data.UserRepository
import dicoding.zulfikar.storyapp.data.models.UserModel
import dicoding.zulfikar.storyapp.data.remote.Result
import dicoding.zulfikar.storyapp.data.remote.response.StoryResponse
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository, private val story: StoryRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    suspend fun getStories(): Result<StoryResponse> {
        return story.getStories()
    }


    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

}
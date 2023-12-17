package dicoding.zulfikar.storyapp.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dicoding.zulfikar.storyapp.data.StoryRepository
import dicoding.zulfikar.storyapp.data.remote.Result
import dicoding.zulfikar.storyapp.data.remote.response.ListStoryItem
import dicoding.zulfikar.storyapp.data.remote.response.LoginResponse
import dicoding.zulfikar.storyapp.data.remote.response.MessageResponse
import dicoding.zulfikar.storyapp.data.remote.response.StoryResponse
import java.io.File

class MainViewModel(
    private val storyRepository: StoryRepository
) : ViewModel() {
    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> get() = _stories

    fun getStoryPaging(token: String): LiveData<PagingData<ListStoryItem>> {
        return storyRepository.getStoryPaging(token).cachedIn(viewModelScope)
    }
    suspend fun getStoryLocation(token: String) :Result<StoryResponse>{
        return storyRepository.getStoryLocation(token)
    }
    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return storyRepository.login(email, password)
    }

    suspend fun register(name: String, email: String, password: String): Result<MessageResponse> {
        return storyRepository.register(name, email, password)
    }
    suspend fun uploadImage(
        file: File,
        description: String,
        lat: Double?,
        lon: Double?,
        location: Boolean,
        token: String,
    ): Result<MessageResponse> {
        return storyRepository.uploadImage(file, description, lat, lon, location, token)
    }
}
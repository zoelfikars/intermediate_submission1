package dicoding.zulfikar.storyapp.view.main

import androidx.lifecycle.ViewModel
import dicoding.zulfikar.storyapp.data.StoryRepository
import dicoding.zulfikar.storyapp.data.remote.Result
import dicoding.zulfikar.storyapp.data.remote.response.MessageResponse
import dicoding.zulfikar.storyapp.data.remote.response.StoryResponse
import java.io.File

class MainViewModel(
    private val repository: StoryRepository
) : ViewModel() {
    suspend fun getStories(token: String): Result<StoryResponse> {
        return repository.getStories(token)
    }
    suspend fun uploadImage(
        file: File,
        description: String,
    ): Result<MessageResponse> {
        return repository.uploadImage(file, description)
    }
}
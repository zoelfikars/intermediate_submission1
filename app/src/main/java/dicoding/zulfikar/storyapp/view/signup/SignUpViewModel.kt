package dicoding.zulfikar.storyapp.view.signup

import androidx.lifecycle.ViewModel
import dicoding.zulfikar.storyapp.data.StoryRepository
import dicoding.zulfikar.storyapp.data.remote.Result
import dicoding.zulfikar.storyapp.data.remote.response.MessageResponse


class SignUpViewModel(private val repository: StoryRepository) : ViewModel() {
    suspend fun register(name: String, email: String, password: String): Result<MessageResponse> {
        return repository.register(name, email, password)
    }
}
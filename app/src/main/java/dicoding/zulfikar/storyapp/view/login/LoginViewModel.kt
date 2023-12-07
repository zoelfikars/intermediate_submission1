package dicoding.zulfikar.storyapp.view.login

import androidx.lifecycle.ViewModel
import dicoding.zulfikar.storyapp.data.StoryRepository
import dicoding.zulfikar.storyapp.data.remote.Result
import dicoding.zulfikar.storyapp.data.remote.response.LoginResponse

class LoginViewModel(private val repository: StoryRepository) : ViewModel() {
    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return repository.login(email, password)
    }
}

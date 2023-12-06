package dicoding.zulfikar.storyapp.view.login

import androidx.lifecycle.ViewModel
import dicoding.zulfikar.storyapp.data.UserRepository
import dicoding.zulfikar.storyapp.data.remote.Result
import dicoding.zulfikar.storyapp.data.remote.response.LoginResponse

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return userRepository.login(email, password)
    }
}
package dicoding.zulfikar.storyapp.view.signup

import androidx.lifecycle.ViewModel
import dicoding.zulfikar.storyapp.data.UserRepository
import dicoding.zulfikar.storyapp.data.remote.Result
import dicoding.zulfikar.storyapp.data.remote.response.MessageResponse


class SignUpViewModel(private val userRepository: UserRepository) : ViewModel() {
    suspend fun register(name: String, email: String, password: String): Result<MessageResponse> {
        return userRepository.register(name, email, password)
    }
}
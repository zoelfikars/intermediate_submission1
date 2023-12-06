package dicoding.zulfikar.storyapp.view.addstory

import androidx.lifecycle.ViewModel
import dicoding.zulfikar.storyapp.data.UploadRepository
import dicoding.zulfikar.storyapp.data.remote.Result
import dicoding.zulfikar.storyapp.data.remote.response.MessageResponse
import java.io.File

class AddStoryViewModel(private val repository: UploadRepository) : ViewModel() {
    suspend fun uploadImage(file: File, description: String): Result<MessageResponse> {
        return repository.uploadImage(file, description)
    }
}
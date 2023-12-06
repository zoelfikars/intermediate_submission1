package dicoding.zulfikar.storyapp.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dicoding.zulfikar.storyapp.data.StoryRepository
import dicoding.zulfikar.storyapp.data.UploadRepository
import dicoding.zulfikar.storyapp.data.UserRepository
import dicoding.zulfikar.storyapp.data.di.Injection
import dicoding.zulfikar.storyapp.view.addstory.AddStoryViewModel
import dicoding.zulfikar.storyapp.view.login.LoginViewModel
import dicoding.zulfikar.storyapp.view.main.MainViewModel
import dicoding.zulfikar.storyapp.view.signup.SignUpViewModel

class ViewModelFactory(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository,
    private val uploadRepository: UploadRepository
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }

            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userRepository, storyRepository) as T
            }

            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(userRepository) as T
            }

            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(uploadRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    val userRepository = Injection.provideUserRepository(context)
                    val storyRepository = Injection.provideStoryRepository(context)
                    val uploadRepository = Injection.provideUploadRepository(context)

                    INSTANCE = ViewModelFactory(userRepository, storyRepository, uploadRepository)
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}
package dicoding.zulfikar.storyapp.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dicoding.zulfikar.storyapp.data.StoryRepository
import dicoding.zulfikar.storyapp.data.di.Injection

class ViewModelFactory(
    private val repository: StoryRepository,
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            val storyRepository = Injection.provideRepository(context)
            return ViewModelFactory(storyRepository)
        }
    }

}
package dicoding.zulfikar.storyapp.data.models

data class UserModel(
    val userId: String,
    val name: String,
    val token: String,
    val isLogin: Boolean = false
)

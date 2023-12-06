package dicoding.zulfikar.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName
import dicoding.zulfikar.storyapp.data.models.UserModel

data class LoginResponse(

    @field:SerializedName("loginResult")
    val loginResult: UserModel,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)
package dicoding.zulfikar.storyapp.data.remote.response

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class StoryResponse(

    @field:SerializedName("listStory")
    val listStory: List<ListStoryItem>,

    @field:SerializedName("story")
    val story: List<ListStoryItem>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)

@Entity(tableName = "story")
data class ListStoryItem(

    @field:SerializedName("photoUrl")
    val photoUrl: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("lon")
    val lon: Double,

    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("lat")
    val lat: Double
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readDouble(),
        parcel.readString().toString(),
        parcel.readDouble()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(photoUrl)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(createdAt)
        parcel.writeDouble(lon)
        parcel.writeString(id)
        parcel.writeDouble(lat)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ListStoryItem> {
        override fun createFromParcel(parcel: Parcel): ListStoryItem {
            return ListStoryItem(parcel)
        }

        override fun newArray(size: Int): Array<ListStoryItem?> {
            return arrayOfNulls(size)
        }
    }
}

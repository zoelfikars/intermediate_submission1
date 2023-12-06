package dicoding.zulfikar.storyapp.data.models

import android.os.Parcel
import android.os.Parcelable
import java.io.File

data class StoryModel(
    val name: String,
    val description: String,
    val photoUrl: String,
    val photo: File? = null,
    val lat: Float? = null,
    val lon: Float? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readSerializable() as? File,
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Float::class.java.classLoader) as? Float
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(photoUrl)
        parcel.writeSerializable(photo)
        parcel.writeValue(lat)
        parcel.writeValue(lon)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StoryModel> {
        override fun createFromParcel(parcel: Parcel): StoryModel {
            return StoryModel(parcel)
        }

        override fun newArray(size: Int): Array<StoryModel?> {
            return arrayOfNulls(size)
        }
    }
}

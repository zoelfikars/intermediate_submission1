package dicoding.zulfikar.storyapp.data.local.room

import androidx.paging.PagingSource
import androidx.room.*
import dicoding.zulfikar.storyapp.data.remote.response.ListStoryItem

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<ListStoryItem>)

    @Query("SELECT * FROM story")
    fun getPagingStory(): PagingSource<Int, ListStoryItem>

    @Query("SELECT * FROM story")
    suspend fun getStory(): List<ListStoryItem>

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}
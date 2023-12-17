package dicoding.zulfikar.storyapp.data

import androidx.paging.*
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dicoding.zulfikar.storyapp.data.local.room.StoryDatabase
import dicoding.zulfikar.storyapp.data.models.UserModel
import dicoding.zulfikar.storyapp.data.remote.response.ListStoryItem
import dicoding.zulfikar.storyapp.data.remote.response.LoginResponse
import dicoding.zulfikar.storyapp.data.remote.response.MessageResponse
import dicoding.zulfikar.storyapp.data.remote.response.StoryResponse
import dicoding.zulfikar.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class StoryRemoteMediatorTest {
    private var mockDb: StoryDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        StoryDatabase::class.java
    ).allowMainThreadQueries().build()
    private var apiService: FakeApiService = FakeApiService()
    private var token: String =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLWxBeUVvaWRPQ3Nrby0wXzgiLCJpYXQiOjE3MDI2NTU2MDd9.npzFS0ucT0sFucdvCY0ff61TzJKVHNMmPPunrqPk9tg"

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        val remoteMediator = StoryRemoteMediator(
            mockDb,
            token,
            apiService
        )
        val pagingState = PagingState<Int, ListStoryItem>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }
}

class FakeApiService : ApiService {
    override suspend fun register(name: String, email: String, password: String): MessageResponse {
        return MessageResponse(false, "User registered successfully")
    }

    override suspend fun login(email: String, password: String): LoginResponse {
        return LoginResponse(UserModel("", "", "", false), false, "Login successfully")
    }

    override suspend fun getStories(): StoryResponse {
        return StoryResponse(listStory = listOf(), story = listOf(), false, "success")
    }

    override suspend fun getPagingStories(page: Int, size: Int): StoryResponse {
        val items: MutableList<ListStoryItem> = arrayListOf()

        for (i in 0..100) {
            val quote = ListStoryItem(
                "photoUrl : ${items[i].photoUrl}",
                "id : ${items[i].id}",
                "name : ${items[i].name}",
                "createdAt : ${items[i].createdAt}",
                items[i].lon,
                "id : ${items[i].id}",
                items[i].lat
            )
            items.add(quote)
        }
        return StoryResponse(items, items, false, "Success")
    }

    override suspend fun uploadImage(
        photo: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): MessageResponse {
        return MessageResponse(false, "Success")
    }

    override suspend fun uploadWithoutLocation(
        photo: MultipartBody.Part,
        description: RequestBody
    ): MessageResponse {
        return MessageResponse(false, "Success")
    }
}
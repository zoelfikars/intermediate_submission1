package dicoding.zulfikar.storyapp

import dicoding.zulfikar.storyapp.data.remote.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                "photoUrl : $i",
                "id : $i",
                "name : $i",
                "createdAt : $i",
                i.toDouble(),
                "id : $i",
                i.toDouble()
            )
            items.add(story)
        }
        return items
    }

    fun generateDummyToken(): String{
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLWxBeUVvaWRPQ3Nrby0wXzgiLCJpYXQiOjE3MDI2NTU2MDd9.npzFS0ucT0sFucdvCY0ff61TzJKVHNMmPPunrqPk9tg"
    }

}
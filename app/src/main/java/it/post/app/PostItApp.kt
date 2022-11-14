package it.post.app

import android.app.Application
import it.post.app.data.StoryRepository
import it.post.app.data.local.PreferenceManager
import it.post.app.data.remote.NetworkModule

class PostItApp : Application() {

    private val preferenceManager: PreferenceManager by lazy {
        PreferenceManager(applicationContext)
    }

    val storyRepository: StoryRepository by lazy {
        StoryRepository(NetworkModule.storyApi, preferenceManager)
    }
}

package de.tolunla.parsetagram

import android.app.Application
import com.parse.Parse
import com.parse.ParseObject
import de.tolunla.parsetagram.model.Follow
import de.tolunla.parsetagram.model.Post

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG)
        ParseObject.registerSubclass(Post::class.java)
        ParseObject.registerSubclass(Follow::class.java)

        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        )
    }
}
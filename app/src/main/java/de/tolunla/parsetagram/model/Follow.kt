package de.tolunla.parsetagram.model

import com.parse.*
import com.parse.ktx.findAll
import com.parse.ktx.putOrIgnore

@ParseClassName("Follow")
class Follow : ParseObject() {

    var user: ParseUser?
        get() = getParseUser(KEY_USER)
        set(user) = putOrIgnore(KEY_USER, user)

    var following: ParseUser?
        get() = getParseUser(KEY_FOLLOWING)
        set(user) = putOrIgnore(KEY_FOLLOWING, user)

    companion object {
        const val KEY_USER = "user"
        const val KEY_FOLLOWING = "following"

        fun ParseUser.following(): List<ParseUser?> {
            val following = ParseQuery(Follow::class.java)
                .whereEqualTo(KEY_USER, this)
                .findAll()

            return following.map { it.following }
        }

        fun ParseUser.followers(): List<ParseUser?> {
            val following = ParseQuery(Follow::class.java)
                .whereEqualTo(KEY_FOLLOWING, this)
                .findAll()

            return following.map { it.following }
        }

        fun ParseUser.isFollowing(user: ParseUser?): Boolean {
            val count = ParseQuery(Follow::class.java)
                .whereEqualTo(KEY_USER, this)
                .whereEqualTo(KEY_FOLLOWING, user)
                .count()

            return count > 0
        }

        fun ParseUser.follow(user: ParseUser, callback: SaveCallback) {
            val follow = Follow().apply {
                this.user = this@follow
                following = user
            }

            follow.saveInBackground(callback)
        }

        fun ParseUser.unfollow(user: ParseUser) {
            val follows = ParseQuery(Follow::class.java)
                .whereEqualTo(KEY_USER, this)
                .whereEqualTo(KEY_FOLLOWING, user)
                .findAll()

            deleteAll(follows)
        }
    }
}
package de.tolunla.parsetagram.model

import com.parse.*
import com.parse.ktx.findAll
import com.parse.ktx.putOrIgnore

@ParseClassName("Like")
class Like : ParseObject() {

    var user: ParseUser?
        get() = getParseUser(KEY_USER)
        set(user) = putOrIgnore(KEY_USER, user)

    var post: Post?
        get() = getParseObject(KEY_POST) as Post?
        set(post) = putOrIgnore(KEY_POST, post)

    companion object {
        const val KEY_USER = "user"
        const val KEY_POST = "post"

        fun Post.likeCount(): Int {
            return ParseQuery(Like::class.java)
                .whereEqualTo(KEY_POST, this)
                .count()
        }

        fun Post.isLiked(user: ParseUser?): Boolean {
            val likes = ParseQuery(Like::class.java)
                .whereEqualTo(KEY_USER, user)
                .whereEqualTo(KEY_POST, this)
                .count()

            return likes > 0
        }

        fun ParseUser.liked(): List<Post?> {
            val likes = ParseQuery(Like::class.java)
                .whereEqualTo(KEY_USER, this)
                .findAll()

            return likes.map { it.post }
        }

        fun ParseUser.unlike(post: Post?) {
            val likes = ParseQuery(Like::class.java)
                .whereEqualTo(KEY_USER, this)
                .whereEqualTo(KEY_POST, post)
                .findAll()

            deleteAll(likes)
        }

        fun ParseUser.like(post: Post?, callback: SaveCallback) {
            val like = Like().apply {
                this.user = this@like
                this.post = post
            }

            like.saveInBackground(callback)
        }
    }
}
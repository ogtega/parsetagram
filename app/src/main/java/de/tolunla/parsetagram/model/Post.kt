package de.tolunla.parsetagram.model

import com.parse.*
import com.parse.ktx.putOrIgnore

@ParseClassName("Post")
class Post : ParseObject() {
    var user: ParseUser?
        get() = getParseUser(KEY_USER)
        set(user) = putOrIgnore(KEY_USER, user)

    var image: ParseFile?
        get() = getParseFile(KEY_IMAGE)
        set(file) = putOrIgnore(KEY_IMAGE, file)

    var caption: String?
        get() = getString(KEY_CAPTION)
        set(caption) = putOrIgnore(KEY_CAPTION, caption)

    companion object {
        const val KEY_IMAGE = "image"
        const val KEY_USER = "user"
        const val KEY_CAPTION = "caption"
    }
}
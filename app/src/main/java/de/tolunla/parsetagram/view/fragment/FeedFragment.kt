package de.tolunla.parsetagram.view.fragment

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.parse.ParseUser
import de.tolunla.parsetagram.R

class FeedFragment: Fragment() {

    override fun onStart() {
        super.onStart()

        if (ParseUser.getCurrentUser() == null) {
            findNavController().navigate(R.id.action_feed_dst_to_register_dst)
        }
    }
}
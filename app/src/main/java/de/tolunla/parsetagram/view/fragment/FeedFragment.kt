package de.tolunla.parsetagram.view.fragment

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.parse.ParseUser

class FeedFragment : Fragment() {

    override fun onStart() {
        super.onStart()

        if (ParseUser.getCurrentUser() == null) {
            findNavController().navigate(FeedFragmentDirections.actionFeedDstToRegisterDst())
        }
    }
}
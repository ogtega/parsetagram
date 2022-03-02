package de.tolunla.parsetagram.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import de.tolunla.parsetagram.databinding.FeedFragmentBinding
import de.tolunla.parsetagram.view.adapter.FeedListAdapter
import de.tolunla.parsetagram.view.fragment.FeedFragment.Companion.getSearchResultStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ExploreFragment : Fragment() {

    private lateinit var binding: FeedFragmentBinding
    private lateinit var feedAdapter: FeedListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FeedFragmentBinding.inflate(inflater)
        feedAdapter = FeedListAdapter(findNavController())
        binding.feedList.adapter = feedAdapter
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val query = ParseQuery<ParseObject>("Post")
        query.whereNotContainedIn("user", listOf<ParseUser>(ParseUser.getCurrentUser()))

        lifecycleScope.launch(Dispatchers.IO) {
            getSearchResultStream(query).collect { data ->
                feedAdapter.submitData(data)
            }
        }
    }
}
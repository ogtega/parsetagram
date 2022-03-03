package de.tolunla.parsetagram.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import com.parse.ParseQuery
import com.parse.ParseUser
import de.tolunla.parsetagram.databinding.FeedFragmentBinding
import de.tolunla.parsetagram.model.Like.Companion.liked
import de.tolunla.parsetagram.model.Post
import de.tolunla.parsetagram.view.adapter.FeedListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment() {

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
        val query = ParseQuery<Post>("Post")
        val posts = ParseUser.getCurrentUser().liked().map { it?.objectId }
        query.whereContainedIn("objectId", posts)

        binding.swipeRefresh.setOnRefreshListener {
            updateList(query)
        }

        updateList(query)
    }

    private fun updateList(query: ParseQuery<Post>) {
        feedAdapter.submitData(lifecycle, PagingData.empty())
        lifecycleScope.launch(Dispatchers.IO) {
            FeedFragment.getSearchResultStream(query).collect { data ->
                feedAdapter.submitData(data)
            }
        }

        binding.swipeRefresh.isRefreshing = false
    }
}
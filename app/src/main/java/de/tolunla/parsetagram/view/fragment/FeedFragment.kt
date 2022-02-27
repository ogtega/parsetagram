package de.tolunla.parsetagram.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import de.tolunla.parsetagram.databinding.FeedFragmentBinding
import de.tolunla.parsetagram.util.ParseChronoPagingSource
import de.tolunla.parsetagram.view.adapter.FeedListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FeedFragment : Fragment() {

    private lateinit var binding: FeedFragmentBinding
    private val feedAdapter = FeedListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FeedFragmentBinding.inflate(inflater)
        binding.feedList.adapter = feedAdapter
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        if (ParseUser.getCurrentUser() == null) {
            findNavController().navigate(FeedFragmentDirections.actionFeedDstToRegisterDst())
        }


        val query = ParseQuery<ParseObject>("Post")

        lifecycleScope.launch(Dispatchers.IO) {
            getSearchResultStream(query).collect { data ->
                feedAdapter.submitData(data)
            }
        }
    }

    private fun getSearchResultStream(query: ParseQuery<ParseObject>): Flow<PagingData<ParseObject>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { ParseChronoPagingSource(query) }
        ).flow
    }
}
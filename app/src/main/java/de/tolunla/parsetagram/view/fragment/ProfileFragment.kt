package de.tolunla.parsetagram.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import com.parse.ktx.putOrRemove
import de.tolunla.parsetagram.R
import de.tolunla.parsetagram.databinding.ProfileFeedFragmentBinding
import de.tolunla.parsetagram.view.adapter.ProfileFeedListAdapter
import de.tolunla.parsetagram.view.fragment.FeedFragment.Companion.getSearchResultStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var binding: ProfileFeedFragmentBinding
    private lateinit var optionsMenu: Menu
    private lateinit var parseUser: ParseUser

    private val feedAdapter = ProfileFeedListAdapter()
    private val args: ProfileFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = ProfileFeedFragmentBinding.inflate(inflater)
        binding.feedList.adapter = feedAdapter
        binding.feedList.layoutManager = GridLayoutManager(context, 3)

        parseUser = if (args.username == null)
            ParseUser.getCurrentUser() else
            ParseUser.getQuery().whereEqualTo("username", args.username).first

        (activity as AppCompatActivity).supportActionBar?.title = parseUser.username

        binding.fullnameTv.text = parseUser.getString("fullname")

        feedAdapter.selectable = parseUser.isAuthenticated

        binding.logoutBtn.visibility = if (parseUser.isAuthenticated) View.VISIBLE else View.GONE

        binding.logoutBtn.setOnClickListener {
            ParseUser.logOutInBackground { exception ->
                if (exception != null) {
                    return@logOutInBackground
                }

                val action = ProfileFragmentDirections.actionProfileDstToRegisterDst()
                findNavController().navigate(action)
            }
        }

        binding.followBtn.setOnClickListener {
            ParseUser.getCurrentUser().addUnique("following", parseUser)
        }

        binding.unfollowBtn.setOnClickListener {
//            TODO: Unfollow users
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val query = ParseQuery<ParseObject>("Post")
        query.whereContainedIn("user", listOf(parseUser))

        feedAdapter.onPagesUpdatedFlow.asLiveData().observe(viewLifecycleOwner) {
            binding.postCountTv.text = feedAdapter.itemCount.toString()
            binding.postCountLabel.text = if (feedAdapter.itemCount == 1) "Post" else "Posts"
        }

        feedAdapter.liveSelected.observe(viewLifecycleOwner) { selected ->
            val deleteItems = optionsMenu.findItem(R.id.delete_post)

            if (selected.isNotEmpty()) {
                deleteItems.isVisible = true

                deleteItems.setOnMenuItemClickListener {
                    ParseQuery<ParseObject>("Post").whereContainedIn("objectId", selected.toList())
                        .findInBackground { objects, e ->
                            if (e != null) return@findInBackground
                            ParseObject.deleteAllInBackground(objects) { exception ->
                                if (exception != null) return@deleteAllInBackground
                                updateListFeed(query)
                            }
                        }
                    deleteItems.isVisible = false
                    true
                }
            } else {
                deleteItems.isVisible = false
            }
            Log.d(this::class.simpleName, selected.toString())
        }

        updateListFeed(query)
    }

    private fun updateListFeed(query: ParseQuery<ParseObject>) {
        feedAdapter.selected.clear()
        feedAdapter.submitData(lifecycle, PagingData.empty())
        lifecycleScope.launch(Dispatchers.IO) {
            getSearchResultStream(query).collect { data ->
                feedAdapter.submitData(data)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
        optionsMenu = menu
        super.onCreateOptionsMenu(menu, inflater)
    }
}
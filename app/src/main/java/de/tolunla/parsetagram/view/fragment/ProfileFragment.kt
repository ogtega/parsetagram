package de.tolunla.parsetagram.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import de.tolunla.parsetagram.R
import de.tolunla.parsetagram.databinding.ProfileFeedFragmentBinding
import de.tolunla.parsetagram.model.Follow.Companion.follow
import de.tolunla.parsetagram.model.Follow.Companion.followers
import de.tolunla.parsetagram.model.Follow.Companion.following
import de.tolunla.parsetagram.model.Follow.Companion.isFollowing
import de.tolunla.parsetagram.model.Follow.Companion.unfollow
import de.tolunla.parsetagram.model.Post
import de.tolunla.parsetagram.view.adapter.ProfileFeedListAdapter
import de.tolunla.parsetagram.view.fragment.FeedFragment.Companion.getSearchResultStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var binding: ProfileFeedFragmentBinding
    private lateinit var optionsMenu: Menu
    private lateinit var parseUser: ParseUser

    private val followers = MutableLiveData(0)

    private val feedAdapter = ProfileFeedListAdapter()
    private val args: ProfileFragmentArgs by navArgs()

    private lateinit var query: ParseQuery<Post>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = ProfileFeedFragmentBinding.inflate(inflater)
        binding.feedList.adapter = feedAdapter
        binding.feedList.layoutManager = GridLayoutManager(context, 3)

        parseUser =
            if (args.username == null || args.username == ParseUser.getCurrentUser().username)
                ParseUser.getCurrentUser() else
                ParseUser.getQuery().whereEqualTo("username", args.username).first

        query = ParseQuery<Post>("Post").whereContainedIn("user", listOf(parseUser))

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

        followers.value = parseUser.followers().count()

        if (!parseUser.isAuthenticated) {
            val following = ParseUser.getCurrentUser().isFollowing(parseUser)

            binding.followBtn.visibility = if (following) View.GONE else View.VISIBLE
            binding.unfollowBtn.visibility = if (following) View.VISIBLE else View.GONE

            binding.followBtn.setOnClickListener {
                ParseUser.getCurrentUser().follow(parseUser) { exception ->
                    if (exception != null) {
                        return@follow
                    }

                    binding.followBtn.visibility = View.GONE
                    binding.unfollowBtn.visibility = View.VISIBLE
                    followers.value = followers.value!!.plus(1)
                }
            }

            binding.unfollowBtn.setOnClickListener {
                ParseUser.getCurrentUser().unfollow(parseUser)

                binding.followBtn.visibility = View.VISIBLE
                binding.unfollowBtn.visibility = View.GONE
                followers.value = followers.value!!.minus(1)
            }
        }

        updateListFeed(query)

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        feedAdapter.onPagesUpdatedFlow.asLiveData().observe(viewLifecycleOwner) {
            binding.postCountTv.text = feedAdapter.itemCount.toString()
            binding.postCountLabel.text = if (feedAdapter.itemCount == 1) "Post" else "Posts"
        }

        followers.observe(viewLifecycleOwner) {
            binding.followerCountTv.text = it.toString()
            binding.followerCountLabel.text = if (it == 1) "Follower" else "Followers"
        }

        binding.followingCountTv.text = parseUser.following().count().toString()

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
    }

    private fun updateListFeed(query: ParseQuery<Post>) {
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
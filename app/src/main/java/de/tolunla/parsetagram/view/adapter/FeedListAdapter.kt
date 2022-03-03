package de.tolunla.parsetagram.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.parse.ParseUser
import de.tolunla.parsetagram.R
import de.tolunla.parsetagram.databinding.PostFeedItemBinding
import de.tolunla.parsetagram.model.Post

class FeedListAdapter(val navController: NavController) :
    PagingDataAdapter<Post, FeedListAdapter.ViewHolder>(FeedComparator) {
    private lateinit var inflater: LayoutInflater

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        inflater = LayoutInflater.from(recyclerView.context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PostFeedItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = getItem(position)

        post?.let {
            post.getParseFile("image")?.also {
                val file = it.file
                holder.binding.postImg.load(file)
            }

            post.getString("caption")?.let caption@{
                if (it.isBlank()) {
                    holder.binding.captionLayout.visibility = View.GONE
                    return@caption
                }

                holder.binding.captionTv.text = it

                post.getParseUser("user")?.fetchIfNeededInBackground { user: ParseUser, e ->
                    if (e != null) return@fetchIfNeededInBackground
                    holder.binding.usernameCaptionTv.text = user.username
                }
            }

            post.getParseUser("user")?.fetchIfNeededInBackground { user: ParseUser, e ->
                if (e != null) return@fetchIfNeededInBackground
                holder.binding.usernameTv.text = user.username

                val bundle = bundleOf("username" to user.username)

                if (user.username != ParseUser.getCurrentUser().username) {

                    holder.binding.usernameTv.setOnClickListener {
                        navController.navigate(R.id.profile_alt_dst, bundle)
                    }

                    holder.binding.usernameCaptionTv.setOnClickListener {
                        navController.navigate(R.id.profile_alt_dst, bundle)
                    }
                }
            }

            holder.binding.postLikeBtn.setOnClickListener { view ->
                view.isSelected = !view.isSelected
            }
        }
    }

    inner class ViewHolder(val binding: PostFeedItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    object FeedComparator : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.hasSameId(newItem)
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.hasSameId(newItem)
        }
    }
}
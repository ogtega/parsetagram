package de.tolunla.parsetagram.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.parse.ParseObject
import de.tolunla.parsetagram.databinding.PostFeedItemBinding

class FeedListAdapter : PagingDataAdapter<ParseObject, FeedListAdapter.ViewHolder>(FeedComparator) {
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
            post.getParseFile("image")?.let {
                val file = it.file
                holder.binding.postImg.load(file)
            }
        }
    }

    inner class ViewHolder(val binding: PostFeedItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    object FeedComparator : DiffUtil.ItemCallback<ParseObject>() {
        override fun areItemsTheSame(oldItem: ParseObject, newItem: ParseObject): Boolean {
            return oldItem.hasSameId(newItem)
        }

        override fun areContentsTheSame(oldItem: ParseObject, newItem: ParseObject): Boolean {
            return oldItem.hasSameId(newItem)
        }
    }
}
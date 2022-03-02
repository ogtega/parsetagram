package de.tolunla.parsetagram.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.parse.ParseObject
import de.tolunla.parsetagram.databinding.ProfilePostFeedItemBinding

class ProfileFeedListAdapter : PagingDataAdapter<ParseObject, ProfileFeedListAdapter.ViewHolder>(
    FeedListAdapter.FeedComparator
) {
    val liveSelected = MutableLiveData<Set<String>>()
    val selected = mutableSetOf<String>()
    private lateinit var inflater: LayoutInflater
    var selectable = true

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        inflater = LayoutInflater.from(recyclerView.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = getItem(position)

        post?.let {
            post.getParseFile("image")?.also {
                val file = it.file
                holder.binding.postImg.load(file)
            }

            holder.binding.selectedBtn.visibility =
                if (selected.isEmpty()) View.GONE else View.VISIBLE
            holder.binding.selectedBtn.isChecked = post.objectId in selected

            holder.binding.postImg.setOnClickListener {
                if (!selectable) return@setOnClickListener

                if (selected.isEmpty()) {
                    return@setOnClickListener
                }

                if (post.objectId in selected) {
                    selected.remove(post.objectId)

                    if (selected.isEmpty()) {
                        notifyDataSetChanged()
                        liveSelected.value = selected
                        return@setOnClickListener
                    }
                } else {
                    selected.add(post.objectId)
                }

                notifyItemChanged(position)
                liveSelected.value = selected
            }

            holder.binding.postImg.setOnLongClickListener {
                if (!selectable) return@setOnLongClickListener true

                if (selected.isEmpty() || post.objectId !in selected) {
                    selected.add(post.objectId)
                } else {
                    selected.remove(post.objectId)
                    if (selected.isEmpty()) {
                        notifyDataSetChanged()
                        liveSelected.value = selected
                        return@setOnLongClickListener true
                    }
                }

                if (selected.size == 1) {
                    notifyDataSetChanged()
                } else {
                    notifyItemChanged(position)
                }

                liveSelected.value = selected
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ProfilePostFeedItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(val binding: ProfilePostFeedItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}
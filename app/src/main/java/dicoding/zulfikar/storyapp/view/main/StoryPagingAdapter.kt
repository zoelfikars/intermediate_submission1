package dicoding.zulfikar.storyapp.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dicoding.zulfikar.storyapp.data.remote.response.ListStoryItem
import dicoding.zulfikar.storyapp.databinding.StoryListBinding

class StoryPagingAdapter(private val onItemClick: (ListStoryItem) -> Unit) :
    PagingDataAdapter<ListStoryItem, StoryPagingAdapter.StoryViewHolder>(DIFF_CALLBACK) {
    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StoryViewHolder {
        val binding = StoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding, onItemClick)

    }

    class StoryViewHolder(
        private val binding: StoryListBinding, private val onItemClick: (ListStoryItem) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStoryItem) {
            binding.root.setOnClickListener {
                onItemClick(data)
            }
            binding.tvItemNama.text = data.name
            if (data.description.length > 100) {
                val desc = "${data.description.take(100)}..."
                binding.tvItemDescription.text = desc
            } else {
                binding.tvItemDescription.text = data.description.take(100)
            }
            Glide.with(binding.root.context).load(data.photoUrl).into(binding.imgItemPhoto)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

}
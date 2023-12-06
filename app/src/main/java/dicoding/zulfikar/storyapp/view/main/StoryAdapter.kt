package dicoding.zulfikar.storyapp.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dicoding.zulfikar.storyapp.data.remote.response.ListStoryItem
import dicoding.zulfikar.storyapp.databinding.StoryListBinding

class StoryAdapter(private val clickListener: (ListStoryItem, ) -> Unit) : ListAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(DiffCallback()) {
    class StoryViewHolder(private val binding: StoryListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(storyItem: ListStoryItem) {
            binding.tvItemNama.text = storyItem.name
            if(storyItem.description.length > 100) {
                binding.tvItemDescription.text = "${storyItem.description.take(100)}..."
            } else {
                binding.tvItemDescription.text = storyItem.description.take(100)
            }
            Glide.with(binding.root.context).load(storyItem.photoUrl).into(binding.imgItemPhoto)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = StoryListBinding.inflate(inflater, parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val storyItem = getItem(position)
        holder.bind(storyItem)
        holder.itemView.setOnClickListener {
            clickListener(storyItem)
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<ListStoryItem>() {
        override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
            return oldItem == newItem
        }
    }
}

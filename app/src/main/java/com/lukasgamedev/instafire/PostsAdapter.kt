package com.lukasgamedev.instafire

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lukasgamedev.instafire.databinding.ItemPostBinding
import com.lukasgamedev.instafire.models.Post

class PostsAdapter(val context: Context, val posts: List<Post>):
    RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    private var _binding: ItemPostBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding = ItemPostBinding.inflate(LayoutInflater.from(context), parent, false)
        val view = binding.root
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount() = posts.size

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(post: Post) {
            binding.tvUsername.text = post.user?.username
            binding.tvDescription.text = post.description
            Glide.with(context).load(post.imageUrl).into(binding.ivPost)
            binding.tvRelativeTime.text = DateUtils.getRelativeTimeSpanString(post.creationTimeMs)
        }
    }
}
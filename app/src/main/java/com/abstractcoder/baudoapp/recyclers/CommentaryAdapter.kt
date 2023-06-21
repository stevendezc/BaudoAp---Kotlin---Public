package com.abstractcoder.baudoapp.recyclers

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.Commentary
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.databinding.CommentListItemBinding
import com.abstractcoder.baudoapp.utils.CommentaryDialog

class CommentaryAdapter(private val context: Context, private val fragmentManager: FragmentManager, private val postId: String, private val userId: String, private val commentaryList: ArrayList<Commentary>): RecyclerView.Adapter<CommentaryAdapter.CommentHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.comment_list_item,
            parent, false)
        return CommentHolder(itemView)
    }

    override fun onBindViewHolder(holder: CommentHolder, position: Int) {
        val currentItem = commentaryList[position]
        if (currentItem != null) {
            holder.commentAuthor.text = currentItem.author
            holder.commentText.text = currentItem.text
        }

        holder.commentContainer.setOnLongClickListener {
            CommentaryDialog(
                currentItem.id!!,
                currentItem.author_email!!,
                currentItem.text!!,
                postId,
                userId
            ).show(fragmentManager, "comment dialog")
            true
        }
    }

    override fun getItemCount(): Int {
        return commentaryList.size
    }

    class CommentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = CommentListItemBinding.bind(itemView)
        val commentContainer = binding.commentContainer
        val commentAuthor = binding.commentAuthor
        val commentText = binding.commentText

    }
}
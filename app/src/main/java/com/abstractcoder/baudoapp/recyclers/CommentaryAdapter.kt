package com.abstractcoder.baudoapp.recyclers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.Commentary
import com.abstractcoder.baudoapp.FirebaseUser
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.databinding.CommentListItemBinding
import com.abstractcoder.baudoapp.utils.CommentaryDialog
import com.abstractcoder.baudoapp.utils.Firestore
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class CommentaryAdapter(private val context: AppCompatActivity, private val fragmentManager: FragmentManager, private val postId: String, private val userId: String, private val commentaryList: ArrayList<Commentary>): RecyclerView.Adapter<CommentaryAdapter.CommentHolder>() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.comment_list_item,
            parent, false)
        return CommentHolder(itemView)
    }

    override fun onBindViewHolder(holder: CommentHolder, position: Int) {
        val currentItem = commentaryList[position]
        if (currentItem != null) {
            db.collection("users").get().addOnSuccessListener {
                val usersDataList = mutableListOf<FirebaseUser>()
                for (document in it) {
                    println("DOCUMENT: $document")
                    var userData = document.toObject(FirebaseUser::class.java)
                    userData.id = document.id
                    usersDataList.add(userData)
                }
                val user = usersDataList.find { it.id == currentItem.author_email }
                if (user != null) {
                    val userImage = user.user_pic
                    Glide.with(holder.commentAuthorImage)
                        .load(userImage)
                        .into(holder.commentAuthorImage)
                    holder.commentAuthor.text = user.name
                }
            }
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
        val commentAuthorImage = binding.commentAuthorImage
        val commentAuthor = binding.commentAuthor
        val commentText = binding.commentText

    }
}
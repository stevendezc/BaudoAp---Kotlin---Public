package com.abstractcoder.baudoapp.utils

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.databinding.CommentDialogBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class CommentaryDialog(
    private val commentId: String,
    private val commentAuthor: String,
    private val commentText: String,
    private val postId: String,
    private val userId: String
): DialogFragment() {
    private lateinit var binding: CommentDialogBinding

    private val db = FirebaseFirestore.getInstance()
    val commentsCollectionRef = db.collection("commentaries")
    val usersCollectionRef = db.collection("users")
    val postsCollectionRef = db.collection("posts")

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreate(savedInstanceState)
        binding = CommentDialogBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

        setup()

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        return dialog
    }

    private fun setup() {
        val dialogContext = context
        binding.commentCopy.setOnClickListener {
            val clipboard = dialogContext?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", commentText)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(dialogContext, "Comentario copiado", Toast.LENGTH_SHORT).show()
            dismiss()
        }
        if (commentAuthor != userId) {
            binding.commentDelete.visibility = TextView.GONE
        } else {
            binding.commentDelete.setOnClickListener {
                deleteCommentDocument(dialogContext!!)
                dismiss()
            }
        }
    }

    private fun deleteCommentDocument(dialogContext: Context) {
        val documentRef = commentsCollectionRef.document(commentId)
        documentRef.delete()
            .addOnSuccessListener {
                deleteUserAndPostComment(dialogContext)
            }
            .addOnFailureListener { e ->
                Toast.makeText(dialogContext, "Error en borrado", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteUserAndPostComment(dialogContext: Context) {
        val userDocumentRef = usersCollectionRef.document(userId)
        userDocumentRef.update("commentaries", FieldValue.arrayRemove(commentId))
            .addOnSuccessListener {
                val postDocumentRef = postsCollectionRef.document(postId)
                postDocumentRef.update("commentaries", FieldValue.arrayRemove(commentId))
                    .addOnCompleteListener {
                        Toast.makeText(dialogContext, "Comentario eliminado", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(dialogContext, "Error en borrado", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(dialogContext, "Error en borrado", Toast.LENGTH_SHORT).show()
            }
    }
}
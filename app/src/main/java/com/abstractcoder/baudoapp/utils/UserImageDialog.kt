package com.pereira.baudoapp.utils

import android.app.Activity
import android.app.Dialog
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.pereira.baudoapp.FirebaseUser
import com.pereira.baudoapp.R
import com.pereira.baudoapp.databinding.UserImageDialogBinding
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
// Image compression
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.text.toSpannable
import java.io.ByteArrayOutputStream
import java.io.InputStream

class UserImageDialog(
    private val userData: FirebaseUser,
    private val email: String
): DialogFragment() {
    private lateinit var binding: UserImageDialogBinding
    val storageReference = FirebaseStorage.getInstance().reference

    private var userImageUri: Uri? = null
    private var userName: String? = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreate(savedInstanceState)
        binding = UserImageDialogBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

        setup(email!!)

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setGravity(Gravity.TOP)
        dialog.window!!.attributes.windowAnimations = R.style.UserDialogAnimation
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    private fun setup(email: String) {
        println("UserPic: ${userData.user_pic}")

        this.userName = userData.name
        binding.editName.setText(userName)
        if (userData.user_pic != "") {
            binding.uploadText.text = "Actualizando imagen de usuario..."
            Glide.with(binding.userImage)
                .load(userData.user_pic)
                .into(binding.userImage)
        } else {
            binding.uploadText.text = "Cargando imagen de usuario..."
        }
        binding.userImage.setOnClickListener {
            println("Input image")
            //val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }
        binding.newUserImage.setOnClickListener {
            println("Input image")
            //val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }
        binding.cancelButton.setOnClickListener {
            dialog?.dismiss()
        }
        binding.loadButton.setOnClickListener {
            println("Preparing to load image into FireStore")
            if (this.userImageUri != null) {
                binding.options.visibility = LinearLayout.GONE
                binding.contentLoading.visibility = LinearLayout.VISIBLE
                println("Email: $email")
                val userid = userData.uid.lowercase().replace(" ", "_")
                val contentResolver: ContentResolver = context?.contentResolver!!
                // Get the input stream from the image URI
                val inputStream: InputStream? = contentResolver.openInputStream(this.userImageUri!!)
                // Decode the input stream into a bitmap
                val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)
                // Create a byte array output stream to hold the compressed image data
                val outputStream = ByteArrayOutputStream()
                // Compress the bitmap to the output stream with the desired quality (40%)
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 15, outputStream)
                // Convert the compressed image data to a byte array
                val compressedImageData: ByteArray = outputStream.toByteArray()
                val imageRef = storageReference.child("UserImages/$userid.jpg")
                imageRef.putBytes(compressedImageData)
                    .addOnSuccessListener {
                        // Get the download URL of the uploaded image
                        imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                            // Update the Firestore record with the image URL
                            println("User image uploaded succesfully to Firebase Storage")
                            val firestore = FirebaseFirestore.getInstance()
                            val documentRef = firestore.collection("users").document(email)

                            documentRef.update("user_pic", downloadUrl.toString())
                                .addOnSuccessListener {
                                    binding.loadingSpinner.visibility = ProgressBar.GONE
                                    binding.uploadText.text = "Imagen de usuario Actualizada!"
                                    println("User image updated")
                                    updateName(false)
                                }
                                .addOnFailureListener { exception ->
                                    // Handle the failure to update the image URL
                                }
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Handle the failure to upload the image
                    }
            } else {
                updateName(true)
            }
        }
    }

    private fun updateName(onlyNameChange: Boolean) {
        val fieldUserName = binding.editName.text.toString()
        if (fieldUserName == "") {
            binding.editName.setText(userName)
            dialog?.dismiss()
        } else {
            if (onlyNameChange) {
                binding.options.visibility = LinearLayout.GONE
                binding.contentLoading.visibility = LinearLayout.VISIBLE
                binding.uploadText.text = "Actualizando nombre de usuario..."
            }
            val firestore = FirebaseFirestore.getInstance()
            val documentRef = firestore.collection("users").document(email)

            documentRef.update("name", fieldUserName)
                .addOnSuccessListener {
                    if (onlyNameChange) {
                        binding.loadingSpinner.visibility = ProgressBar.GONE
                        binding.uploadText.text = "Nombre de usuario actualizado"
                    } else {
                        Toast.makeText(
                            this.requireContext(),
                            "Nombre de usuario actualizado",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    Thread.sleep(5000)
                    dialog?.dismiss()
                }
                .addOnFailureListener { exception ->
                    // Handle the failure to update the image URL
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1
            && resultCode == Activity.RESULT_OK) {
            this.userImageUri = data?.data
            if (userData.user_pic != "") {
                if (this.userImageUri != null) {
                    binding.userImageChangeView.visibility = LinearLayout.VISIBLE
                    binding.userImage.visibility = ShapeableImageView.GONE
                    if (userData.user_pic != "") {
                        Glide.with(binding.currentUserImage)
                            .load(userData.user_pic)
                            .into(binding.currentUserImage)
                        binding.newUserImage.setImageURI(this.userImageUri)
                    }
                }
            } else {
                // Set the selected image to the ImageView
                binding.userImage.setImageURI(this.userImageUri)
            }
        }
    }

}
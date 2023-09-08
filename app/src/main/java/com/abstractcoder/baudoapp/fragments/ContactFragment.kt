package com.pereira.baudoapp.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.pereira.baudoapp.FragmentButtonClickListener
import com.pereira.baudoapp.databinding.FragmentContactBinding
import com.pereira.baudoapp.utils.EmailMessage
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class ContactFragment : Fragment() {
    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()

    private var buttonClickListener: FragmentButtonClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            buttonClickListener?.onButtonClicked()
        }

        binding.contactSubmit.setOnClickListener {
            if (binding.contactName.text.isNotEmpty() &&
                binding.contactSubject.text.isNotEmpty() &&
                binding.contactEmail.text.isNotEmpty() &&
                binding.contactMessage.text.isNotEmpty()) {
                sendEmail()
            } else {
                Toast.makeText(requireContext(), "No se ha diligenciado todos los datos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentButtonClickListener) {
            buttonClickListener = context
        } else {
            throw RuntimeException("$context must implement FragmentButtonClickListener")
        }
    }

    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val timeZone = TimeZone.getTimeZone("America/Bogota") // Colombia's time zone
            dateFormat.timeZone = timeZone
        }

        return dateFormat.format(Date())
    }

    private fun sendEmail() {
        val name = binding.contactName.text.toString()
        val subject = binding.contactSubject.text.toString()
        val email = binding.contactEmail.text
        val message = binding.contactMessage.text.toString()

        val recipient = "alejandropenagos1998@gmail.com"

        val currentDate = getCurrentDateTime()
        val documentName = "${email}-$currentDate"
        val receivers = listOf<String>(recipient)
        val emailMessage = EmailMessage(
            subject = subject,
            html = "<h2>Mensaje de contacto Baudoap</h2><br>" +
                    "<p>$message</p><br>" +
                    "<p>Cordialmente<b> $name</b></p><br>" +
                    "<img src='https://baudoap.com/wp-content/uploads/2017/10/logo.png'></img>"
        )
        db.collection("mail").document(documentName).set(
            mapOf(
                "to" to receivers,
                "message" to emailMessage
            )
        ).addOnSuccessListener {
            Toast.makeText(requireContext(), "Mensaje enviado", Toast.LENGTH_SHORT).show()
            binding.contactName.setText("")
            binding.contactSubject.setText("")
            binding.contactEmail.setText("")
            binding.contactMessage.setText("")
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "El mensaje no se pudo enviar correctamente", Toast.LENGTH_SHORT).show()
        }
    }
}
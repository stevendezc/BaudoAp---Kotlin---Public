package com.abstractcoder.baudoapp.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.abstractcoder.baudoapp.FragmentButtonClickListener
import com.abstractcoder.baudoapp.databinding.FragmentContactBinding

class ContactFragment : Fragment() {
    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!

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

    fun sendEmail() {
        val name = binding.contactName.text
        val subject = binding.contactSubject.text
        val email = binding.contactEmail.text
        val message = binding.contactMessage.text

        val recipient = "alejandropenagos1998@gmail.com"
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, "Name: $name\nEmail: $email\n\n$message")
        }

        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "No email app found", Toast.LENGTH_SHORT).show()
        }
    }
}
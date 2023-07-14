package com.abstractcoder.baudoapp.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.abstractcoder.baudoapp.OnFragmentInteractionListener
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.databinding.FragmentCheckoutContactBinding

class CheckoutContactFragment : Fragment() {
    private var _binding: FragmentCheckoutContactBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCheckoutContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun validateForm(): Boolean {
        var validForm = true
        val inputFields = arrayOf(
            binding.contactEmail, binding.contactName, binding.contactAddress,
            binding.contactApartment, binding.contactCity, binding.contactPhone
        )
        for (input in inputFields) {
            val inputContent = input.text
            if (inputContent.isEmpty()) {
                input.error = "El campo no puede estar vacio"
                validForm = false
            }
            if (input == binding.contactEmail && !Patterns.EMAIL_ADDRESS.matcher(inputContent).matches()) {
                input.error = "El correo electronico no es valido"
                validForm = false
            }
        }
        return validForm
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            listener?.onDataReceived("back")
        }

        binding.shippmentPolicyButton.setOnClickListener {
            listener?.onDataReceived("policy")
        }

        binding.checkputContactSubmit.setOnClickListener {
            if (validateForm()) {
                listener?.onDataReceived("payment")
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }
}
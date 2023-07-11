package com.abstractcoder.baudoapp.fragments

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.abstractcoder.baudoapp.OnFragmentInteractionListener
import com.abstractcoder.baudoapp.databinding.FragmentCheckoutPaymentBinding
import com.braintreepayments.cardform.view.CardForm

class CheckoutPaymentFragment : Fragment() {
    private var _binding: FragmentCheckoutPaymentBinding? = null
    private val binding get() = _binding!!

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCheckoutPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            listener?.onDataReceived("contact")
        }

        binding.paymentSubmit.setOnClickListener {
            val validPaymentForm = checkPaymentForm()
            if (validPaymentForm) {
                Toast.makeText(requireContext(), "Informacion de pago valida", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Informacion de pago invalida", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPaymentForm(): Boolean {
        if (!binding.cardFormCardHolder.isValid) return false
        if (!binding.cardFormCardNumber.isValid) return false
        if (!binding.cardFormCardExpiration.isValid) return false
        if (!binding.cardFormCardCvv.isValid) return false
        return true
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
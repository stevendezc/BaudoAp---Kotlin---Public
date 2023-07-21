package com.abstractcoder.baudoapp.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abstractcoder.baudoapp.OnFragmentInteractionListener
import com.abstractcoder.baudoapp.databinding.FragmentCheckoutContactBinding
import com.abstractcoder.baudoapp.utils.API.PostsService
import com.abstractcoder.baudoapp.utils.API.PostsServiceImpl
import com.abstractcoder.baudoapp.utils.CheckoutData
import com.abstractcoder.baudoapp.utils.ContactInfo
import com.abstractcoder.baudoapp.utils.wompi.WompiKeys
import kotlinx.coroutines.*
import retrofit2.awaitResponse

class CheckoutContactFragment : Fragment() {
    private var _binding: FragmentCheckoutContactBinding? = null
    private val binding get() = _binding!!

    private var listener: OnFragmentInteractionListener? = null
    private var sharedCheckoutData: CheckoutData = CheckoutData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCheckoutContactBinding.inflate(inflater, container, false)

        val checkout_data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("checkout_data", CheckoutData::class.java)
        } else {
            arguments?.getParcelable("checkout_data") as? CheckoutData
        }
        if (checkout_data != null) {
            sharedCheckoutData = checkout_data
            fillContactData()
        }
        println("checkout_data in contact: $checkout_data")

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

    private fun fillContactData() {
        binding.contactEmail.setText(sharedCheckoutData.contact_info?.contact_email)
        binding.contactName.setText(sharedCheckoutData.contact_info?.contact_name)
        binding.contactAddress.setText(sharedCheckoutData.contact_info?.contact_address)
        binding.contactApartment.setText(sharedCheckoutData.contact_info?.contact_apartment)
        binding.contactCity.setText(sharedCheckoutData.contact_info?.contact_city)
        binding.contactPhone.setText(sharedCheckoutData.contact_info?.contact_phone)
    }

    private fun fillCheckoutData(type: String):CheckoutData {
        return CheckoutData(
            type,
            ContactInfo(
                binding.contactEmail.text.toString(),
                binding.contactName.text.toString(),
                binding.contactAddress.text.toString(),
                binding.contactApartment.text.toString(),
                binding.contactCity.text.toString(),
                binding.contactPhone.text.toString()
            ),
            sharedCheckoutData.pse_payment_info,
            sharedCheckoutData.cc_payment_info
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            val checkoutData = fillCheckoutData("back")
            listener?.onDataReceived(checkoutData)
        }

        binding.shippmentPolicyButton.setOnClickListener {
            val checkoutData = fillCheckoutData("policy")
            listener?.onDataReceived(checkoutData)
        }

        binding.checkputContactSubmit.setOnClickListener {
            if (validateForm()) {
                val checkoutData = fillCheckoutData("payment")
                listener?.onDataReceived(checkoutData)
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

    companion object {
        fun newInstance(checkout_data: CheckoutData): CheckoutContactFragment {
            val fragment = CheckoutContactFragment()
            val args = Bundle()
            args.putParcelable("checkout_data", checkout_data)
            fragment.arguments = args
            return fragment
        }
    }
}
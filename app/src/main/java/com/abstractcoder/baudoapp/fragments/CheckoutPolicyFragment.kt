package com.abstractcoder.baudoapp.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abstractcoder.baudoapp.OnFragmentInteractionListener
import com.abstractcoder.baudoapp.databinding.FragmentCheckoutPolicyBinding
import com.abstractcoder.baudoapp.utils.CheckoutData
import com.abstractcoder.baudoapp.utils.JsonFile
import org.json.JSONObject

class CheckoutPolicyFragment : Fragment() {
    private var _binding: FragmentCheckoutPolicyBinding? = null
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
        _binding = FragmentCheckoutPolicyBinding.inflate(inflater, container, false)

        val checkout_data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("checkout_data", CheckoutData::class.java)
        } else {
            arguments?.getParcelable("checkout_data") as? CheckoutData
        }
        if (checkout_data != null) {
            sharedCheckoutData = checkout_data
        }
        println("checkout_data in Policy: $checkout_data")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val jsonData = JsonFile().readJsonFile(requireContext(), "shipping_policy.json")
        val parsedJsonData = JSONObject(jsonData)

        binding.p1.text = parsedJsonData.get("p1").toString()
        binding.p2.text = parsedJsonData.get("p2").toString()
        binding.p3.text = parsedJsonData.get("p3").toString()
        binding.p4.text = parsedJsonData.get("p4").toString()
        binding.p5.text = parsedJsonData.get("p5").toString()

        binding.backButton.setOnClickListener {
            sharedCheckoutData.type = "contact"
            listener?.onDataReceived(sharedCheckoutData)
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
        fun newInstance(checkout_data: CheckoutData): CheckoutPolicyFragment {
            val fragment = CheckoutPolicyFragment()
            val args = Bundle()
            args.putParcelable("checkout_data", checkout_data)
            fragment.arguments = args
            return fragment
        }
    }
}
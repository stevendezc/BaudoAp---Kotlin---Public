package com.abstractcoder.baudoapp.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abstractcoder.baudoapp.OnFragmentInteractionListener
import com.abstractcoder.baudoapp.databinding.FragmentCheckoutPolicyBinding
import com.abstractcoder.baudoapp.utils.JsonFile
import org.json.JSONObject

class CheckoutPolicyFragment : Fragment() {
    private var _binding: FragmentCheckoutPolicyBinding? = null
    private val binding get() = _binding!!

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCheckoutPolicyBinding.inflate(inflater, container, false)
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
            listener?.onDataReceived("contact")
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
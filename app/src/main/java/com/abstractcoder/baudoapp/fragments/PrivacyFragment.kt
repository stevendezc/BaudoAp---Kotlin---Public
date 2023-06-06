package com.abstractcoder.baudoapp.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abstractcoder.baudoapp.FragmentButtonClickListener
import com.abstractcoder.baudoapp.databinding.FragmentPrivacyBinding

class PrivacyFragment : Fragment() {
    private var _binding: FragmentPrivacyBinding? = null
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
        _binding = FragmentPrivacyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            buttonClickListener?.onButtonClicked()
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
}
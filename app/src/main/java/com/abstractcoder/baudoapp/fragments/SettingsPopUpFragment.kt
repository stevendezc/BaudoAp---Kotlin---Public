package com.abstractcoder.baudoapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.databinding.FragmentSettingsPopUpBinding

class SettingsPopUpFragment : DialogFragment() {

    private var _binding: FragmentSettingsPopUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsPopUpBinding.inflate(inflater, container, false)
        return binding.root
    }

}
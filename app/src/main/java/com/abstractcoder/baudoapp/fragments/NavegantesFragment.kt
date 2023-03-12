package com.abstractcoder.baudoapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.databinding.FragmentNavegantesBinding

class NavegantesFragment : Fragment() {

    private var _binding: FragmentNavegantesBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNavegantesBinding.inflate(inflater, container, false)
        return binding.root
    }

}
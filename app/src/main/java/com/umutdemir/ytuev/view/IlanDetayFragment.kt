package com.umutdemir.ytuev.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.umutdemir.ytuev.R
import com.umutdemir.ytuev.databinding.FragmentIlanDetayBinding

class IlanDetayFragment : Fragment() {

    private lateinit var binding: FragmentIlanDetayBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentIlanDetayBinding.inflate(inflater,container,false)
        return binding.root

    }


}
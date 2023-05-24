package com.umutdemir.ytuev.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.umutdemir.ytuev.R
import com.umutdemir.ytuev.databinding.FragmentGelenKutusuBinding

class GelenKutusuFragment : Fragment() {

    private lateinit var binding : FragmentGelenKutusuBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentGelenKutusuBinding.inflate(inflater,container,false)
        return binding.root
    }



}
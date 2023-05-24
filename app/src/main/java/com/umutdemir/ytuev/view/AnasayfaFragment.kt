package com.umutdemir.ytuev.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.umutdemir.ytuev.R
import com.umutdemir.ytuev.adapter.IlanRecyclerAdapter
import com.umutdemir.ytuev.databinding.FragmentAnasayfaBinding
import com.umutdemir.ytuev.viewmodel.IlanViewModel


class AnasayfaFragment : Fragment() {

    private lateinit var binding: FragmentAnasayfaBinding
    private lateinit var viewModel: IlanViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAnasayfaBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.ilanRecyclerView.setHasFixedSize(true)
        binding.ilanRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel = ViewModelProvider(this)[IlanViewModel::class.java]
        viewModel.getIlanList(onSuccess = {
            val adapter = IlanRecyclerAdapter(it!!)
            binding.ilanRecyclerView.adapter = adapter
        }, onFailure = {

        })

        binding.floatingActionButton.setOnClickListener{
            val activity = requireActivity() as MainActivity
            activity.loadFragment(IlanEkleFragment())
        }
    }

    override fun onResume() {
        super.onResume()

    }

}
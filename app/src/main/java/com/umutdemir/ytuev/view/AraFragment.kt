package com.umutdemir.ytuev.view

import android.os.Binder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.umutdemir.ytuev.R
import com.umutdemir.ytuev.adapter.AraRecyclerAdapter
import com.umutdemir.ytuev.databinding.FragmentAraBinding
import com.umutdemir.ytuev.viewmodel.AraViewModel

class AraFragment : Fragment() {

    private lateinit var binding: FragmentAraBinding
    private lateinit var adapter: AraRecyclerAdapter
    private lateinit var viewModel: AraViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment


        binding = FragmentAraBinding.inflate(layoutInflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(requireActivity())[AraViewModel::class.java]
        binding.araRecyclerView.setHasFixedSize(true)
        binding.araRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.progressBar.visibility = View.VISIBLE
        viewModel.getUserList(onSuccess = {
            it?.let {
                adapter = AraRecyclerAdapter(it)
                binding.araRecyclerView.adapter = adapter
                binding.progressBar.visibility = View.INVISIBLE
                binding.araScrollView.visibility =  View.VISIBLE
                binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        adapter.filter.filter(newText)
                        return false
                    }

                })
            }


        }, onFailure = {

        })


    }

}
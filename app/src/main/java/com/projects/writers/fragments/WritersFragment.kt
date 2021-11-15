package com.projects.writers.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.projects.writers.R
import com.projects.writers.adapter.ViewPagerAdapter
import com.projects.writers.databinding.FragmentWritersBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class WritersFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private var _binding: FragmentWritersBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWritersBinding.inflate(inflater, container, false)
        viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewpager.adapter = viewPagerAdapter
        TabLayoutMediator(
            binding.tab, binding.viewpager
        ) { tab, position ->
            tab.text = list1()[position]
        }.attach()
        binding.search.setOnClickListener {
            findNavController().navigate(R.id.action_writers_to_searchFragment)
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WritersFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun list1(): ArrayList<String> {
        val list1 = ArrayList<String>()
        list1.add("Mumtoz")
        list1.add("O’zbek")
        list1.add("Jahon")
        return list1

    }
}
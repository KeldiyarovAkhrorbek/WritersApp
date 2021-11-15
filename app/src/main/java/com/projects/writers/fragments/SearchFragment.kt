package com.projects.writers.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.projects.writers.R
import com.projects.writers.adapter.RvAdapter
import com.projects.writers.databinding.FragmentSearchBinding
import com.projects.writers.models.Writer
import me.ibrahimsn.lib.SmoothBottomBar
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SearchFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var allList: ArrayList<Writer>
    private lateinit var adapter: RvAdapter
    private lateinit var firestore: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        allList = ArrayList()
        val temp = ArrayList<Writer>()
        getList()
        adapter = RvAdapter(temp, object : RvAdapter.OnItemClick {
            override fun onItemClick(writer: Writer, position: Int) {
                val bundle = Bundle()
                bundle.putSerializable("writer", writer)
                findNavController().navigate(R.id.writerFragment, bundle)
            }

            override fun onItemSaveClick(writer: Writer, position: Int) {
                writer.saved = !writer.saved
                adapter.notifyItemChanged(position)
                firestore.collection("writers").document(writer.id.toString()).set(writer)
            }
        })
        binding.rv.adapter = adapter
        binding.apply {
            searchView.requestFocus()
            searchView.setOnClickListener {
                searchView.isIconified = false
            }
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    searchData(p0.toString())
                    return false
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    return false
                }
            })
        }
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getList() {
        firestore.collection("writers")
            .get().addOnSuccessListener { result ->
                for (queryDocumentSnapshot in result) {
                    val writer = queryDocumentSnapshot.toObject(Writer::class.java)
                    allList.add(writer)
                }
                adapter.notifyDataSetChanged()
            }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onPause() {
        val bottomNav: SmoothBottomBar = requireActivity().findViewById(R.id.bottomBar)
        bottomNav.visibility = View.VISIBLE
        super.onPause()
    }

    override fun onResume() {
        val bottomNav: SmoothBottomBar = requireActivity().findViewById(R.id.bottomBar)
        bottomNav.visibility = View.GONE
        super.onResume()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun searchData(query: String) {
        val list1 = ArrayList<Writer>()
        allList.forEach {
            if (it.name?.lowercase()?.contains(query.lowercase()) == true)
                list1.add(it)
        }
        adapter.list = list1
        adapter.notifyDataSetChanged()
    }
}
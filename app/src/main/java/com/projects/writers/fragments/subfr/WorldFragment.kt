package com.projects.writers.fragments.subfr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.projects.writers.R
import com.projects.writers.adapter.RvAdapter
import com.projects.writers.databinding.FragmentWorldBinding
import com.projects.writers.models.Writer

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class WorldFragment : Fragment() {
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

    private var _binding: FragmentWorldBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore
    private lateinit var list: ArrayList<Writer>
    private lateinit var adapter: RvAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorldBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        list = ArrayList()
        getList()
        adapter = RvAdapter(list, object : RvAdapter.OnItemClick {
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

        return binding.root
    }

    private fun getList() {
        firestore.collection("writers")
            .get().addOnSuccessListener { result ->
                for (queryDocumentSnapshot in result) {
                    val writer = queryDocumentSnapshot.toObject(Writer::class.java)
                    if (writer.type.contentEquals("Jahon")) {
                        list.add(writer)
                    }
                }
                adapter.notifyDataSetChanged()
            }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WorldFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
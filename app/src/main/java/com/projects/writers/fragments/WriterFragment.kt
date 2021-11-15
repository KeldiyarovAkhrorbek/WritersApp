package com.projects.writers.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.projects.writers.R
import com.projects.writers.databinding.FragmentWriterBinding
import com.projects.writers.models.Writer
import com.squareup.picasso.Picasso
import me.ibrahimsn.lib.SmoothBottomBar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class WriterFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private var _binding: FragmentWriterBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWriterBinding.inflate(inflater, container, false)
        val bundle = arguments
        firestore = FirebaseFirestore.getInstance()

        val writer = bundle?.getSerializable("writer") as Writer
        binding.apply {
            if (writer.saved) {
                save.setCardBackgroundColor(Color.parseColor("#00B238"))
            } else {
                save.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            }
            info.text = writer.info
            date.text = "(${writer.birthYear} - ${writer.deathYear})"
            writerName.text = writer.name
            Picasso.get().load(writer.photoUrl).into(image)

            binding.backBtn.setOnClickListener {
                findNavController().popBackStack()
            }
            binding.save.setOnClickListener {
                if (writer.saved) {
                    writer.saved = false
                    save.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                    firestore.collection("writers")
                        .document(writer.id.toString())
                        .set(writer)
                } else {
                    writer.saved = true
                    save.setCardBackgroundColor(Color.parseColor("#00B238"))
                    firestore.collection("writers")
                        .document(writer.id.toString())
                        .set(writer)
                }
            }

            binding.search.setOnClickListener {
                searchCard.visibility = View.VISIBLE
                binding.search.visibility = View.GONE
                binding.save.visibility = View.GONE
                binding.backBtn.visibility = View.GONE
                searchView.isIconified = false

            }

            binding.searchView.setOnCloseListener(object : SearchView.OnCloseListener,
                androidx.appcompat.widget.SearchView.OnCloseListener {
                override fun onClose(): Boolean {
                    binding.searchCard.visibility = View.GONE
                    binding.search.visibility = View.VISIBLE
                    binding.save.visibility = View.VISIBLE
                    binding.backBtn.visibility = View.VISIBLE
                    binding.info.text = writer.info
                    return true
                }
            })

            binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    val textToHighlight = p0.toString()
                    val replaceWith =
                        "<span style='background-color:red'>$textToHighlight</span>"
                    val originalText = writer.info
                    val modifiedText = originalText?.replace(textToHighlight, replaceWith)
                    info.text = Html.fromHtml(modifiedText)
                    return false
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    return false
                }
            })
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WriterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onPause() {
        val bottomnav: SmoothBottomBar = requireActivity().findViewById(R.id.bottomBar)
        bottomnav.visibility = View.VISIBLE
        super.onPause()
    }

    override fun onResume() {
        val bottomnav: SmoothBottomBar = requireActivity().findViewById(R.id.bottomBar)
        bottomnav.visibility = View.GONE
        super.onResume()
    }
}
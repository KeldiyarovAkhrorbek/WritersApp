package com.projects.writers.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import com.projects.writers.R
import com.projects.writers.databinding.FragmentAddWriterBinding
import com.projects.writers.models.Writer
import com.squareup.picasso.Picasso
import me.ibrahimsn.lib.SmoothBottomBar

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AddWriterFragment : Fragment() {
    private var imgUrl: String = ""
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentAddWriterBinding? = null
    private val binding get() = _binding
    private lateinit var storage: FirebaseStorage
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var reference: StorageReference
    private lateinit var write: Writer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddWriterBinding.inflate(inflater, container, false)
        storage = FirebaseStorage.getInstance()
        val millis = System.currentTimeMillis()
        firebaseFirestore = FirebaseFirestore.getInstance()
        reference = storage.getReference("$millis")
        binding?.apply {
            chooseImage.setOnClickListener {
                onResultGallery()
            }
            saveBtn.setOnClickListener {
                if (isValidation()) {
                    firebaseFirestore.collection("writers")
                        .document(write.id.toString())
                        .set(write)
                        .addOnSuccessListener {
                            findNavController().popBackStack()
                        }.addOnFailureListener {
                            Toast.makeText(
                                requireContext(),
                                "Xatolik yuz berdi!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                }
            }
        }
        return binding?.root
    }


    private fun onResultGallery() {
        Dexter.withContext(requireContext())
            .withPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    getContent.launch("image/*")
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    if (response.isPermanentlyDenied) {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri: Uri =
                            Uri.fromParts("package", requireActivity().packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    } else {
                        response.requestedPermission
                    }

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: com.karumi.dexter.listener.PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    builder.setTitle("Gallery permission!")
                    builder.setMessage("Galleriyadan foydalanish uchun ruxsat bering!")
                    builder.setPositiveButton("Ruxsat so'rash!")
                    { _, _ ->
                        p1?.continuePermissionRequest()
                    }
                    builder.setNegativeButton("Ruxsat so'ramaslik!") { _, _ -> p1?.cancelPermissionRequest() }
                    builder.show()

                }

            }).check()
    }

    private var getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
                reference.putFile(it)
                    .addOnSuccessListener { result ->
                        if (result?.task?.isSuccessful == true) {
                            val downloadUrl = result.metadata?.reference?.downloadUrl
                            downloadUrl?.addOnSuccessListener { uri ->
                                imgUrl = uri.toString()
                                Picasso.get().load(imgUrl).resize(200, 200)
                                    .error(R.drawable.ic_launcher_background).into(binding?.image)
                            }
                        }
                    }
            }
        }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddWriterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun isValidation(): Boolean {
        val name = binding?.edName?.text.toString()
        val birthYear = binding?.edBirthYear?.text.toString()
        val deathYear = binding?.edDeathYear?.text.toString()
        val type = binding?.spinner?.selectedItem.toString()
        val info = binding?.edInfo?.text.toString()
        if (name == "") {
            Toast.makeText(requireContext(), "Ism kiritilmagan", Toast.LENGTH_SHORT)
                .show()
            return false
        } else if (birthYear == "") {
            Toast.makeText(requireContext(), "Tug'ilgan yili kiritilmagan", Toast.LENGTH_SHORT)
                .show()
            return false
        } else if (deathYear == "") {
            Toast.makeText(requireContext(), "Vafot etgan yili kiritilmagan", Toast.LENGTH_SHORT)
                .show()
            return false
        } else if (info == "") {
            Toast.makeText(
                requireContext(),
                "Adib haqida ma'lumot kiritilmagan",
                Toast.LENGTH_SHORT
            )
                .show()
            return false
        } else if (imgUrl == "") {
            Toast.makeText(requireContext(), "Rasm kiritilmagan", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        val id = System.currentTimeMillis().toString()
        write = Writer(
            id,
            name,
            birthYear,
            deathYear,
            type,
            info,
            imgUrl,
            false
        )
        return true
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
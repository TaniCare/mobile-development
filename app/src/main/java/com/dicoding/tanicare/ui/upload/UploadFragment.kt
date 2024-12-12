package com.dicoding.tanicare.ui.upload

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.dicoding.tanicare.R
import com.dicoding.tanicare.ui.result.ResultActivity
import com.dicoding.tanicare.databinding.FragmentUploadBinding
import java.io.File

class UploadFragment : Fragment() {

    private var _binding: FragmentUploadBinding? = null
    private val binding get() = _binding!!

    private val uploadViewModel: UploadViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnUpload.setOnClickListener {
            if (checkStoragePermission()) {
                openGallery()
            } else {
                requestStoragePermission()
            }
        }

        binding.btnCapture.setOnClickListener {
            if (checkCameraPermission()) {
                openCamera()
            } else {
                requestCameraPermission()
            }
        }

        binding.btnAnalyze.setOnClickListener {
            val imageUri = uploadViewModel.imageUri
            if (imageUri != null) {
                val filePath = FileUtils.getPath(requireContext(), imageUri)
                if (filePath != null) {
                    uploadViewModel.imageUri = null
                    uploadViewModel.capturedImage = null
                    val intent = Intent(requireContext(), ResultActivity::class.java)
                    intent.putExtra("imagePath", filePath)
                    startActivity(intent)
                } else {
                    showToast("Failed to resolve image path")
                }
            } else {
                showToast("Please upload or capture an image first")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        _binding?.let {
            if (uploadViewModel.imageUri == null && uploadViewModel.capturedImage == null) {
                it.ivImagePlaceholder.setImageResource(R.drawable.ic_image_placeholder)
            } else {
                uploadViewModel.imageUri?.let { uri ->
                    it.ivImagePlaceholder.setImageURI(uri)
                }
                uploadViewModel.capturedImage?.let { bitmap ->
                    it.ivImagePlaceholder.setImageBitmap(bitmap)
                }
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImage.launch(intent)
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }

    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestStoragePermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private val requestStoragePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGallery()
            } else {
                showToast("Permission to access storage was denied")
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val bitmap = data?.extras?.get("data") as Bitmap
                val file = File(requireContext().cacheDir, "captured_image.jpg")
                file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
                uploadViewModel.capturedImage = bitmap
                uploadViewModel.imageUri = Uri.fromFile(file)
                binding.ivImagePlaceholder.setImageBitmap(bitmap)
            } else {
                showToast("Camera action canceled")
            }
        }

    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            uri?.let {
                uploadViewModel.imageUri = it
                uploadViewModel.capturedImage = null
                binding.ivImagePlaceholder.setImageURI(it)
            } ?: run {
                showToast("Failed to select image from gallery")
            }
        } else {
            showToast("Gallery action canceled")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }
}

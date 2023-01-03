package go.skillbox.customcamera.ui.fragments

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import go.skillbox.customcamera.R
import go.skillbox.customcamera.databinding.FragmentCameraBinding
import go.skillbox.customcamera.states.State
import go.skillbox.customcamera.viewmodels.CameraViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*
import java.util.concurrent.Executor

@AndroidEntryPoint
class CameraFragment : Fragment() {

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-hh-mm-ss"
        private val REQUEST_PERMISSIONS = buildList {
            add(Manifest.permission.CAMERA)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }

    private val viewModel by viewModels<CameraViewModel>()
    private var imageCapture: ImageCapture? = null
    private lateinit var executor: Executor
    private val launcher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionsMap ->
            if (permissionsMap.values.all { it }) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), "permission is not Granted", Toast.LENGTH_SHORT)
                    .show()
                findNavController().popBackStack()
            }
        }
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        executor = ContextCompat.getMainExecutor(requireContext())

        binding.takePhotoButton.setOnClickListener {
            takePhoto()
        }

        viewModel.photoFlow.onEach { url ->
            openPhoto(url)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.stateFlow.onEach { state ->
            handle(state)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.errorFlow.onEach { error ->
            handle(error)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        checkPermissions()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkPermissions() {
        val isAllGranted = REQUEST_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
        if (isAllGranted) {
            startCamera()
            Toast.makeText(requireContext(), "permission camera is granted", Toast.LENGTH_SHORT)
                .show()
        } else {
            launcher.launch(REQUEST_PERMISSIONS)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(binding.cameraPhotoView.surfaceProvider)
            imageCapture = ImageCapture.Builder().build()
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture
            )
        }, executor)
    }

    private fun takePhoto() {
        val dateTime = System.currentTimeMillis()
        val name =
            SimpleDateFormat(
                FILENAME_FORMAT,
                Locale.getDefault()
            ).format(dateTime)
        val imageCapture = this.imageCapture ?: return
        imageCapture.targetRotation = Surface.ROTATION_0
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            this.requireContext().contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()
        imageCapture.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Toast.makeText(
                        requireContext(),
                        "Photo saved on: ${outputFileResults.savedUri}",
                        Toast.LENGTH_SHORT
                    ).show()
                    viewModel.savePhoto(outputFileResults.savedUri, dateTime)
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        requireContext(),
                        "Photo failed: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    exception.printStackTrace()
                }
            }
        )
    }

    private fun openPhoto(url: String) {
        val args = Bundle()
        args.putString(FullscreenPhotoFragment.IMAGE_URL, url)
        findNavController().navigate(
            R.id.action_TakePhotoFragment_to_fullscreenPhotoFragment,
            args
        )
    }

    private fun handle(state: State) {
        when (state) {
            State.LOADING -> binding.progressBar.isVisible = true
            State.SUCCESS, State.ERROR -> binding.progressBar.isVisible = false
        }
    }

    private fun handle(error: Exception) {
        Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
    }
}
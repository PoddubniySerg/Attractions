package go.skillbox.customcamera.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import go.skillbox.customcamera.R
import go.skillbox.customcamera.databinding.FragmentListPhotosBinding
import go.skillbox.customcamera.models.PhotoUi
import go.skillbox.customcamera.states.State
import go.skillbox.customcamera.ui.adapters.PhotosListAdapter
import go.skillbox.customcamera.viewmodels.ListPhotosViewModel
import go.skillbox.domain.models.Photo
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ListPhotosFragment : Fragment() {

    private val viewModel by viewModels<ListPhotosViewModel>()
    private var _binding: FragmentListPhotosBinding? = null
    private val binding get() = _binding!!
    private val adapter = PhotosListAdapter(
        { item -> viewModel.onItemClick(item) },
        { item -> removeItem(item) }
    )
    private val launcher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                lifecycleScope.launchWhenResumed { getPhotos() }
            } else {
                findNavController().navigate(R.id.action_ListPhotosFragment_to_confirmPermissionsFragment)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListPhotosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.listPhotos.adapter = adapter

        viewModel.photosFlow.onEach { photos ->
            submitList(photos)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.onItemClickFlow.onEach { url ->
            onItemClick(url)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.stateFlow.onEach { state ->
            handle(state)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.errorFlow.onEach { error ->
            handle(error)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        binding.newPhotoButton.setOnClickListener {
            findNavController().navigate(R.id.action_ListPhotosFragment_to_TakePhotoFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launchWhenResumed { checkFileStoragePermission() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun submitList(photos: List<PhotoUi>) {
        binding.progressBar.isVisible = true
        adapter.submitList(photos)
        binding.progressBar.isVisible = false
    }

    private fun onItemClick(url: String) {
        val args = Bundle()
        args.putString(FullscreenPhotoFragment.IMAGE_URL, url)
        findNavController().navigate(
            R.id.action_ListPhotosFragment_to_fullscreenPhotoFragment,
            args
        )
    }

    private suspend fun getPhotos() {
        viewModel.takePhotos(activity?.contentResolver ?: return)
    }

    private suspend fun checkFileStoragePermission() {
        val statePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (statePermission == PackageManager.PERMISSION_GRANTED) {
            getPhotos()
        } else {
            launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun removeItem(photo: Photo) {
        viewModel.removeItem(photo, activity?.contentResolver ?: return)
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
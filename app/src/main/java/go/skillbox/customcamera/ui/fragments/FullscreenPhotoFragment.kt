package go.skillbox.customcamera.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import go.skillbox.customcamera.R
import go.skillbox.customcamera.databinding.FragmentFullscreenPhotoBinding
import go.skillbox.customcamera.states.State
import go.skillbox.customcamera.viewmodels.FullscreenViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class FullscreenPhotoFragment : Fragment() {

    companion object {

        const val IMAGE_URL = "image_url"
    }

    private val viewModel by viewModels<FullscreenViewModel>()
    private var _binding: FragmentFullscreenPhotoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFullscreenPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.removeImageView.setOnClickListener {
            removePhoto()
        }

        binding.homeButton.setOnClickListener {
            goHome()
        }

        viewModel.stateFlow.onEach { state ->
            handle(state)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.errorFlow.onEach { error ->
            handle(error)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        loadImage()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun removePhoto() {
        viewModel.removePhoto(
            arguments?.getString(IMAGE_URL) ?: return,
            activity?.contentResolver ?: return
        )
    }

    private fun goHome() {
        findNavController().popBackStack(R.id.ListPhotosFragment, false)
    }

    private fun loadImage() {
        Glide
            .with(this)
            .load(requireArguments().getString(IMAGE_URL))
            .placeholder(R.drawable.ic_baseline_photo_camera_24)
            .into(binding.fullscreenImageView)
        binding.progressBar.isVisible = false
    }

    private fun handle(state: State) {
        when (state) {
            State.LOADING -> binding.progressBar.isVisible = true
            State.SUCCESS, State.ERROR -> {
                binding.progressBar.isVisible = false
                findNavController().popBackStack()
            }
        }
    }

    private fun handle(error: Exception) {
        Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
    }
}
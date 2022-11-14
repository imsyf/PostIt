package it.post.app.ui.upload

import android.app.Activity
import android.os.Bundle
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.fragment.findNavController
import com.github.drjacky.imagepicker.ImagePicker
import it.post.app.PostItApp
import it.post.app.R
import it.post.app.databinding.FragmentUploadBinding
import it.post.app.ui.common.TransientMessage
import it.post.app.ui.common.showToasty
import it.post.app.ui.feed.FeedFragment.Companion.KEY_SHOULD_REFRESH
import kotlinx.coroutines.launch

class UploadFragment : Fragment() {

    private var _binding: FragmentUploadBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<UploadViewModel> {
        viewModelFactory {
            initializer {
                val app = activity?.application as PostItApp
                UploadViewModel(app.storyRepository)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUploadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.setup()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {
                    binding.render(it)
                }
            }
        }
    }

    private fun FragmentUploadBinding.setup() {
        val launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    result.data?.data?.let(viewModel::onImageChanged)
                }
                ImagePicker.RESULT_ERROR -> {
                    showToasty(
                        TransientMessage.Plain(
                            TransientMessage.Type.Error,
                            ImagePicker.getError(result.data),
                        ),
                    )
                }
            }

            viewModel.setBusy(false)
        }

        pick.setOnClickListener {
            viewModel.setBusy(true)

            ImagePicker.with(requireActivity())
                .maxResultSize(1080, 1080, true)
                .crop()
                .cropFreeStyle()
                .setDismissListener { viewModel.setBusy(false) }
                .createIntentFromDialog(launcher::launch)
        }

        description.doOnTextChanged { text, _, _, _ ->
            val desc = "$text"

            if (desc.isBlank()) {
                labelForDescription.error = getString(R.string.desc_validation_error)
            } else {
                labelForDescription.error = null
            }

            viewModel.onDescriptionChanged(desc, labelForDescription.error == null)
        }

        upload.setOnClickListener {
            root.requestFocus()
            viewModel.upload()
        }
    }

    private fun FragmentUploadBinding.render(state: UploadState) {
        loading.isVisible = state.isBusy
        description.isEnabled = !state.isBusy

        if (state.isImageSelected) {
            image.setImageURI(state.imageUri)
            size.text = Formatter.formatShortFileSize(context, state.imageSizeBytes)
        }

        size.isVisible = state.isImageSelected
        text.isVisible = !state.isImageSelected

        upload.isEnabled = state.canUpload

        state.message?.let {
            showToasty(it, viewModel::onMessageShown)
        }

        if (state.isSucceed) {
            // Send signal to the previous screen to refresh the feed
            findNavController().previousBackStackEntry
                ?.savedStateHandle
                ?.set(KEY_SHOULD_REFRESH, true)

            // We're done here
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

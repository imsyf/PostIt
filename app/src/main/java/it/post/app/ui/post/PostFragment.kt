package it.post.app.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.fragment.navArgs
import it.post.app.PostItApp
import it.post.app.databinding.FragmentPostBinding
import it.post.app.ui.common.bindImage
import it.post.app.ui.common.showToasty
import kotlinx.coroutines.launch

class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<PostFragmentArgs>()

    private val viewModel by viewModels<PostViewModel> {
        viewModelFactory {
            initializer {
                val app = activity?.application as PostItApp
                PostViewModel(args.storyId, app.storyRepository)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
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

    private fun FragmentPostBinding.setup() {
        swipeToRefresh.setOnRefreshListener {
            viewModel.fetch()
        }
    }

    private fun FragmentPostBinding.render(state: PostState) {
        swipeToRefresh.isRefreshing = state.isFetching

        askToPull.root.isVisible = state.isFailed
        content.isVisible = !state.isFailed

        state.story?.let {
            image.bindImage(it.photoUrl)
            image.contentDescription = it.caption
            description.text = it.caption
            timeAgo.text = it.timeAgo
        }

        state.message?.let {
            showToasty(it, viewModel::onMessageShown)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

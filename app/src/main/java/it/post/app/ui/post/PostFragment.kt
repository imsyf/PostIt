package it.post.app.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.fragment.navArgs
import it.post.app.PostItApp
import it.post.app.askToPull
import it.post.app.databinding.EpoxyLayoutBinding
import it.post.app.postDetails
import it.post.app.ui.common.showToasty
import kotlinx.coroutines.launch

class PostFragment : Fragment() {

    private var _binding: EpoxyLayoutBinding? = null
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
        _binding = EpoxyLayoutBinding.inflate(inflater, container, false)
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

    private fun EpoxyLayoutBinding.setup() {
        swipeToRefresh.setOnRefreshListener {
            viewModel.fetch()
        }
    }

    private fun EpoxyLayoutBinding.render(state: PostState) {
        swipeToRefresh.isRefreshing = state.isFetching

        epoxy.withModels {
            state.story?.let {
                postDetails {
                    id(it.id)
                    props(it)
                }
            }

            if (state.isFailed) {
                askToPull {
                    id("askToPull")
                }
            }
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

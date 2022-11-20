package it.post.app.ui.feed

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import it.post.app.PostItApp
import it.post.app.R
import it.post.app.askToPull
import it.post.app.databinding.FragmentFeedBinding
import it.post.app.feedItem
import it.post.app.loading
import it.post.app.tryAgain
import it.post.app.ui.common.showToasty
import kotlinx.coroutines.launch

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<FeedViewModel> {
        viewModelFactory {
            initializer {
                val app = activity?.application as PostItApp
                FeedViewModel(app.storyRepository)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOptionsMenu()
        binding.setup()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect {
                        binding.render(it)
                    }
                }

                launch {
                    val currentHandle = findNavController().currentBackStackEntry?.savedStateHandle
                    val initialValue = false

                    currentHandle
                        ?.getStateFlow(KEY_SHOULD_REFRESH, initialValue)
                        ?.collect { shouldRefresh ->
                            // Refresh the feed should user successfully create a new post
                            if (shouldRefresh) {
                                viewModel.refresh()
                                // Revert back to its initial value once the request is handled
                                currentHandle[KEY_SHOULD_REFRESH] = initialValue
                            }
                        }
                }
            }
        }
    }

    private fun FragmentFeedBinding.setup() {
        swipeToRefresh.setOnRefreshListener {
            viewModel.refresh()
        }

        newPost.setOnClickListener {
            val directions = FeedFragmentDirections.toUploadFragment()
            findNavController().navigate(directions)
        }
    }

    private fun FragmentFeedBinding.render(state: FeedState) {
        swipeToRefresh.isRefreshing = state.isRefreshing

        epoxy.withModels {
            state.stories.forEach {
                feedItem {
                    id(it.id)
                    props(it)
                    onClick { _, _, clickedView, _ ->
                        val extras = FragmentNavigatorExtras(
                            Pair(
                                clickedView.findViewById(R.id.image),
                                it.id,
                            ),
                        )

                        val directions = FeedFragmentDirections.toPostFragment(
                            name = it.name,
                            storyId = it.id,
                        )

                        findNavController().navigate(directions, extras)
                    }
                }
            }

            if (state.shouldFetch) {
                loading {
                    id("loading/${state.stories.size}")
                    onBind { _, _, _ -> viewModel.fetch() }
                }
            } else if (state.stories.isNotEmpty()) {
                tryAgain {
                    id("tryAgain")
                    onClick { _, _, _, _ -> viewModel.fetch() }
                }
            } else {
                askToPull {
                    id("askToPull")
                }
            }
        }

        state.message?.let {
            showToasty(it, viewModel::onMessageShown)
        }
    }

    private fun setupOptionsMenu() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_main, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.menu_log_out -> {
                            viewModel.logout()
                            true
                        }
                        R.id.menu_change_language -> {
                            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                            true
                        }
                        else -> false
                    }
                }
            },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED,
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val KEY_SHOULD_REFRESH = "KEY_SHOULD_REFRESH"
    }
}

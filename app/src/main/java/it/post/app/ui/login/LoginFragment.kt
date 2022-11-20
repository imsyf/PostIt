package it.post.app.ui.login

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
import androidx.navigation.fragment.findNavController
import it.post.app.PostItApp
import it.post.app.databinding.FragmentLoginBinding
import it.post.app.ui.common.showToasty
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<LoginViewModel> {
        viewModelFactory {
            initializer {
                val app = activity?.application as PostItApp
                LoginViewModel(app.storyRepository)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
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

    private fun FragmentLoginBinding.setup() {
        loginEmail.bind(
            value = viewModel.state.value.email,
            onValueChanged = viewModel::onEmailChanged,
        )

        loginPassword.bind(
            value = viewModel.state.value.password,
            onValueChanged = viewModel::onPasswordChanged,
        )

        login.setOnClickListener {
            root.requestFocus()
            viewModel.login()
        }

        register.setOnClickListener {
            val directions = LoginFragmentDirections.toRegisterFragment()
            findNavController().navigate(directions)
        }
    }

    private fun FragmentLoginBinding.render(state: LoginState) {
        loading.isVisible = state.isSubmitting

        loginEmail.editText?.isEnabled = !state.isSubmitting
        loginPassword.editText?.isEnabled = !state.isSubmitting

        login.isEnabled = state.canLogin

        state.message?.let {
            showToasty(it, viewModel::onMessageShown)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

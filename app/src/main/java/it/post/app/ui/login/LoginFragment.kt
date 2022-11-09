package it.post.app.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import it.post.app.databinding.LoginFragmentBinding
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: LoginFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = LoginFragmentBinding.inflate(inflater, container, false)
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

    private fun LoginFragmentBinding.setup() {
        loginEmail.doOnTextChanged { text, _, _, _ ->
            val email = "$text"
            viewModel.onEmailChanged(email)
        }

        loginPassword.doOnTextChanged { text, _, _, _ ->
            val password = "$text"
            viewModel.onPasswordChanged(password)
        }
    }

    private fun LoginFragmentBinding.render(state: LoginState) {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

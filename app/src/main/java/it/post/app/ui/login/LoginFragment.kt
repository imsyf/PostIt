package it.post.app.ui.login

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import it.post.app.R
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

            if (email.isBlank() || Patterns.EMAIL_ADDRESS.matcher(email).matches().not()) {
                labelForLoginEmail.error = getString(R.string.email_validation_error)
            } else {
                labelForLoginEmail.error = null
            }

            viewModel.onEmailChanged(email, labelForLoginEmail.error == null)
        }

        loginPassword.doOnTextChanged { text, _, _, _ ->
            val password = "$text"

            if (password.isBlank() || password.length < 6) {
                labelForLoginPassword.error = getString(R.string.password_validation_error)
            } else {
                labelForLoginPassword.error = null
            }

            viewModel.onPasswordChanged(password, labelForLoginPassword.error == null)
        }
    }

    private fun LoginFragmentBinding.render(state: LoginState) {
        login.isEnabled = state.canLogin
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

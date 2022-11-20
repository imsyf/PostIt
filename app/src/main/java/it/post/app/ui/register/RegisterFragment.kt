package it.post.app.ui.register

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
import it.post.app.PostItApp
import it.post.app.R
import it.post.app.databinding.FragmentRegisterBinding
import it.post.app.ui.common.showToasty
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<RegisterViewModel> {
        viewModelFactory {
            initializer {
                val app = activity?.application as PostItApp
                RegisterViewModel(app.storyRepository)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
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

    private fun FragmentRegisterBinding.setup() {
        registerName.bind(
            value = viewModel.state.value.name,
            onValueChanged = viewModel::onNameChanged,
            validation = String::isNotBlank,
            errorMessage = R.string.name_validation_error,
        )

        registerEmail.bind(
            value = viewModel.state.value.email,
            onValueChanged = viewModel::onEmailChanged,
        )

        registerPassword.bind(
            value = viewModel.state.value.password,
            onValueChanged = viewModel::onPasswordChanged,
        )

        register.setOnClickListener {
            root.requestFocus()
            viewModel.register()
        }
    }

    private fun FragmentRegisterBinding.render(state: RegisterState) {
        loading.isVisible = state.isSubmitting

        registerName.editText?.isEnabled = !state.isSubmitting
        registerEmail.editText?.isEnabled = !state.isSubmitting
        registerPassword.editText?.isEnabled = !state.isSubmitting

        register.isEnabled = state.canRegister

        state.message?.let {
            showToasty(it, viewModel::onMessageShown)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

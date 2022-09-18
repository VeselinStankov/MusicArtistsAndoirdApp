package com.example.navigationtask.login

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.navigationtask.R
import com.example.navigationtask.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initFields()
        binding.initListeners()
        initObservers(binding)

        return binding.root
    }

    private fun initFields() {
        binding = FragmentLoginBinding.inflate(layoutInflater)
    }

    private fun FragmentLoginBinding.initListeners() {
        btnLogin.setOnClickListener {
            viewModel.onLoginClicked(
                editEmail.text.toString(),
                editPassword.text.toString()
            )
        }

        btnRegister.setOnClickListener {
            viewModel.onRegisterClicked()
        }
    }

    private fun initObservers(binding: FragmentLoginBinding) {
        viewModel.shouldNavigateToRegisterScreen.consume(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate) {
                findNavController().navigate(
                    LoginFragmentDirections.actionLoginFragmentToRegisterFragment(
                        binding.editEmail.text.toString()
                    )
                )
            }
        }

        viewModel.shouldNavigateToFeedScreen.consume(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate) {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToFeedFragment())
            }
        }

        viewModel.emailError.observe(viewLifecycleOwner) { hasError ->
            if (hasError) {
                binding.editLayoutEmail.error = getString(R.string.invalid_email)
            } else {
                binding.editLayoutEmail.error = null
            }
        }

        viewModel.passwordError.observe(viewLifecycleOwner) { hasError ->
            if (hasError) {
                binding.editLayoutPassword.error = getString(R.string.invalid_password)
            } else {
                binding.editLayoutPassword.error = null
            }
        }
    }
}
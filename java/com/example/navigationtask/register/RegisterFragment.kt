package com.example.navigationtask.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.navigationtask.R
import com.example.navigationtask.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initFields()
        binding.initListeners(binding)
        initObservers(binding)

        return binding.root
    }

    private fun initFields() {
        binding = FragmentRegisterBinding.inflate(layoutInflater)

        val args = RegisterFragmentArgs.fromBundle(requireArguments())
        binding.editEmail.setText(args.enteredEmail)
    }

    private fun FragmentRegisterBinding.initListeners(binding: FragmentRegisterBinding) {
        binding.btnRegisterRegisterFragment.setOnClickListener {
            viewModel.onRegisterClicked(
                editEmail.text.toString(),
                editPassword.text.toString(),
                editFullName.text.toString()
            )
        }
    }

    private fun initObservers(binding: FragmentRegisterBinding) {
        viewModel.shouldNavigateToLoginScreen.consume(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate) {
                findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
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

        viewModel.nameError.observe(viewLifecycleOwner) { hasError ->
            if (hasError) {
                binding.editLayoutFullName.error = getString(R.string.invalid_full_name)
            } else {
                binding.editLayoutFullName.error = null
            }
        }

        viewModel.existingUserError.observe(viewLifecycleOwner) { hasError ->
            if (hasError) {
                Toast.makeText(
                    activity,
                    getString(R.string.error_existing_user),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
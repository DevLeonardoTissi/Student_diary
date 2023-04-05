package com.example.studentdiary.ui.fragment.registerFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.studentdiary.R
import com.example.studentdiary.databinding.FragmentRegisterBinding
import com.example.studentdiary.extensions.identifiesErrorFirebaseAuth
import com.example.studentdiary.extensions.snackBar
import com.example.studentdiary.model.User
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val model: RegisterViewModel by viewModel()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureObserverRegister()
        configureRegistrationButton()
    }


    private fun configureRegistrationButton() {
        val registrationButton = binding.registerFragmentRegisterButton
        registrationButton.setOnClickListener {
            cleanFields()

            val email = binding.registerFragmentTextfieldEmail.editText?.text.toString()
            val password = binding.registerFragmentTextfieldPassword.editText?.text.toString()
            val passwordChecker =
                binding.registerFragmentTextfieldPasswordChecker.editText?.text.toString()

            val isvalid = validateData(email, password, passwordChecker)
            if (isvalid) {
                register(User(email, password))
            }

        }
    }

    private fun cleanFields() {
        binding.registerFragmentTextfieldEmail.error = null
        binding.registerFragmentTextfieldPassword.error = null
        binding.registerFragmentTextfieldPasswordChecker.error = null
    }

    private fun validateData(email: String, password: String, passwordChecker: String): Boolean {
        var valid = true

        if (email.isBlank()) {
            binding.registerFragmentTextfieldEmail.error =
                context?.getString(R.string.register_fragment_text_field_error_email_required)
            valid = false
        }

        if (password.isBlank()) {
            binding.registerFragmentTextfieldPassword.error =
                context?.getString(R.string.register_fragment_text_field_error_password_required)
            valid = false
        }

        if (passwordChecker != password) {
            binding.registerFragmentTextfieldPasswordChecker.error =
                context?.getString(R.string.register_fragment_text_field_error_different_password)
            valid = false
        }
        return valid
    }

    private fun register(user: User) {
            model.register(user)
    }

    private fun configureObserverRegister() {
        context?.let { context ->
            model.firebaseAuthLiveData.observe(viewLifecycleOwner) { resource ->
                resource?.let {
                    if (resource.data) {
                        view?.snackBar(context.getString(R.string.register_fragment_snackbar_message_registration_done))

                    } else {
                        resource.exception?.let { exception ->
                            val errorMessage = identifiesErrorFirebaseAuth(exception)
                            view?.snackBar(errorMessage)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


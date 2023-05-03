package com.example.studentdiary.ui.fragment.registerFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.studentdiary.R
import com.example.studentdiary.databinding.FragmentRegisterBinding
import com.example.studentdiary.extensions.isOnline
import com.example.studentdiary.extensions.snackBar
import com.example.studentdiary.extensions.toast
import com.example.studentdiary.model.User
import com.example.studentdiary.ui.AppViewModel
import com.example.studentdiary.ui.NavigationComponents
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val model: RegisterViewModel by viewModel()
    private val appViewModel: AppViewModel by activityViewModel()
    private val controller by lazy {
        findNavController()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigationComponents()
        configureObserverRegister()
        configureRegistrationButton()
        configureAndSaveFields()
        updateUi()
    }

    private fun updateUi() {
        model.fieldEmail.observe(viewLifecycleOwner){email ->
            email?.let {
                val fieldEmail = binding.registerFragmentTextfieldEmail.editText
                fieldEmail?.setText(it)
                fieldEmail?.setSelection(it.length)
            }
        }
        model.fieldPassword.observe(viewLifecycleOwner){password ->
            password?.let {
                val fieldPassword = binding.registerFragmentTextfieldPassword.editText
                fieldPassword?.setText(it)
                fieldPassword?.setSelection(it.length)
            }
        }
        model.fieldPasswordChecker.observe(viewLifecycleOwner){ passwordChecker ->
            passwordChecker?.let {
                val fieldPasswordChecker = binding.registerFragmentTextfieldPasswordChecker.editText
                fieldPasswordChecker?.setText(it)
                fieldPasswordChecker?.setSelection(it.length)
            }
        }
    }

    private fun configureAndSaveFields() {
        val fieldEmail = binding.registerFragmentTextfieldEmail.editText
        val fieldPassword = binding.registerFragmentTextfieldPassword.editText
        val fieldPasswordChecker = binding.registerFragmentTextfieldPasswordChecker.editText

        fieldEmail?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                model.setEmail(fieldEmail?.text.toString())
            }
        }

        fieldPassword?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                model.setPassword(fieldPassword?.text.toString())
            }
        }

        fieldPasswordChecker?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                model.setPasswordChecker(fieldPassword?.text.toString())
            }
        }
    }

    private fun setupNavigationComponents() {
        appViewModel.hasNavigationComponents =
            NavigationComponents(navigationIcon = true, menuDrawer = false)
    }


    private fun configureRegistrationButton() {
        val registrationButton = binding.registerFragmentRegisterButton
        registrationButton.setOnClickListener {
            cleanFields()

            val email = binding.registerFragmentTextfieldEmail.editText?.text.toString()
            val password = binding.registerFragmentTextfieldPassword.editText?.text.toString()
            val passwordChecker =
                binding.registerFragmentTextfieldPasswordChecker.editText?.text.toString()

            val isValid = validateData(email, password, passwordChecker)
            if (isValid) {
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
                        controller.popBackStack()
//                        snackBar(getString(R.string.register_fragment_snackbar_message_registration_done))
                    } else {
                        resource.exception?.let { exception ->
                            if (context.isOnline()) {
                                val errorMessage = identifiesErrorFirebaseAuthOnRegister(exception)
                                snackBar(errorMessage)
                            } else {
                                context.toast(getString(R.string.default_message_noConnection))
                            }
                        }
                    }
                }
            }
        }
    }


    private fun identifiesErrorFirebaseAuthOnRegister(exception: Exception): String {
        lateinit var errorMessage: String
        context?.let { context ->
            errorMessage = when (exception) {
                is FirebaseAuthWeakPasswordException -> context.getString(R.string.register_fragment_snackbar_message_firebase_auth_weak_password_exception)
                is FirebaseAuthInvalidCredentialsException -> context.getString(R.string.register_fragment_snackbar_message_firebase_auth_Invalid_credentials_Exception)
                is FirebaseAuthUserCollisionException -> context.getString(R.string.register_fragment_snackbar_message_firebase_auth_user_collision_exception)
                is IllegalArgumentException -> context.getString(R.string.register_fragment_snackbar_message_firebase_auth_illegal_argument_exception)
                else -> context.getString(R.string.register_fragment_snackbar_message_unknown_error)
            }
        }
        return errorMessage
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


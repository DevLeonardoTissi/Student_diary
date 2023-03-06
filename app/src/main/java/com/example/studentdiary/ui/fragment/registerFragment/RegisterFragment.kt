package com.example.studentdiary.ui.fragment.registerFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.studentdiary.R
import com.example.studentdiary.databinding.FragmentRegisterBinding
import com.example.studentdiary.extensions.snackBar
import com.example.studentdiary.model.User
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
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
        register(User("leonardo.tissi@gmail.com", "123"))

    }

    fun register(user: User) {
        context?.let { context ->
            model.register(user)
            model.firebaseAuthLiveData.observe(viewLifecycleOwner) { resource ->
                resource?.let {
                    if (resource.data) {
                        view?.snackBar(context.getString(R.string.register_fragment_snackbar_message_registration_done))

                    } else {
                        resource.exception?.let { exception ->
                            val errorMessage = when (exception) {
                                is FirebaseAuthWeakPasswordException -> context.getString(R.string.register_fragment_snackbar_message_firebase_auth_weak_password_exception)
                                is FirebaseAuthInvalidCredentialsException -> context.getString(R.string.register_fragment_snackbar_message_firebase_auth_Invalid_credentials_Exception)
                                is FirebaseAuthUserCollisionException -> context.getString(R.string.register_fragment_snackbar_message_firebase_auth_user_collision_exception)
                                is IllegalArgumentException -> context.getString(R.string.register_fragment_snackbar_message_firebase_auth_illegal_argument_exception)
                                else -> context.getString(R.string.register_fragment_snackbar_message_unknown_error)
                            }
                            view?.snackBar(errorMessage)
                        }
                    }
                }
            }
        }
    }
}
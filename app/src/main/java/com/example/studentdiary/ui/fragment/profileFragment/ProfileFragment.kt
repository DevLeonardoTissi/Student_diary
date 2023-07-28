package com.example.studentdiary.ui.fragment.profileFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.studentdiary.R
import com.example.studentdiary.databinding.FragmentProfileBinding
import com.example.studentdiary.extensions.snackBar
import com.example.studentdiary.ui.AppViewModel
import com.example.studentdiary.ui.NavigationComponents
import com.example.studentdiary.ui.dialog.UpdatePasswordBottomSheetDialog
import com.example.studentdiary.ui.fragment.baseFragment.BaseFragment
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProfileFragment : BaseFragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val model: ProfileViewModel by viewModel()
    private val appViewModel: AppViewModel by activityViewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupNavigationComponents()
        configureAndSaveFields()
        updateUi()
        onClickUpdatePasswordButton()
        onclickFinishedButton()
        onClickDeleteUserTextView()
    }

    private fun setupNavigationComponents() {
        appViewModel.hasNavigationComponents =
            NavigationComponents(toolbar = true, menuDrawer = true)
    }


    private fun updateUi() {
        model.userEmail.observe(viewLifecycleOwner) { userEmail ->
            userEmail?.let {
                val fieldEmail = binding.profileFragmentTextFieldEmail
                fieldEmail.editText?.setText(it)
                fieldEmail.editText?.setSelection(it.length)
            }

        }
        model.userName.observe(viewLifecycleOwner) { userName ->
            userName?.let {
                val fieldName = binding.profileFragmentTextFieldName
                fieldName.editText?.setText(it)
                fieldName.editText?.setSelection(it.length)
            }
        }
    }

    private fun configureAndSaveFields() {
        val fieldEmail = binding.profileFragmentTextFieldEmail.editText
        val fieldName = binding.profileFragmentTextFieldName.editText


        fieldEmail?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                model.setUserEmail(fieldEmail?.text.toString())
            }
        }

        fieldName?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                model.setUserName(fieldName?.text.toString())
            }
        }
    }


    private fun onClickUpdatePasswordButton() {
        context?.let { context ->
            binding.profileFragmentUpdatePassword.setOnClickListener {
                UpdatePasswordBottomSheetDialog(context).show { newPassword ->
                    model.updatePassword(newPassword, onError = { exception ->
                        if (exception is FirebaseAuthRecentLoginRequiredException) {

                            //  USUÁRIO LOGADO HÁ BASTANTE TEMPO


                        } else {
                            val errorMessage =
                                identifiesErrorFirebaseAuthOnUpdatePassword(exception)
                            snackBar(errorMessage)
                        }

                    }, onSuccess = {
                        snackBar(getString(R.string.user_profile_fragment_snackbar_message_success_on_update_password))
                    })
                }
            }
        }
    }

    private fun identifiesErrorFirebaseAuthOnUpdatePassword(e: Exception): String {
        val errorMessage = when (e) {
            is FirebaseAuthWeakPasswordException -> getString(R.string.user_profile_fragment_snackbar_message_firebase_auth_weak_password_exception)
            is FirebaseNetworkException -> getString(R.string.user_profile_fragment_snackbar_message_firebase_auth_network_exception_on_forgot_password)
            else -> getString(R.string.user_profile_fragment_snackbar_message_unknown_error)
        }
        return errorMessage
    }

    private fun onclickFinishedButton() {
        binding.profileFragmentFinishedButton.setOnClickListener {
            val name = binding.profileFragmentTextFieldName.editText?.text.toString().trim()
            val email = binding.profileFragmentTextFieldEmail.editText?.text.toString().trim()


            val credential = EmailAuthProvider
                .getCredential("user@example.com", "password1234")




            model.updateUserEmailAndUserName(name = name, email = email,

                // Verificar campos e , se tudo certo, chama o aler dialog


                onSuccess = {
                    snackBar(getString(R.string.user_profile_fragment_snackbar_message_success_on_update_user_information))
                }, onError = { exception ->

                    if (exception is FirebaseAuthRecentLoginRequiredException) {

                        //  USUÁRIO LOGADO HÁ BASTANTE TEMPO

                    } else {
                        val errorMessage =
                            identifiesErrorFirebaseAuthOnUpdateUserEmailAndUserName(exception)
                        snackBar(errorMessage)
                    }
                })
        }
    }

    private fun identifiesErrorFirebaseAuthOnUpdateUserEmailAndUserName(e: Exception): String {
        val errorMessage = when (e) {
            is FirebaseAuthInvalidCredentialsException -> getString(R.string.user_profile_fragment_snackbar_message_firebase_auth_Invalid_credentials_Exception)
            is FirebaseNetworkException -> getString(R.string.user_profile_fragment_snackbar_message_firebase_auth_network_exception_on_forgot_password)
            is FirebaseAuthUserCollisionException -> getString(R.string.user_profile_fragment_snackbar_message_firebase_auth_user_collision_exception)
            else -> getString(R.string.user_profile_fragment_snackbar_message_unknown_error)
        }
        return errorMessage
    }

    private fun onClickDeleteUserTextView() {
        binding.fragmentProfileTextViewDeleteUser.setOnClickListener {
            model.deleteUser(onSuccess = {
                // IMPLEMENTAR
            }, onError = {
                // IMPLEMENTAR
            })
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}
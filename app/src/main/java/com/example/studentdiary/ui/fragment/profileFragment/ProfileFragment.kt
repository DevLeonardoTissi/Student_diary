package com.example.studentdiary.ui.fragment.profileFragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.studentdiary.R
import com.example.studentdiary.databinding.FragmentProfileBinding
import com.example.studentdiary.datastore.getUserAuthProvider
import com.example.studentdiary.datastore.getUserTokenAuth
import com.example.studentdiary.extensions.alertDialog
import com.example.studentdiary.extensions.googleSignInClient
import com.example.studentdiary.extensions.snackBar
import com.example.studentdiary.ui.AppViewModel
import com.example.studentdiary.ui.NavigationComponents
import com.example.studentdiary.ui.activity.MainActivity
import com.example.studentdiary.ui.dialog.RequestPasswordBottomSheetDialog
import com.example.studentdiary.ui.dialog.UpdatePasswordBottomSheetDialog
import com.example.studentdiary.ui.fragment.baseFragment.BaseFragment
import com.example.studentdiary.utils.CancellationException
import com.example.studentdiary.utils.enums.UserAuthProvider
import com.example.studentdiary.utils.validateEmailFormat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProfileFragment : BaseFragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val model: ProfileViewModel by viewModel()
    private val appViewModel: AppViewModel by activityViewModel()
    private lateinit var googleCredential: AuthCredential
    private val openGoogleLogin =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val googleAccount =
                    GoogleSignIn.getSignedInAccountFromIntent(result.data).result
                googleCredential =
                    GoogleAuthProvider.getCredential(googleAccount.idToken, null)
            }
        }


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
        getGoogleCredential()
        onClickUpdatePasswordButton()
        onclickFinishedButton()
        onClickDeleteUserTextView()
    }

    private fun getGoogleCredential() {
        context?.let { context ->
            lifecycleScope.launch {
                getUserAuthProvider(context)?.let {
                    if (it == UserAuthProvider.GOOGLE_PROVIDER.toString()) {
                        val client = context.googleSignInClient()
                        val intent = client.signInIntent
                        openGoogleLogin.launch(intent)
                    }
                }
            }
        }
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
            binding.profileFragmentUpdatePasswordButton.setOnClickListener {
                UpdatePasswordBottomSheetDialog(context).show { newPassword ->
                    model.updatePassword(newPassword, onError = { exception ->
                        if (exception is FirebaseAuthRecentLoginRequiredException) {
                            lifecycleScope.launch {
                                tryToGetCredential {
                                    reAuthenticate(it, onSuccess = {
                                        model.updatePassword(newPassword, onError = {
                                            val errorMessage =
                                                identifiesErrorFirebaseAuthOnUpdatePassword(exception)
                                            snackBar(errorMessage)
                                        }, onSuccess = {
                                            snackBar(getString(R.string.user_profile_fragment_snackbar_message_success_on_update_password))
                                        })
                                    })
                                }
                            }

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

    private fun validateData(email: String): Boolean {
        var valid = true

        if (email.isBlank()) {
            binding.profileFragmentTextFieldEmail.error =
                getString(R.string.user_profile_field_email_error_field_empty)
            valid = false
        } else if (!validateEmailFormat(email)) {
            binding.profileFragmentTextFieldEmail.error =
                getString(R.string.user_profile_field_email_error_email_invalid)
            valid = false
        }

        return valid
    }

    private fun onclickFinishedButton() {
        binding.profileFragmentFinishedButton.setOnClickListener {
            val fieldEmail = binding.profileFragmentTextFieldEmail
            fieldEmail.error = null
            val name = binding.profileFragmentTextFieldName.editText?.text.toString().trim()
            val email = fieldEmail.editText?.text.toString().trim()


            val isValid = validateData(email)
            if (isValid) {
                context?.alertDialog(
                    title = getString(R.string.user_profile_fragment_alert_dialog_update_data_title),
                    message = getString(R.string.user_profile_fragment_alert_dialog_update_data_description),
                    icon = R.drawable.ic_user,
                    onClickingOnPositiveButton = {

                        model.updateUserEmailAndUserName(name = name, email = email,
                            onSuccess = {
                                snackBar(getString(R.string.user_profile_fragment_snackbar_message_success_on_update_user_information))

                            }, onError = { exception ->
                                lifecycleScope.launch {
                                    if (exception is FirebaseAuthRecentLoginRequiredException) {

                                        tryToGetCredential(onSuccess = {
                                            reAuthenticate(it, onSuccess = {
                                                model.updateUserEmailAndUserName(
                                                    name = name,
                                                    email = email,
                                                    onSuccess = {
                                                        snackBar(getString(R.string.user_profile_fragment_snackbar_message_success_on_update_user_information))

                                                    },
                                                    onError = { exception ->
                                                        val errorMessage =
                                                            identifiesErrorFirebaseAuthOnUpdateUserEmailAndUserName(
                                                                exception
                                                            )
                                                        snackBar(errorMessage)
                                                    })
                                            })
                                        })


                                    } else {
                                        val errorMessage =
                                            identifiesErrorFirebaseAuthOnUpdateUserEmailAndUserName(
                                                exception
                                            )
                                        snackBar(errorMessage)
                                    }
                                }
                            })
                    })


            }
        }
    }


    private suspend fun tryToGetCredential(onSuccess: (credential: AuthCredential) -> Unit) {
        var exceptionOccurred = false
        context?.let { context ->

            val authProvider = getUserAuthProvider(context)
            val token = getUserTokenAuth(context)


            val credential: AuthCredential? =
                try {
                    when (authProvider) {
                        UserAuthProvider.FACEBOOK_PROVIDER.toString() -> {
                            token?.let { tokenNonNull ->
                                FacebookAuthProvider.getCredential(tokenNonNull)
                            }
                        }

                        UserAuthProvider.GOOGLE_PROVIDER.toString() -> {
                            if (::googleCredential.isInitialized) {
                                googleCredential
                            } else {
                                null
                            }
                        }

                        UserAuthProvider.EMAIL_PROVIDER.toString() -> {
                            val email = model.firebaseUser?.email
                            var credentialEmailAuthProvider: AuthCredential? = null
                            val password = RequestPasswordBottomSheetDialog(context).show()
                            email?.let {
                                password?.let {
                                    credentialEmailAuthProvider = EmailAuthProvider
                                        .getCredential(email, password)
                                }
                            }

                            credentialEmailAuthProvider
                        }

                        else -> {
                            null
                        }
                    }

                } catch (e: CancellationException) {
                    exceptionOccurred = true
                    snackBar(getString(R.string.user_profile_fragment_snackbar_message_cancellation_exception_on_request_password))
                    null
                }

            if (!exceptionOccurred) {
                credential?.let {
                    onSuccess(it)
                } ?: kotlin.run {
                    snackBar(getString(R.string.user_profile_fragment_snackbar_message_session_expired))
                    exitFromFragment()
                }
            }
        }
    }

    private fun reAuthenticate(
        credential: AuthCredential,
        onSuccess: () -> Unit
    ) {
        model.reAuthenticate(credential, onSuccess = {
            onSuccess()
        }, onError = {
            val errorMessage = identifiesErrorFirebaseAuthOnReAuthenticate(it)
            snackBar(errorMessage)
        })
    }

    private fun identifiesErrorFirebaseAuthOnReAuthenticate(e: Exception): String {
        val errorMessage = when (e) {
            is FirebaseAuthInvalidCredentialsException -> getString(R.string.user_profile_fragment_snackbar_message_firebase_auth_invalid_cradentials_exception_on_reauthenticate)
            is FirebaseNetworkException -> getString(R.string.user_profile_fragment_snackbar_message_firebase_auth_network_exception_on_forgot_password)
            is FirebaseAuthInvalidUserException -> getString(R.string.user_profile_fragment_snackbar_message_firebase_auth_invalid_user_exception_on_reauthenticate)
            is FirebaseAuthRecentLoginRequiredException -> getString(R.string.user_profile_fragment_snackbar_message_firebase_auth_recent_login_required_exception)
            else -> getString(R.string.user_profile_fragment_snackbar_message_unknown_error)
        }
        return errorMessage
    }

    private fun identifiesErrorFirebaseAuthOnUpdateUserEmailAndUserName(e: Exception): String {
        val errorMessage = when (e) {
            is FirebaseAuthInvalidCredentialsException -> getString(R.string.user_profile_fragment_snackbar_message_firebase_auth_Invalid_credentials_Exception)
            is FirebaseNetworkException -> getString(R.string.user_profile_fragment_snackbar_message_firebase_auth_network_exception_on_forgot_password)
            is FirebaseAuthUserCollisionException -> getString(R.string.user_profile_fragment_snackbar_message_firebase_auth_user_collision_exception)
            is FirebaseAuthRecentLoginRequiredException -> getString(R.string.user_profile_fragment_snackbar_message_firebase_auth_recent_login_required_exception)
            else -> getString(R.string.user_profile_fragment_snackbar_message_unknown_error)
        }
        return errorMessage
    }

    private fun onClickDeleteUserTextView() {

        context?.let { context ->
            binding.fragmentProfileTextViewDeleteUser.setOnClickListener {

                context.alertDialog(
                    title = context.getString(R.string.user_profile_fragment_alert_dialog_delete_account_title),
                    message = context.getString(R.string.user_profile_fragment_alert_dialog_delete_account_description),
                    icon = R.drawable.ic_exit,
                    onClickingOnPositiveButton = {


                        appViewModel.tryRemoveUserPhoto(onError = {
                            snackBar(getString(R.string.user_profile_fragment_snackbar_message_error_on_delete_user))
                        }, onSuccessful = {

                            model.deleteUser(onSuccess = {
                                snackBar(getString(R.string.user_profile_fragment_snackbar_message_success_on_delete_user))
                                exitFromFragment()

                            }, onError = { exception ->
                                if (exception is FirebaseAuthRecentLoginRequiredException) {
                                    lifecycleScope.launch {
                                        tryToGetCredential {
                                            reAuthenticate(it, onSuccess = {
                                                model.deleteUser(onSuccess = {

                                                    exitFromFragment()
                                                    snackBar(getString(R.string.user_profile_fragment_snackbar_message_success_on_delete_user))

                                                }, onError = {
                                                    snackBar(getString(R.string.user_profile_fragment_snackbar_message_error_on_delete_user))

                                                })
                                            })
                                        }
                                    }
                                } else {
                                    snackBar(getString(R.string.user_profile_fragment_snackbar_message_error_on_delete_user))
                                }
                            })
                        })
                    }
                )
            }
        }
    }

    private fun exitFromFragment() {
        val mainActivity = activity as MainActivity
        mainActivity.performExitProcess()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}
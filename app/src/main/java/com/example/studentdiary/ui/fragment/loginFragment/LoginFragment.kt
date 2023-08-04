package com.example.studentdiary.ui.fragment.loginFragment

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.studentdiary.R
import com.example.studentdiary.databinding.FragmentLoginBinding
import com.example.studentdiary.datastore.getUserTokenAuth
import com.example.studentdiary.datastore.removeUserTokenAuth
import com.example.studentdiary.datastore.setUserProvider
import com.example.studentdiary.datastore.setUserTokenAuth
import com.example.studentdiary.extensions.googleSignInClient
import com.example.studentdiary.extensions.isOnline
import com.example.studentdiary.extensions.showToastNoConnectionMessage
import com.example.studentdiary.extensions.snackBar
import com.example.studentdiary.model.User
import com.example.studentdiary.ui.AppViewModel
import com.example.studentdiary.ui.FACEBOOK_PERMISSION_EMAIL
import com.example.studentdiary.ui.FACEBOOK_PERMISSION_PROFILE
import com.example.studentdiary.ui.NavigationComponents
import com.example.studentdiary.ui.dialog.ForgotPasswordBottomSheetDialog
import com.example.studentdiary.utils.enums.UserAuthProvider
import com.example.studentdiary.utils.exitGoogleAndFacebookAccount
import com.example.studentdiary.utils.validateEmailFormat
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.SignInButton
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import org.json.JSONException
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val model: LoginViewModel by viewModel()
    private val controller by lazy {
        findNavController()
    }
    private val appViewModel: AppViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigationComponents()
        configureObserverLogin()
        configureLoginButton()
        configureLoginGoogleAccountButton()
        configureLoginFacebookAccountButton()
        textViewRegister()
        configureAndSaveFields()
        updateUi()
        forgotPassword()
    }

    override fun onResume() {
        super.onResume()
        clearErrorFields()
    }


    private fun forgotPassword() {
        binding.fragmentLoginTextViewForgotPassword.setOnClickListener {
            context?.let { context ->
                ForgotPasswordBottomSheetDialog(context).show { recoveryEmail ->
                    model.forgotPassword(recoveryEmail, onSuccess = {
                        snackBar(getString(R.string.login_fragment_snackbar_message_firebase_auth_on_sucess_forgot_password))
                    }, onFailure = { exception ->
                        snackBar(identifiesErrorFirebaseAuthOnSendPasswordResetEmail(exception))
                    })
                }
            }
        }
    }

    private fun identifiesErrorFirebaseAuthOnSendPasswordResetEmail(e: Exception): String {
        val errorMessage = when (e) {
            is FirebaseAuthInvalidUserException -> getString(R.string.login_fragment_snackbar_message_firebase_auth_invalid_user_exception_on_forgot_password)
            is FirebaseAuthInvalidCredentialsException -> getString(R.string.login_fragment_snackbar_message_firebase_auth_invalid_credentials_exception_on_forgot_password)
            is FirebaseTooManyRequestsException -> getString(R.string.login_fragment_snackbar_message_firebase_auth_many_request_exception_on_forgot_password)
            is FirebaseNetworkException -> getString(R.string.login_fragment_snackbar_message_firebase_auth_network_exception_on_forgot_password)
            else -> getString(R.string.login_fragment_snackbar_message_unknown_error)
        }
        return errorMessage
    }


    private fun configureAndSaveFields() {
        val fieldEmail = binding.fragmentLoginTextfieldEmail.editText
        val fieldPassword = binding.fragmentLoginTextfieldPassword.editText

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
    }

    private fun updateUi() {

        model.fieldEmail.observe(viewLifecycleOwner) { email ->
            email?.let {
                val fieldEmail = binding.fragmentLoginTextfieldEmail
                fieldEmail.editText?.setText(it)
                fieldEmail.editText?.setSelection(it.length)
            }
        }

        model.fieldPassword.observe(viewLifecycleOwner) { password ->
            password?.let {
                val fieldPassword = binding.fragmentLoginTextfieldPassword.editText
                fieldPassword?.setText(it)
                fieldPassword?.setSelection(it.length)
            }
        }
    }

    private fun setupNavigationComponents() {
        appViewModel.hasNavigationComponents =
            NavigationComponents(toolbar = false, menuDrawer = false)
    }


    private fun configureObserverLogin() {
        context?.let { context ->
            model.firebaseAuthLiveData.observe(viewLifecycleOwner) { resource ->
                resource?.let {
                    if (resource.data) {
                        goToDisciplinesFragment()
                        snackBar(getString(R.string.login_fragment_snackbar_message_login_success))
                    } else {
                        resource.exception?.let { exception ->
                            snackBar(identifiesErrorFirebaseAuthOnLogin(exception))
                            exitGoogleAndFacebookAccount(context)
                            model.clearLiveData()
                        }
                    }
                }
            }
        }
    }


    private fun identifiesErrorFirebaseAuthOnLogin(exception: Exception): String {
        val errorMessage = when (exception) {
            is FirebaseAuthInvalidCredentialsException -> getString(R.string.login_fragment_snackbar_message_firebase_auth_Invalid_credentials_Exception)
            is FirebaseAuthInvalidUserException -> getString(R.string.login_fragment_snackbar_message_firebase_auth_Invalid_user_Exception)
            is FirebaseAuthUserCollisionException -> getString(R.string.login_fragment_snackbar_message_firebase_auth_user_collision_Exception)
            is FirebaseNetworkException -> getString(R.string.login_fragment_snackbar_message_firebase_auth_network_exception)
            else -> getString(R.string.login_fragment_snackbar_message_unknown_error)
        }
        return errorMessage
    }

    private fun goToDisciplinesFragment() {
        val direction = LoginFragmentDirections.actionLoginFragmentToDisciplinesFragment()
        controller.navigate(direction)
    }

    private fun configureLoginButton() {
        val loginButton = binding.fragmentLoginLoginButton
        loginButton.setOnClickListener {
            clearErrorFields()

            val email = binding.fragmentLoginTextfieldEmail.editText?.text.toString()
            val password = binding.fragmentLoginTextfieldPassword.editText?.text.toString()

            val isValid = validateData(email, password)
            if (isValid) {
                authenticate(User(email, password))
            }
        }
    }

    private fun validateData(email: String, password: String): Boolean {
        var valid = true

        if (email.isBlank()) {
            binding.fragmentLoginTextfieldEmail.error =
                getString(R.string.login_fragment_text_field_error_email_required)
            valid = false
        } else if (!validateEmailFormat(email)) {
            binding.fragmentLoginTextfieldEmail.error =
                getString(R.string.login_fragment_text_field_email_not_format_email_error)
            valid = false
        }

        if (password.isBlank()) {
            binding.fragmentLoginTextfieldPassword.error =
                context?.getString(R.string.login_fragment_text_field_error_password_required)
            valid = false
        }
        return valid
    }


    private fun authenticate(user: User) = model.authenticate(user, onSuccess = {
        savePreferencesUserProvider(
            provider = UserAuthProvider.EMAIL_PROVIDER.toString()
        )
    })

    private fun configureLoginFacebookAccountButton() {
        val callbackManager = CallbackManager.Factory.create()
        val loginButton = binding.fragmentLoginSigninFacebookButton
        loginButton.setPermissions(
            FACEBOOK_PERMISSION_EMAIL,
            FACEBOOK_PERMISSION_PROFILE
        )
        loginButton.setFragment(this)
        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val credential =
                    FacebookAuthProvider.getCredential(result.accessToken.token)
                model.loginWithCredential(credential, onSuccess = {
                    savePreferencesUserProvider(
                        provider = UserAuthProvider.FACEBOOK_PROVIDER.toString(),
                        token = result.accessToken.token
                    )

                })
                searchFacebookUser()
            }

            override fun onCancel() {
                snackBar(getString(R.string.login_fragment_snackbar_message_login_facebook_cancel))
            }

            override fun onError(error: FacebookException) {

            }
        })
    }

    fun savePreferencesUserProvider(provider: String, token: String? = null) {
        lifecycleScope.launch {
            context?.let { context ->
                    setUserProvider(context, provider)

                    token?.let {
                        setUserTokenAuth(context, it)
                    } ?: kotlin.run {
                        getUserTokenAuth(context)?.let {
                            removeUserTokenAuth(context)
                        }
                    }
            }
        }
    }

    private fun searchFacebookUser() {
        val accessToken = AccessToken.getCurrentAccessToken()
        val request = GraphRequest.newMeRequest(accessToken) { jsonObject, _ ->
            try {
                val name = jsonObject?.getString("name")
                name?.let {
                    snackBar(name)
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        request.executeAsync()
    }

    private fun configureLoginGoogleAccountButton() {
        val loginGoogleButton = binding.fragmentLoginSigninGoogleButton
        val openGoogleLogin =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val googleAccount =
                        GoogleSignIn.getSignedInAccountFromIntent(result.data).result
                    val credential =
                        GoogleAuthProvider.getCredential(googleAccount.idToken, null)
                    model.loginWithCredential(credential, onSuccess = {

                        savePreferencesUserProvider(
                            provider = UserAuthProvider.GOOGLE_PROVIDER.toString()
                        )

                    })
                }
            }

        loginGoogleButton.setSize(SignInButton.SIZE_WIDE)
        loginGoogleButton.setOnClickListener {
            context?.let { context ->
                if (context.isOnline()) {
                    val client = context.googleSignInClient()
                    val intent = client.signInIntent
                    openGoogleLogin.launch(intent)
                } else {
                    context.showToastNoConnectionMessage()
                }
            }

        }
    }

    private fun clearErrorFields() {
        binding.fragmentLoginTextfieldEmail.error = null
        binding.fragmentLoginTextfieldPassword.error = null
    }


    private fun textViewRegister() {
        binding.fragmentLoginTextViewRegister.setOnClickListener {
            goToRegisterFragment()
            model.clearLiveData()
        }
    }

    private fun goToRegisterFragment() {
        controller.navigate(R.id.action_loginFragment_to_registerFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



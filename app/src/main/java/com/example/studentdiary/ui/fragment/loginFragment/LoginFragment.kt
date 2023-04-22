package com.example.studentdiary.ui.fragment.loginFragment

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.studentdiary.R
import com.example.studentdiary.databinding.FragmentLoginBinding
import com.example.studentdiary.extensions.googleSignInClient
import com.example.studentdiary.extensions.snackBar
import com.example.studentdiary.model.User
import com.example.studentdiary.ui.AppViewModel
import com.example.studentdiary.ui.NavigationComponents
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import org.json.JSONException
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.Exception


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
        overridePopBackStack()
        logout()
        configureAndSaveTextFields()
    }

    override fun onStart() {
        super.onStart()
        fillFields()
    }

    override fun onResume() {
        super.onResume()
        clearErrorFields()
    }

    private fun configureAndSaveTextFields(){
        val textFieldEmail = binding.fragmentLoginTextfieldEmail.editText
        val textFieldPassword = binding.fragmentLoginTextfieldPassword.editText

        textFieldEmail?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && textFieldEmail?.text.toString().isNotBlank()){
                    model.setEmail(textFieldEmail?.text.toString())
            }
        }

        textFieldPassword?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && textFieldPassword?.text.toString().isNotBlank()){
                    model.setPassword(textFieldPassword?.text.toString())
            }
        }
    }

    private fun fillFields(){
        Log.i("TAG", "fillFields: ${model.fieldPassword.value}")
        model.fieldEmail.observe(this@LoginFragment){email ->
            email?.let {
                val textFieldEmail = binding.fragmentLoginTextfieldEmail
                textFieldEmail.editText?.setText(it)
                textFieldEmail.editText?.setSelection(it.length)
            }
        }

        model.fieldPassword.observe(this@LoginFragment){password ->
            password?.let {
                val textFieldPassword = binding.fragmentLoginTextfieldPassword.editText
                textFieldPassword?.setText(it)
                Log.i("TAG", "fillFields: $it")
                textFieldPassword?.setSelection(it.length)
            }
        }
    }

    private fun overridePopBackStack() {
        activity?.let { activity ->
            activity.onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                activity.finish()
            }
        }
    }

    private fun setupNavigationComponents() {
        appViewModel.hasNavigationComponents =
            NavigationComponents(navigationIcon = false, menuDrawer = false)
    }

    private fun configureObserverLogin() {
        model.firebaseAuthLiveData.observe(viewLifecycleOwner) { resource ->
            resource?.let {
                if (resource.data) {
                    goToDisciplinesFragment()
                    snackBar(getString(R.string.login_fragment_snackbar_message_login_success))
                } else {
                    resource.exception?.let { exception ->
                        val errorMessage = identifiesErrorFirebaseAuthOnLogin(exception)
                        exitGoogleAndFacebookAccount()
                        snackBar(errorMessage)
                    }
                }
            }
        }
    }

    private fun identifiesErrorFirebaseAuthOnLogin(exception: Exception): String {
        val errorMessage = when (exception) {
            is FirebaseAuthInvalidCredentialsException -> getString(R.string.login_fragment_snackbar_message_firebase_auth_Invalid_credentials_Exception)
            is FirebaseAuthInvalidUserException -> getString(R.string.login_fragment_snackbar_message_firebase_auth_Invalid_user_Exception)
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
                context?.getString(R.string.login_fragment_text_field_error_email_required)
            valid = false
        }

        if (password.isBlank()) {
            binding.fragmentLoginTextfieldPassword.error =
                context?.getString(R.string.login_fragment_text_field_error_password_required)
            valid = false
        }
        return valid
    }

    private fun authenticate(user: User) = model.authenticate(user)

    private fun configureLoginFacebookAccountButton() {
        val callbackManager = CallbackManager.Factory.create()
        val loginButton = binding.fragmentLoginSigninFacebookButton
        loginButton.setPermissions(
            getString(R.string.facebook_permission_email),
            getString(R.string.facebook_permission_profile)
        )
        loginButton.setFragment(this)
        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val credential =
                    FacebookAuthProvider.getCredential(result.accessToken.token)
                model.linkFacebookAccount(credential)
                searchFacebookUser()
            }

            override fun onCancel() {
                snackBar(getString(R.string.login_fragment_snackbar_message_login_facebook_cancel))
            }

            override fun onError(error: FacebookException) {

            }
        })
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
                    model.linkGoogleAccount(credential)

                }
            }

        loginGoogleButton.setSize(SignInButton.SIZE_WIDE)
        loginGoogleButton.setOnClickListener {
            val client = context?.googleSignInClient()
            val intent = client?.signInIntent
            openGoogleLogin.launch(intent)
        }
    }

    private fun clearErrorFields() {
        binding.fragmentLoginTextfieldEmail.error = null
        binding.fragmentLoginTextfieldPassword.error = null
    }

    private fun logout() {
        if (model.isAuthenticated()){
            model.logout()
            exitGoogleAndFacebookAccount()
        }
    }

    private fun exitGoogleAndFacebookAccount() {
        context?.googleSignInClient()?.signOut()
        checkAndLogoutIfLoggedInFacebook()
    }

    private fun checkAndLogoutIfLoggedInFacebook() {
        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        if (isLoggedIn) {
            AccessToken.setCurrentAccessToken(null)
        }
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



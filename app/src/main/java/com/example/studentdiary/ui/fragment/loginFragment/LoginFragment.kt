package com.example.studentdiary.ui.fragment.loginFragment

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.studentdiary.R
import com.example.studentdiary.databinding.FragmentLoginBinding
import com.example.studentdiary.extensions.googleSignInClient
import com.example.studentdiary.extensions.snackBar
import com.example.studentdiary.model.User
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import org.json.JSONException
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val model: LoginViewModel by viewModel()
    private val controller by lazy {
        findNavController()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureObserverLogin()
        configureObserverGoogleAccount()
        observerFacebookAccount()
        configureLoginButton()
        configureLoginGoogleAccountButton()
        configureLoginFacebookAccountButton()
        botaoSairContaGoogle()
        registerButton()
    }

    private fun configureObserverLogin() {
        context?.let { context ->
            model.firebaseAuthLiveData.observe(viewLifecycleOwner) { resource ->
                resource?.let {
                    if (resource.data) {
                        view?.snackBar(context.getString(R.string.login_fragment_snackbar_message_login_success))
                    } else {
                        resource.exception?.let { exception ->
                            val errorMessage = when (exception) {
                                is FirebaseAuthInvalidCredentialsException -> context.getString(R.string.login_fragment_snackbar_message_firebase_auth_Invalid_credentials_Exception)
                                is FirebaseAuthInvalidUserException -> context.getString(R.string.login_fragment_snackbar_message_firebase_auth_Invalid_user_Exception)
                                else -> context.getString(R.string.login_fragment_snackbar_message_unknown_error)
                            }
                            view?.snackBar(errorMessage)
                        }
                    }
                }
            }
        }
    }

    private fun configureObserverGoogleAccount() {
        context?.let { context ->
            model.googleAccountLiveData.observe(viewLifecycleOwner) { resource ->
                resource?.let {
                    if (resource.data) {
                        view?.snackBar(context.getString(R.string.login_fragment_snackbar_message_login_success_googleAccount))
                    } else {
                        view?.snackBar(context.getString(R.string.login_fragment_snackbar_message_login_error_googleAccount))
                    }
                }
            }
        }
    }

    private fun observerFacebookAccount() {
        context?.let {
            model.facebookAccountLiveData.observe(viewLifecycleOwner) { resource ->
                resource?.let {
                    if (resource.data) {
                        view?.snackBar("Sucesso login com facebook")
                    } else {
                        view?.snackBar("erro login facebook")
                    }
                }
            }
        }
    }

    private fun configureLoginButton() {
        val loginButton = binding.fragmentLoginLoginButton
        loginButton.setOnClickListener {
            cleanFields()
            val email = binding.fragmentLoginTextfieldEmail.editText?.text.toString()
            val password = binding.fragmentLoginTextfieldPassword.editText?.text.toString()

            val isvalid = validateData(email, password)
            if (isvalid) {
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
        loginButton.setPermissions("email", "public_profile")
        loginButton.setFragment(this)

        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {

                val credential =
                    FacebookAuthProvider.getCredential(result.accessToken.token)
                model.linkFacebookAccount(credential)
                view?.snackBar("foi")
                searchFacebookUser()

                //sair da conta facebook
                //AccessToken.setCurrentAccessToken(null)

            }

            override fun onCancel() {
                view?.snackBar(" nao foi, cancelado")
            }

            override fun onError(error: FacebookException) {
                view?.snackBar(" nao foi ")
                Log.e("TAG", "onError: facebook ", error)
            }
        })
    }

    private fun searchFacebookUser() {
        val acessToken = AccessToken.getCurrentAccessToken()
        val request = GraphRequest.newMeRequest(acessToken) { jsonObject, _ ->
            try {
                val name = jsonObject?.getString("name")
                name?.let {
                    view?.snackBar(name)
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
                    val contaGoogle =
                        GoogleSignIn.getSignedInAccountFromIntent(result.data).result
                    val credencial =
                        GoogleAuthProvider.getCredential(contaGoogle.idToken, null)
                    model.linkGoogleAccount(credencial)

                }
            }

        loginGoogleButton.setSize(SignInButton.SIZE_WIDE)
        loginGoogleButton.setOnClickListener {
            val client = context?.googleSignInClient()
            val intent = client?.signInIntent
            openGoogleLogin.launch(intent)
        }
    }

    private fun cleanFields() {
        binding.fragmentLoginTextfieldEmail.error = null
        binding.fragmentLoginTextfieldPassword.error = null
    }

    private fun botaoSairContaGoogle() {
        binding.fragmentLoginSairGoogle.setOnClickListener {
            context?.googleSignInClient()?.signOut()
            view?.snackBar("Saiu da conta google")
        }
    }

    private fun registerButton(){
        binding.fragmentLoginRegisterButton.setOnClickListener {
            controller.navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
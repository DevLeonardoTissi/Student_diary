package com.example.studentdiary.ui.fragment.profileFragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
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
    private var googleCredential: AuthCredential? = null
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

    private fun validateData(email: String): Boolean {
        var valid = true


        if (email.isBlank()) {
            binding.profileFragmentTextFieldEmail.error =
                "campo email vazio"
            valid = false
        } else if (!validateEmailFormat(email)) {
            binding.profileFragmentTextFieldEmail.error =
                "digite um email válido"
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
                // Verificar campos e , se tudo certo, chama o aler dialog
                model.updateUserEmailAndUserName(name = name, email = email,
                    onSuccess = {
                        snackBar(getString(R.string.user_profile_fragment_snackbar_message_success_on_update_user_information))
                    }, onError = { exception ->
                        lifecycleScope.launch {
                            if (exception is FirebaseAuthRecentLoginRequiredException) {
                                Log.i("TAG", "onclickFinishedButton: muito tempo sem entrar")

                                tryToGetCredential(onSuccess = {
                                    reauthenticate(it, onSuccess = {
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
                            googleCredential
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
                    snackBar("Senha necessária")
                    null
                }

            if (!exceptionOccurred) {
                credential?.let {
                    onSuccess(it)
                } ?: kotlin.run {
                    snackBar("Sessão expirada, faça login novamente")
                }
            }
        }
    }

    private fun reauthenticate(
        credential: AuthCredential,
        onSuccess: () -> Unit
    ) {


        model.reauthenticate(credential, onSuccess = {
            onSuccess()
        }, onError = {
            //Pode haver outros erros, ex internet, ou credencial inválida gerada com email e senha
            Log.i("TAG", "onclickFinishedButton: $it")
            snackBar("faça login novamente para alterar os dados, $it")
        })
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

        // REFATORAR MÉTODO
        context?.let { context ->
            binding.fragmentProfileTextViewDeleteUser.setOnClickListener {

                context.alertDialog(
                    title = "Excluir conta",
                    "Deseja realmente excluir a conta?",
                    icon = R.drawable.ic_exit,
                    onClickingOnPositiveButton = {

                        model.deleteUser(onSuccess = {
                            snackBar("Sucesso ao deletar usuário")
                            exitFromFragment()
                        }, onError = {
                            //identifica erro e, se for de autenticação:
                            //implementar
                            lifecycleScope.launch {
                                tryToGetCredential {
                                    reauthenticate(it, onSuccess = {


                                        context.alertDialog(
                                            title = "Excluir conta",
                                            "Deseja realmente excluir a conta?",
                                            icon = R.drawable.ic_exit,
                                            onClickingOnPositiveButton = {

                                                model.deleteUser(onSuccess = {
                                                    snackBar("sucesso ao deletar depois de reautenticar")

                                                    exitFromFragment()

                                                }, onError = {

                                                    // identificar erro
                                                    snackBar("erro ao deletar depois de reautenticar")
                                                })


                                            })
                                    })
                                }
                            }
                        })


                    })


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
package com.example.studentdiary.ui.fragment.registerFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.studentdiary.R
import com.example.studentdiary.databinding.FragmentRegisterBinding
import com.example.studentdiary.extensions.snackBar
import com.example.studentdiary.model.User
import com.example.studentdiary.repository.DictionaryRepository
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val model: RegisterViewModel by viewModel()
    private val dicionarioRepo: DictionaryRepository by inject()


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

        lifecycleScope.launch {
            val respostaSignificado = dicionarioRepo.searchMeaning("sorriso")
            Log.i("TAG", "onViewCreated: $respostaSignificado")

            val respostaSinonimo = dicionarioRepo.searchSynonyms("sorriso")
            Log.i("TAG", "onViewCreated: $respostaSinonimo")

            val respostaSilaba = dicionarioRepo.searchSyllables("sorriso")
            Log.i("TAG", "onViewCreated: $respostaSilaba")

            val respostaFrase = dicionarioRepo.searchSentences("sorriso")
            Log.i("TAG", "onViewCreated: $respostaFrase")

        }
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
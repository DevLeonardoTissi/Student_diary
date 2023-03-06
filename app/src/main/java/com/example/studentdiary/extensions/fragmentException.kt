package com.example.studentdiary.extensions

import androidx.fragment.app.Fragment
import com.example.studentdiary.R
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

fun Fragment.identifiesError(exception: java.lang.Exception): String {
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
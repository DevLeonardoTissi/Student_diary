package com.example.studentdiary.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import com.example.studentdiary.R
import com.example.studentdiary.databinding.RequestPasswordDialogBinding
import com.example.studentdiary.utils.CancellationException
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class RequestPasswordBottomSheetDialog(private val context: Context) {
    suspend fun show(): String? = suspendCancellableCoroutine { continuation ->
        val dialog = BottomSheetDialog(context)
        val binding = RequestPasswordDialogBinding.inflate(LayoutInflater.from(context))

        dialog.setContentView(binding.root)
        binding.requestPasswordDialogButtonPositive.setOnClickListener {
            val fieldPassword = binding.requestPasswordDialogTextFieldPassword
            fieldPassword.error = null

            val password = fieldPassword.editText?.text.toString().trim()

            if (password.isBlank()) {
                fieldPassword.error =
                    context.getString(R.string.request_password_dialog_text_field_password_empty_error)
            } else if (password.length < 6) {
                fieldPassword.error =
                    context.getString(R.string.request_password_dialog_text_field_password_not_valid_password_error)
            } else {
                dialog.dismiss()
                continuation.resume(password)
            }
        }

        binding.requestPasswordDialogButtonNegative.setOnClickListener {
            dialog.dismiss()
            continuation.cancel(CancellationException())
        }

        dialog.setOnCancelListener { continuation.cancel(CancellationException()) }

        dialog.show()
        continuation.invokeOnCancellation { dialog.dismiss() }
    }
}

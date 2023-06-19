package com.example.studentdiary.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import com.example.studentdiary.R
import com.example.studentdiary.databinding.ForgotPasswordAlertDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class ForgotPasswordBottomSheetDialog(private val context: Context) {

    fun show(recoveryEmail: (email: String) -> Unit) {
        val dialog = BottomSheetDialog(context)
        val forgotPasswordBinding =
            ForgotPasswordAlertDialogBinding.inflate(LayoutInflater.from(context))
                .apply {
                    forgotPasswordDialogButtonPositive.setOnClickListener {
                        val fieldEmail = forgotPasswordDialogTextFieldEmail
                        fieldEmail.error = null

                        val email =
                            forgotPasswordDialogTextFieldEmail.editText?.text.toString()
                                .trim()
                        if (email.isBlank()) {
                            fieldEmail.error =
                                context.getString(R.string.forgot_password_alert_dialog_text_field_email_empty_error)
                        } else {
                            recoveryEmail(email)
                            dialog.dismiss()
                        }
                    }

                    forgotPasswordDialogButtonNegative.setOnClickListener {
                        dialog.dismiss()
                    }
                }


        dialog.setContentView(
            forgotPasswordBinding.root
        )
        dialog.show()
    }

}
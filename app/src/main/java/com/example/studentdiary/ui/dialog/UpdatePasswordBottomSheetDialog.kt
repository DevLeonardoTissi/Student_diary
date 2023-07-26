package com.example.studentdiary.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import com.example.studentdiary.R
import com.example.studentdiary.databinding.UpdatePasswordDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class UpdatePasswordBottomSheetDialog(private val context: Context) {


    fun show(onNewPassword: (newPassword: String) -> Unit) {
        val dialog = BottomSheetDialog(context)

        UpdatePasswordDialogBinding.inflate(LayoutInflater.from(context))
            .apply {

                updatePasswordDialogButtonPositive.setOnClickListener {

                    val fieldPassword = updatePasswordDialogTextFieldPassword
                    fieldPassword.error = null

                    val password = fieldPassword.editText?.text.toString().trim()

                    if (password.isBlank()) {
                        fieldPassword.error =
                            context.getString(R.string.update_password_dialog_text_field_password_empty_error)
                    } else if (password.length < 6) {
                        fieldPassword.error =
                            context.getString(R.string.update_password_dialog_text_field_password_not_valid_password_error)
                    } else {
                        onNewPassword(password)
                        dialog.dismiss()
                    }
                }

                updatePasswordDialogButtonNegative.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.setContentView(root)
                dialog.show()

            }


    }


}
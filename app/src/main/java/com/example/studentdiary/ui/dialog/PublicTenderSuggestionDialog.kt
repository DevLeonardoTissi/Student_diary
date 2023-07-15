package com.example.studentdiary.ui.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.example.studentdiary.R
import com.example.studentdiary.databinding.PublicTenderSuggestionDialogBinding
import com.example.studentdiary.utils.PublicTenderSuggestion
import com.example.studentdiary.utils.validateUrlFormat

class PublicTenderSuggestionDialog(private val context: Context) {

    fun show(publicTender: (publicTenderSuggestion: PublicTenderSuggestion?) -> Unit) {
        PublicTenderSuggestionDialogBinding.inflate(LayoutInflater.from(context)).apply {

            val dialog = AlertDialog.Builder(context)
                .setView(root)
                .show()

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            publicTenderSuggestionDialogButtonPositive.setOnClickListener {
                clearErrorTextFields()

                var valid = true

                val fieldName = publicTenderSuggestionDialogTextInputLayoutName
                val name = fieldName.editText?.text.toString()
                val fieldUrl = publicTenderSuggestionDialogTextInputLayoutUrl
                val url = fieldUrl.editText?.text.toString()

                if (name.isBlank()) {
                    fieldName.error =
                        context.getString(R.string.public_tender_suggestion_dialog_text_field_error)
                    valid = false
                }

                if (url.isBlank()) {
                    fieldUrl.error =
                        context.getString(R.string.public_tender_suggestion_dialog_text_field_error)
                    valid = false

                } else if (!validateUrlFormat(url)) {
                    fieldUrl.error =
                        context.getString(R.string.public_tender_suggestion_dialog_text_field_url_not_format_email_error)
                    valid = false
                }

                val description =
                    publicTenderSuggestionDialogTextInputLayoutDescription.editText?.text.toString()

                if (valid) {

                    publicTender(
                        PublicTenderSuggestion(
                            name = name,
                            description = description.ifBlank { null },
                            url = url
                        )
                    )

                    dialog.dismiss()
                }
            }

            publicTenderSuggestionDialogButtonNegative.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private fun PublicTenderSuggestionDialogBinding.clearErrorTextFields() {
        publicTenderSuggestionDialogTextInputLayoutName.error = null
        publicTenderSuggestionDialogTextInputLayoutUrl.error = null
    }

}
package com.example.studentdiary.ui.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.example.studentdiary.R
import com.example.studentdiary.databinding.PublicTenderSuggestionDialogBinding
import com.example.studentdiary.utils.PublicTenderSuggestion

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
                val name =
                    publicTenderSuggestionDialogTextInputLayoutName.editText?.text.toString()
                if (name.isBlank()) {
                    val fieldName = publicTenderSuggestionDialogTextInputLayoutName
                    fieldName.error =
                        context.getString(R.string.public_tender_suggestion_dialog_text_field_error)
                    valid = false
                }

                val url =
                    publicTenderSuggestionDialogTextInputLayoutUrl.editText?.text.toString()
                if (url.isBlank()) {
                    val fieldUrl = publicTenderSuggestionDialogTextInputLayoutUrl
                    fieldUrl.error =
                        context.getString(R.string.public_tender_suggestion_dialog_text_field_error)
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
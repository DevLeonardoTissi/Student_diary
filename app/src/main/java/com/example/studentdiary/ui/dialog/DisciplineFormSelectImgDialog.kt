package com.example.studentdiary.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.example.studentdiary.R
import com.example.studentdiary.databinding.DisciplineFormDialogBinding
import com.example.studentdiary.extensions.tryLoadImage
import com.example.studentdiary.utils.validateUrlFormat

class DisciplineFormSelectImgDialog(private val context: Context) {

    fun show(
        imageUrl: String? = null,
        onClickPositiveButton: (imageUrl: String?) -> Unit
    ) {

        DisciplineFormDialogBinding.inflate(LayoutInflater.from(context)).apply {
            val alertDialog =  AlertDialog.Builder(context).create()

            imageUrl?.let {
                disciplineFormImageDialogImageView.tryLoadImage(it)
                disciplineFormImageDialogTextInputLayoutUrl.editText?.setText(it)
            }

            disciplineFormImageDialogButtonRemove.setOnClickListener {
                disciplineFormImageDialogImageView.tryLoadImage()
                disciplineFormImageDialogTextInputLayoutUrl.editText?.text = null
            }

            disciplineFormImageDialogButtonLoad.setOnClickListener {
               val fieldUrl = disciplineFormImageDialogTextInputLayoutUrl
                   fieldUrl.error = null

                val url = disciplineFormImageDialogTextInputLayoutUrl.editText?.text.toString()
                if (url.isBlank()){
                    disciplineFormImageDialogTextInputLayoutUrl.error = context.getString(R.string.discipline_form_image_dialog_field_url_empty_error)
                    disciplineFormImageDialogImageView.tryLoadImage()

                }else if (!validateUrlFormat(url)){
                    disciplineFormImageDialogTextInputLayoutUrl.error =  context.getString(R.string.discipline_form_image_dialog_field_url_not_format_error)
                    disciplineFormImageDialogImageView.tryLoadImage()
                }
                else{
                    disciplineFormImageDialogImageView.tryLoadImage(url)
                }
            }

            disciplineFormImageDialogButtonPositive.setOnClickListener {
                val url = disciplineFormImageDialogTextInputLayoutUrl.editText?.text.toString()
                if (url.isNotBlank()){
                    onClickPositiveButton(url)
                } else{
                    onClickPositiveButton(null)
                }
                alertDialog.dismiss()
            }

            disciplineFormImageDialogButtonNegative.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.setView(root)
            alertDialog.show()

        }
    }
}
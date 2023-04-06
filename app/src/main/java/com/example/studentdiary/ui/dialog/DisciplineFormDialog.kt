package com.example.studentdiary.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.example.studentdiary.R
import com.example.studentdiary.databinding.DisciplineFormDialogBinding
import com.example.studentdiary.extensions.tryLoadImage

class DisciplineFormDialog(private val context: Context) {

    fun show(
        url: String? = null,
        onImageClick: (imageUrl: String) -> Unit
    ) {

        DisciplineFormDialogBinding.inflate(LayoutInflater.from(context)).apply {
            url?.let {
                disciplineFormImageDialogImageView.tryLoadImage(it)
                disciplineFormImageDialogTextInputLayoutUrl.editText?.setText(it)
            }

            disciplineFormImageDialogButtonLoad.setOnClickListener {
                val url = disciplineFormImageDialogTextInputLayoutUrl.editText?.text.toString()
                disciplineFormImageDialogImageView.tryLoadImage(url)
            }

            AlertDialog.Builder(context)
                .setView(root)
                .setPositiveButton(context.getString(R.string.common_confirm)) { _, _ ->
                    val url = disciplineFormImageDialogTextInputLayoutUrl.editText?.text.toString()
                    onImageClick(url)
                }
                .setNegativeButton(context.getString(R.string.common_cancel)){_, _ ->

                }
                .show()
        }

    }
}
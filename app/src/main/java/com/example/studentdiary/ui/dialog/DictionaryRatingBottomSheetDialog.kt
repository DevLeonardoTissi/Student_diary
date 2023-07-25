package com.example.studentdiary.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import com.example.studentdiary.databinding.DictionaryRatingBottomSheetDialogBinding
import com.example.studentdiary.webClient.model.RatingSender
import com.google.android.material.bottomsheet.BottomSheetDialog

class DictionaryRatingBottomSheetDialog(private val context: Context) {

    fun show(onComment:(rating:RatingSender) -> Unit) {
        val dialog = BottomSheetDialog(context)
        DictionaryRatingBottomSheetDialogBinding.inflate(LayoutInflater.from(context)).apply {

            dictionaryRatingBottomSheetDialogButtonNegative.setOnClickListener {
                dialog.dismiss()
            }

            dictionaryRatingBottomSheetDialogButtonPositive.setOnClickListener {
                val comment = dictionaryRatingBottomSheetDialogFieldComment.editText?.text.toString().trim()
                val rating = dictionaryBottomSheetDialogRatingBar.rating

                onComment(RatingSender(rating = rating, comment = comment.ifBlank { null }))
                dialog.dismiss()
            }


            dialog.setContentView(root)
            dialog.show()
        }
    }

}
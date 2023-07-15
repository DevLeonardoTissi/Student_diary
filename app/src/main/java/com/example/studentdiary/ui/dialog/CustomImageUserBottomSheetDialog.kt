package com.example.studentdiary.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import com.example.studentdiary.databinding.CustomImageBottomSheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class CustomImageUserBottomSheetDialog(private val context: Context) {
    fun show(onClickGalleryButton: () -> Unit = {}, onClickRemoveButton: () -> Unit = {}) {
        val customImageBottomSheetDialog = BottomSheetDialog(context)
        CustomImageBottomSheetDialogBinding.inflate(LayoutInflater.from(context)).apply {

            setupImageBottomSheetDialogGalleryButton.setOnClickListener {
                onClickGalleryButton()
                customImageBottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_HIDDEN
            }

            setupImageBottomSheetDialogRemoveButton.setOnClickListener {
                onClickRemoveButton()
                customImageBottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_HIDDEN
            }

            customImageBottomSheetDialog.setContentView(root)
            customImageBottomSheetDialog.show()
        }
    }
}
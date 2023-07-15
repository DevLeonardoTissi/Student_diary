package com.example.studentdiary.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import com.example.studentdiary.databinding.AppInfoBottomSheetDialogBinding
import com.example.studentdiary.ui.GITHUB_LINK
import com.example.studentdiary.ui.LINKEDIN_LINK
import com.example.studentdiary.ui.STUDENT_DIARY_GITHUB_LINK
import com.example.studentdiary.utils.goToUri
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class AppInfoBottomSheetDialog(private val context: Context) {
    fun show() {
        val appInfoBottomSheetDialog = BottomSheetDialog(context)
        AppInfoBottomSheetDialogBinding.inflate(LayoutInflater.from(context)).apply {
            appInfoBottomSheetDialogChipGitHub.setOnClickListener {
                appInfoBottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_HIDDEN
                goToUri(GITHUB_LINK, context)
            }

            appInfoBottomSheetDialogChipGitHubProject.setOnClickListener {
                appInfoBottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_HIDDEN
                goToUri(STUDENT_DIARY_GITHUB_LINK, context)
            }

            appInfoBottomSheetDialogChipLinkedin.setOnClickListener {
                appInfoBottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_HIDDEN
                goToUri(LINKEDIN_LINK, context)
            }

            appInfoBottomSheetDialog.setContentView(root)
            appInfoBottomSheetDialog.show()
        }
    }
}
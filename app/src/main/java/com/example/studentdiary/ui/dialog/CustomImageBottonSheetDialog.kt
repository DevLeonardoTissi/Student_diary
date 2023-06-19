package com.example.studentdiary.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import com.example.studentdiary.databinding.CustomImageBottomSheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class CustomImageBottonSheetDialog(private val context:Context) {


      fun show(){
         val customImageBottomSheetDialog = BottomSheetDialog(context)
         CustomImageBottomSheetDialogBinding.inflate(LayoutInflater.from(context)).apply {

             setupImageBottomSheetDialogGalleryButton.setOnClickListener {

//                    if (hasPermission()) {
//                        openGallery()
//                    } else {
//                        requestPermission.launch(READ_EXTERNAL_STORAGE)
//                    }

                 customImageBottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_HIDDEN
             }


             customImageBottomSheetDialog.setContentView(root)
             customImageBottomSheetDialog.show()


         }
     }
}
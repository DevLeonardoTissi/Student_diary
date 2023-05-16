package com.example.studentdiary.ui.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.example.studentdiary.databinding.AletDialogProgressBarBinding

class LoadAlertDialog(private val context:Context) {

    private lateinit var alertDialog: AlertDialog

    fun showLoadDialog(){
        alertDialog = AlertDialog.Builder(context)
            .setView(AletDialogProgressBarBinding.inflate(LayoutInflater.from(context)).root)
            .setCancelable(false)
            .create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

    fun closeLoadDialog(){
        alertDialog.dismiss()
    }


}
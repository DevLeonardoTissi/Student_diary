package com.example.studentdiary.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import com.example.studentdiary.databinding.SelectPomodoroCyclesLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class SelectPomodoroCyclesBottomSheet(private val context: Context) {

    fun show(numCycles:Int, onPositiveButton:(Int) -> Unit){
        val dialog = BottomSheetDialog(context)
        SelectPomodoroCyclesLayoutBinding.inflate(LayoutInflater.from(context)).apply {
        selectPomodoroCyclesDialogSliderInterval.setValues(numCycles.toFloat())
            selectPomodoroCyclesDialogButtonPositive.setOnClickListener {
                onPositiveButton(selectPomodoroCyclesDialogSliderInterval.values.first().toInt())
                dialog.dismiss()
            }
            selectPomodoroCyclesDialogButtonNegative.setOnClickListener {
                dialog.dismiss()
            }

            dialog.setContentView(root)
            dialog.show()
        }
    }
}
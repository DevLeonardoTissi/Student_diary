package com.example.studentdiary.extensions

import androidx.fragment.app.Fragment
import com.example.studentdiary.R
import com.google.android.material.snackbar.Snackbar


fun Fragment.snackBar(
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT,
    ) {
    view?.let { view ->
        context?.let { context ->
            Snackbar.make(
                view,
                message,
                duration
            )
                .setActionTextColor(context.getColor(R.color.colorOnPrimary))
                .setTextColor(context.getColor(R.color.colorOnPrimary))
                .setBackgroundTint(context.getColor(R.color.colorPrimary))
                .setAction(context.getString(R.string.common_ok)) {

                }
                .show()
        }
    }
}

package com.example.studentdiary.extensions

import androidx.fragment.app.Fragment
import com.example.studentdiary.R
import com.google.android.material.snackbar.Snackbar


fun Fragment.snackBar(
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT
) {
    view?.let {
        Snackbar.make(
            it,
            message,
            duration
        ).setAction(resources.getString(R.string.common_ok)) {

        }
            .show()
    }
}

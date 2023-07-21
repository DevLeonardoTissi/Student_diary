package com.example.studentdiary.ui.dialog


import android.app.KeyguardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.studentdiary.R
import com.example.studentdiary.databinding.PublicTenderSuggestionDialogBinding
import com.example.studentdiary.utils.PublicTenderSuggestion
import com.example.studentdiary.utils.validateUrlFormat
import java.util.concurrent.Executor

class PublicTenderSuggestionDialog(private val context: Context, private val fragment: Fragment) {

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo



    fun show(publicTender: (publicTenderSuggestion: PublicTenderSuggestion?) -> Unit) {
        PublicTenderSuggestionDialogBinding.inflate(LayoutInflater.from(context)).apply {

            val dialog = AlertDialog.Builder(context)
                .setView(root)
                .show()

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            publicTenderSuggestionDialogButtonPositive.setOnClickListener {
                clearErrorTextFields()

                var valid = true

                val fieldName = publicTenderSuggestionDialogTextInputLayoutName
                val name = fieldName.editText?.text.toString()
                val fieldUrl = publicTenderSuggestionDialogTextInputLayoutUrl
                val url = fieldUrl.editText?.text.toString()

                if (name.isBlank()) {
                    fieldName.error =
                        context.getString(R.string.public_tender_suggestion_dialog_text_field_error)
                    valid = false
                }

                if (url.isBlank()) {
                    fieldUrl.error =
                        context.getString(R.string.public_tender_suggestion_dialog_text_field_error)
                    valid = false

                } else if (!validateUrlFormat(url)) {
                    fieldUrl.error =
                        context.getString(R.string.public_tender_suggestion_dialog_text_field_url_not_format_email_error)
                    valid = false
                }

                val description =
                    publicTenderSuggestionDialogTextInputLayoutDescription.editText?.text.toString()

                if (valid) {

                    val biometricManager = BiometricManager.from(context)


                    when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
                        BiometricManager.BIOMETRIC_SUCCESS -> {
                            val isDeviceSecure =
                                (context.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager)?.isDeviceSecure
                            isDeviceSecure?.let {
                                executor = ContextCompat.getMainExecutor(context)
                                biometricPrompt = BiometricPrompt(fragment, executor,
                                    object : BiometricPrompt.AuthenticationCallback() {
                                        override fun onAuthenticationError(
                                            errorCode: Int,
                                            errString: CharSequence
                                        ) {
                                            super.onAuthenticationError(errorCode, errString)
                                            Toast.makeText(
                                                context,
                                                "Authentication error: $errString",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        }

                                        override fun onAuthenticationSucceeded(
                                            result: BiometricPrompt.AuthenticationResult
                                        ) {
                                            super.onAuthenticationSucceeded(result)
                                            publicTender(
                                                PublicTenderSuggestion(
                                                    name = name,
                                                    description = description.ifBlank { null },
                                                    url = url
                                                )
                                            )
                                            dialog.dismiss()
                                        }

                                        override fun onAuthenticationFailed() {
                                            super.onAuthenticationFailed()
//                                            Toast.makeText(
//                                                context, "Authentication failed",
//                                                Toast.LENGTH_SHORT
//                                            )
//                                                .show()
                                        }
                                    })

                                promptInfo = BiometricPrompt.PromptInfo.Builder()
                                    .setTitle("Biometric login for my app")
                                    .setSubtitle("Log in using your biometric credential")
                                    .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
                                    .build()
                                biometricPrompt.authenticate(promptInfo)
                            }
                        }

                        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                                publicTender(
                                    PublicTenderSuggestion(
                                        name = name,
                                        description = description.ifBlank { null },
                                        url = url
                                    )
                                )
                                dialog.dismiss()
                            }


                        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                            publicTender(
                                PublicTenderSuggestion(
                                    name = name,
                                    description = description.ifBlank { null },
                                    url = url
                                )
                            )
                            dialog.dismiss()
                        }

                        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                            publicTender(
                                PublicTenderSuggestion(
                                    name = name,
                                    description = description.ifBlank { null },
                                    url = url
                                )
                            )
                            dialog.dismiss()
                        }

                        BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                            publicTender(
                                PublicTenderSuggestion(
                                    name = name,
                                    description = description.ifBlank { null },
                                    url = url
                                )
                            )
                            dialog.dismiss()
                        }

                        BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                            publicTender(
                                PublicTenderSuggestion(
                                    name = name,
                                    description = description.ifBlank { null },
                                    url = url
                                )
                            )
                            dialog.dismiss()
                        }

                        BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                            publicTender(
                                PublicTenderSuggestion(
                                    name = name,
                                    description = description.ifBlank { null },
                                    url = url
                                )
                            )
                            dialog.dismiss()
                        }
                    }
                }
            }

            publicTenderSuggestionDialogButtonNegative.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private fun PublicTenderSuggestionDialogBinding.clearErrorTextFields() {
        publicTenderSuggestionDialogTextInputLayoutName.error = null
        publicTenderSuggestionDialogTextInputLayoutUrl.error = null
    }

}
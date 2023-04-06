package com.example.studentdiary.ui.fragment.disciplineFormFragment

import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.studentdiary.R
import com.example.studentdiary.databinding.FragmentDisciplineFormBinding
import com.example.studentdiary.extensions.tryLoadImage
import com.example.studentdiary.model.Discipline
import com.example.studentdiary.ui.TAG_DATA_PICKER
import com.example.studentdiary.ui.TAG_TIME_PICKER
import com.example.studentdiary.ui.dialog.DisciplineFormDialog
import com.example.studentdiary.utils.formatTime
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DisciplineFormFragment : Fragment() {

    private var _binding: FragmentDisciplineFormBinding? = null
    private val binding get() = _binding!!
    private val model: DisciplineFormViewModel by viewModel()
    private val arguments by navArgs<DisciplineFormFragmentArgs>()


    private val disciplineId by lazy {
        arguments.testeId
    }

    private val controller by lazy {
        findNavController()
    }


    private var url: String? = null
    private var initialMinute: Int? = null
    private var initialHour: Int? = null
    private var finalHour: Int? = null
    private var finalMinute: Int? = null
    private var favorite: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDisciplineFormBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchDisciplineId()
        checkBoxFavorite()
        startTimeButton()
        endTimeButton()
        fabInsetImage()
        saveTextFieldsValue()
        doneButton()
        calendarButton()

    }

    private fun searchDisciplineId() {
        lifecycleScope.launch {
            disciplineId?.let {
                model.getDiscipline() ?: model.searchDisciplideForId(it)
                updateUi()
            } ?: kotlin.run {
                model.getDiscipline() ?: model.setDiscipline(Discipline())
                updateUi()
            }
        }
    }

    private fun updateUi() {
        model.discipline.observe(viewLifecycleOwner) { discipline ->
            discipline?.let {
                initialMinute = discipline.initialMinute
                initialHour = discipline.initialHourt
                finalHour = discipline.finalHour
                finalMinute = discipline.finalMinute
                favorite = discipline.favorite

                discipline.name?.let {
                    val textInputLayoutName = binding.disciplineFormFragmentTextfieldName
                    textInputLayoutName.editText?.setText(discipline.name)
                    textInputLayoutName.editText?.setSelection(it.length)
                }
                discipline.description?.let {
                    val textInputLayoutDescription =
                        binding.disciplineFormFragmentTextfieldDescription
                    textInputLayoutDescription.editText?.setText(discipline.description)
                    textInputLayoutDescription.editText?.setSelection(it.length)
                }


                binding.disciplineFormCheckBox.isChecked = favorite

                discipline.img?.let { url ->
                    binding.disciplineFormFragmentImageView.tryLoadImage(url)
                    this@DisciplineFormFragment.url = url
                }

                if (initialHour != null && initialMinute != null) {
                    binding.disciplineFormFragmentStartTime.text =
                        formatTime(initialHour, initialMinute)
                }

                if (finalHour != null && finalMinute != null) {
                    binding.disciplineFormFragmentEndTime.text = formatTime(finalHour, finalMinute)
                }
            }
        }
    }

    private fun checkBoxFavorite() {
        binding.disciplineFormCheckBox.setOnCheckedChangeListener { _, isChecked ->
            model.setFavorite(isChecked)
        }
    }

    private fun startTimeButton() {
        val startTimeButton = binding.disciplineFormFragmentStartTime
        startTimeButton.setOnClickListener {
            clearFocusTextFields()
            val picker =
                timePicker(
                    hour = initialHour,
                    minute = initialMinute,
                    message = resources.getString(R.string.discipline_form_fragment_title_timePicker_start)
                )
            picker.show(childFragmentManager, TAG_TIME_PICKER)
            picker.addOnPositiveButtonClickListener {
                model.setInitialHour(picker.hour, picker.minute)
            }
        }
    }

    private fun endTimeButton() {
        val endTimeButton = binding.disciplineFormFragmentEndTime
        endTimeButton.setOnClickListener {
            clearFocusTextFields()
            val picker =
                timePicker(
                    hour = finalHour,
                    minute = finalMinute,
                    message = resources.getString(R.string.discipline_form_fragment_title_timePicker_end)
                )
            picker.show(childFragmentManager, TAG_TIME_PICKER)
            picker.addOnPositiveButtonClickListener {
                model.setFinalHour(picker.hour, picker.minute)
            }
        }
    }

    private fun timePicker(hour: Int?, minute: Int?, message: String): MaterialTimePicker {
        val isSystem24Hour = is24HourFormat(context)
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        return MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(hour ?: 12)
            .setMinute(minute ?: 0)
            .setTitleText(message)
            .build()
    }

    private fun fabInsetImage() {
        binding.disciplineFormFabImg.setOnClickListener {
            clearFocusTextFields()
            context?.let {
                DisciplineFormDialog(it)
                    .show(url) { url ->
                        binding.disciplineFormFragmentImageView.tryLoadImage(url)
                        model.setImg(url)
                    }
            }
        }
    }

    private fun clearFocusTextFields() {
        binding.disciplineFormFragmentTextfieldName.editText?.clearFocus()
        binding.disciplineFormFragmentTextfieldDescription.editText?.clearFocus()
    }

    private fun saveTextFieldsValue() {
        val textFieldName = binding.disciplineFormFragmentTextfieldName.editText
        val textFieldDescription = binding.disciplineFormFragmentTextfieldDescription.editText


        textFieldName?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val name = textFieldName?.text.toString()
                model.setName(name)
            }
        }

        textFieldDescription?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val description = textFieldDescription?.text.toString()
                model.setDescription(description)
            }
        }
    }

    private fun doneButton() {
        val doneButton = binding.disciplineFormFabDone
        doneButton.setOnClickListener {
            clearFocusTextFields()
            cleanErrorField()
            val name = binding.disciplineFormFragmentTextfieldName.editText?.text.toString()
            val isValid = validate(name)
            if (isValid) {
                alertDialog()

            }
        }
    }

    private fun validate(name: String): Boolean {
        var valid = true
        if (name.isBlank()) {
            val textFieldName = binding.disciplineFormFragmentTextfieldName
            textFieldName.error =
                resources.getString(R.string.discipline_form_fragment_text_field_name_error)
            textFieldName.requestFocus()
            valid = false
        }
        return valid
    }

    private fun cleanErrorField() {
        binding.disciplineFormFragmentTextfieldName.error = null
    }

    private fun createDiscipline(): Discipline {
        val name = binding.disciplineFormFragmentTextfieldName.editText?.text.toString()
        val description =
            binding.disciplineFormFragmentTextfieldDescription.editText?.text.toString()

        val url = url
        return disciplineId?.let { id ->
            Discipline(
                id = id,
                name = name,
                description = description,
                initialHourt = initialHour,
                initialMinute = initialMinute,
                finalHour = finalHour,
                finalMinute = finalMinute,
                img = url,
                favorite = favorite
            )
        } ?: Discipline(
            name = name,
            description = description,
            initialHourt = initialHour,
            initialMinute = initialMinute,
            finalHour = finalHour,
            finalMinute = finalMinute,
            img = url,
            favorite = favorite
        )
    }

    private fun insert(discipline: Discipline) {
        lifecycleScope.launch {
            model.insert(discipline)
        }
    }

    private fun alertDialog() {
        context?.let { context ->
            MaterialAlertDialogBuilder(context)
                .setTitle(resources.getString(R.string.discipline_form_confirm_dialog_title))
                .setMessage(resources.getString(R.string.discipline_form_confirm_dialog_message))
                .setNeutralButton(resources.getString(R.string.common_cancel)) { _, _ ->

                }
                .setNegativeButton(resources.getString(R.string.common_decline)) { _, _ ->

                }
                .setPositiveButton(resources.getString(R.string.common_confirm)) { _, _ ->
                    val discipline = createDiscipline()
                    insert(discipline)
                    controller.navigate(R.id.action_disciplineFormFragment_to_disciplinesFragment)
                }
                .show()
        }
    }

    private fun calendarButton() {
        binding.disciplineFormFabCalendar.setOnClickListener {
            clearFocusTextFields()

            val constraintsBuilder =
                CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointForward.now())

            val picker =
                MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText(resources.getString(R.string.discipline_form_fragment_title_dataPicker))
                    .setSelection(
                        Pair(
                            MaterialDatePicker.todayInUtcMilliseconds(),
                            MaterialDatePicker.todayInUtcMilliseconds()
                        )
                    )
                    .setCalendarConstraints(constraintsBuilder.build())

                    .build()



            picker.show(childFragmentManager, TAG_DATA_PICKER)
            picker.addOnPositiveButtonClickListener {
                Log.i("TAG", "calendarButton: ${picker.selection}")
            }
            picker.addOnNegativeButtonClickListener {
                // Respond to negative button click.
            }
            picker.addOnCancelListener {
                // Respond to cancel button click.
            }
            picker.addOnDismissListener {
                // Respond to dismiss events.
            }


        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
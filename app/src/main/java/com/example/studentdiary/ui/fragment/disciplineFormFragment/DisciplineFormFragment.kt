package com.example.studentdiary.ui.fragment.disciplineFormFragment

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.text.format.DateFormat.is24HourFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.util.Pair
import androidx.core.util.component1
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.studentdiary.R
import com.example.studentdiary.databinding.FragmentDisciplineFormBinding
import com.example.studentdiary.extensions.snackBar
import com.example.studentdiary.extensions.tryLoadImage
import com.example.studentdiary.ui.TAG_DATA_PICKER
import com.example.studentdiary.ui.TAG_TIME_PICKER
import com.example.studentdiary.ui.dialog.DisciplineFormDialog
import com.example.studentdiary.utils.concatenateDateValues
import com.example.studentdiary.utils.concatenateTimeValues
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDisciplineFormBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkBoxFavorite()
        startTimeButton()
        endTimeButton()
        fabInsetImage()
        saveTextFieldsValue()
        doneButton()
        calendarButton()
    }

    override fun onStart() {
        super.onStart()
        updateUi()
        searchDisciplineId()
    }

    private fun checkBoxFavorite() {
        binding.disciplineFormCheckBox.setOnClickListener {
            if (it is CheckBox) model.setFavorite(it.isChecked)
        }
    }

    private fun fabInsetImage() {
        binding.disciplineFormFabImg.setOnClickListener {
            clearFocusTextFields()
            context?.let {
                DisciplineFormDialog(it)
                    .show(model.getImg()) { url ->
                        binding.disciplineFormFragmentImageView.tryLoadImage(url)
                        model.setImg(url)
                    }
            }
        }
    }

    private fun saveTextFieldsValue() {
        val textFieldName = binding.disciplineFormFragmentTextfieldName.editText
        val textFieldDescription =
            binding.disciplineFormFragmentTextfieldDescription.editText


        textFieldName?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                model.setName(textFieldName?.text.toString())
            }
        }

        textFieldDescription?.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    model.setDescription(textFieldDescription?.text.toString())
                }
            }
    }

    private fun clearFocusTextFields() {
        binding.disciplineFormFragmentTextfieldName.editText?.clearFocus()
        binding.disciplineFormFragmentTextfieldDescription.editText?.clearFocus()
    }

    private fun startTimeButton() {
        val startTimeButton = binding.disciplineFormFragmentStartTime
        startTimeButton.setOnClickListener {
            clearFocusTextFields()
            val picker =
                timePicker(
                    time = model.getStartTime(),
                    message = getString(R.string.discipline_form_fragment_title_timePicker_start)
                )
            picker.show(childFragmentManager, TAG_TIME_PICKER)
            picker.addOnPositiveButtonClickListener {
                model.setStartTime(picker.hour, picker.minute)
            }
        }
    }

    private fun endTimeButton() {
        val endTimeButton = binding.disciplineFormFragmentEndTime
        endTimeButton.setOnClickListener {
            clearFocusTextFields()
            val picker =
                timePicker(
                    time = model.getEndTime(),
                    message = getString(R.string.discipline_form_fragment_title_timePicker_end)
                )
            picker.show(childFragmentManager, TAG_TIME_PICKER)
            picker.addOnPositiveButtonClickListener {
                model.setEndTime(picker.hour, picker.minute)
            }
        }
    }

    private fun timePicker(time: kotlin.Pair<Int, Int>?, message: String): MaterialTimePicker {
        val isSystem24Hour = is24HourFormat(context)
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        return MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(time?.component1() ?: 12)
            .setMinute(time?.component2() ?: 0)
            .setTitleText(message)
            .build()
    }

    private fun calendarButton() {
        binding.disciplineFormFabCalendar.setOnClickListener {
            clearFocusTextFields()

            val constraintsBuilder =
                CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointForward.now())

            val picker =
                MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText(getString(R.string.discipline_form_fragment_title_dataPicker))
                    .setSelection(
                        model.getDate() ?: Pair(
                            MaterialDatePicker.todayInUtcMilliseconds(),
                            MaterialDatePicker.todayInUtcMilliseconds()
                        )
                    )
                    .setCalendarConstraints(constraintsBuilder.build())
                    .build()

            picker.show(childFragmentManager, TAG_DATA_PICKER)
            picker.addOnPositiveButtonClickListener {
                picker.selection?.let {
                    model.setDate(it)
                }
            }
        }
    }

    private fun doneButton() {
        val doneButton = binding.disciplineFormFabDone
        doneButton.setOnClickListener {
            clearFocusTextFields()
            cleanErrorField()
            val isValid = validate()
            if (isValid) {
                alertDialog()
            }
        }
    }

    private fun cleanErrorField() {
        binding.disciplineFormFragmentTextfieldName.error = null
    }

    private fun validate(): Boolean {
        var valid = true
        val name = binding.disciplineFormFragmentTextfieldName.editText?.text.toString()
        if (name.isBlank()) {
            val textFieldName = binding.disciplineFormFragmentTextfieldName
            textFieldName.error =
                getString(R.string.discipline_form_fragment_text_field_name_error)
            textFieldName.requestFocus()
            valid = false
        }


        if (model.getDate() == null) {
            valid = false
            snackBar(getString(R.string.discipline_form_fragment_snackBarMessage_emptyDate))
        }
        return valid
    }

    private fun alertDialog() {
        context?.let { context ->
            MaterialAlertDialogBuilder(context)
                .setTitle(getString(R.string.discipline_form_confirm_dialog_title))
                .setMessage(getString(R.string.discipline_form_confirm_dialog_message))
                .setNeutralButton(getString(R.string.common_cancel)) { _, _ ->

                }
                .setNegativeButton(getString(R.string.common_decline)) { _, _ ->

                }
                .setPositiveButton(getString(R.string.common_confirm)) { _, _ ->
                    insert()
//                  adicionarLembreteNoCalendario()
                    controller.navigate(R.id.action_disciplineFormFragment_to_disciplinesFragment)
                }
                .show()
        }
    }

    private fun insert() = model.insert()

    private fun searchDisciplineId() {
        lifecycleScope.launch {
            disciplineId?.let {
                model.getDiscipline() ?: model.searchDisciplineForId(it)
            } ?: kotlin.run {
                model.getDiscipline() ?: model.setDiscipline()
            }
        }
    }

    private fun updateUi() {
        model.discipline.observe(viewLifecycleOwner) { discipline ->
            discipline?.let {

                discipline.favorite.let { favorite ->
                    if (binding.disciplineFormCheckBox.isChecked != favorite) {
                        binding.disciplineFormCheckBox.isChecked = favorite
                    }
                }

                discipline.startTime?.let { startTime ->
                    binding.disciplineFormFragmentStartTime.text =
                        concatenateTimeValues(startTime)

                }

                discipline.endTime?.let { endTime ->
                    binding.disciplineFormFragmentEndTime.text =
                        concatenateTimeValues(endTime)
                }

                discipline.date?.let { date ->
                    binding.disciplineFormFabCalendar.text = concatenateDateValues(date)
                }

                discipline.name?.let { name ->
                    val textInputLayoutName = binding.disciplineFormFragmentTextfieldName
                    textInputLayoutName.editText?.setText(name)
                    textInputLayoutName.editText?.setSelection(name.length)
                }

                discipline.description?.let { description ->
                    val textInputLayoutDescription =
                        binding.disciplineFormFragmentTextfieldDescription
                    textInputLayoutDescription.editText?.setText(description)
                    textInputLayoutDescription.editText?.setSelection(description.length)
                }

                discipline.img?.let { url ->
                    binding.disciplineFormFragmentImageView.tryLoadImage(url)
                }
            }
        }
    }

    fun adicionarLembreteNoCalendario() {


        model.getDate()?.let {

            val calendario = Calendar.getInstance().apply {
                timeInMillis = it.component1()

            }
            val ano = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)


            val startMillis: Long = Calendar.getInstance().run {
                set(ano, mes, dia, 7, 30)
                timeInMillis
            }
            val endMillis: Long = Calendar.getInstance().run {
                set(ano, mes, dia, 7, 30)
                timeInMillis
            }
            val intent = Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
                .putExtra(CalendarContract.Events.TITLE, "Yoga")
                .putExtra(CalendarContract.Events.DESCRIPTION, "Group class")
                .putExtra(CalendarContract.Events.EVENT_LOCATION, "The gym")
                .putExtra(
                    CalendarContract.Events.AVAILABILITY,
                    CalendarContract.Events.AVAILABILITY_BUSY
                )
                .putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com")
            startActivity(intent)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
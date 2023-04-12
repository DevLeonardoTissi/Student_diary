package com.example.studentdiary.ui.fragment.disciplineFormFragment

import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.util.Pair
import androidx.core.util.component1
import androidx.core.util.component2
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.studentdiary.R
import com.example.studentdiary.databinding.FragmentDisciplineFormBinding
import com.example.studentdiary.extensions.snackBar
import com.example.studentdiary.extensions.tryLoadImage
import com.example.studentdiary.ui.*
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

     private var calID: Long = 0


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
        switchAddReminder()
        Log.i("TAG", "onViewCreated: ${model.getEventId()}")
    }

    override fun onStart() {
        super.onStart()
        updateUi()
        searchDisciplineId()
    }


    private fun checkBoxFavorite() {
        binding.disciplineFormFragmentCheckBox.setOnClickListener {
            if (it is CheckBox) model.setFavorite(it.isChecked)
        }
    }

    private fun fabInsetImage() {
        binding.disciplineFormFragmentFabImg.setOnClickListener {
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

    private fun switchAddReminder() {
        val switchAddReminder = binding.disciplineFormFragmentSwitchAddReminder

        binding.disciplineFormFragmentTextfieldEmail.isEnabled = switchAddReminder.isChecked

        switchAddReminder.setOnCheckedChangeListener { _, isChecked ->
            binding.disciplineFormFragmentTextfieldEmail.isEnabled = isChecked
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
                alertDialogConfirm()
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

        val email = binding.disciplineFormFragmentTextfieldEmail.editText?.text.toString()
        if (email.isBlank()) {
            val textFieldEmail = binding.disciplineFormFragmentTextfieldEmail
            textFieldEmail.error =
                getString(R.string.discipline_form_fragment_text_field_email_error)
            if (name.isNotBlank()) {
                textFieldEmail.requestFocus()
            }
            valid = false
        }

        if (model.getDate() == null) {
            valid = false
            snackBar(getString(R.string.discipline_form_fragment_snackBarMessage_emptyDate))
        }
        return valid
    }

    private fun alertDialogConfirm() {
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
                    val switchAddReminder = binding.disciplineFormFragmentSwitchAddReminder
                    if (switchAddReminder.isChecked) {
                        val email =
                            binding.disciplineFormFragmentTextfieldEmail.editText?.text.toString().trim()
                        addReminder(email)
                    }
//                    controller.navigate(R.id.action_disciplineFormFragment_to_disciplinesFragment)
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
                    if (binding.disciplineFormFragmentCheckBox.isChecked != favorite) {
                        binding.disciplineFormFragmentCheckBox.isChecked = favorite
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

    private fun addReminder(email: String) {
        model.getDate()?.let { date ->

            val startMillis: Long = Calendar.getInstance().run {
                val (year, month, day) = converterLongToDate(date.component1())
                model.getStartTime()?.let {
                    set(year, month, day, it.component1(), it.component2())
                    timeInMillis
                }
                set(year, month, day)
                timeInMillis
            }

            val endMillis: Long = Calendar.getInstance().run {
                val (year, month, day) = converterLongToDate(date.component2())
                model.getEndTime()?.let {
                    set(year, month, day, it.component1(), it.component2())
                    timeInMillis
                } ?: set(year, month, day)
                timeInMillis

            }

            model.getEventId()?.let {
                addedit(startMillis, endMillis, it)
            } ?: addeventooo(startMillis, endMillis, email)

        }
    }

    private fun addedit(startMillis: Long, endMillis: Long, eventId:Long) {

//        val urii: Uri = CalendarContract.Calendars.CONTENT_URI
//        val selection: String = "((${CalendarContract.Calendars.ACCOUNT_NAME} = ?) AND (" +
//                "${CalendarContract.Calendars.ACCOUNT_TYPE} = ?) AND (" +
//                "${CalendarContract.Calendars.OWNER_ACCOUNT} = ?))"
//        val selectionArgs: Array<String> =
//            arrayOf("leonardo.tissi.si@gmail.com", GOOGLE_EMAIL_TYPE, "leonardo.tissi.si@gmail.com")
//        Log.i("TAG", "addedit: $eventId")
//
//
//        activity?.let {
//            val cut: Cursor? =
//                it.contentResolver.query(
//                    urii,
//                    EVENT_PROJECTION,
//                    selection,
//                    selectionArgs,
//                    null
//                )
//
//            cut?.let { cursor ->
//                while (cursor.moveToNext()) {
//                    calID = cursor.getLong(PROJECTION_ID_INDEX)
//                }
//                cut.close()
//            }
//        }

        val values = ContentValues().apply {
//            put(CalendarContract.Events.DTSTART, startMillis)
//            put(CalendarContract.Events.DTEND, endMillis)
            put(CalendarContract.Events.TITLE, model.getName())
//            put(CalendarContract.Events.DESCRIPTION, model.getDescription())
//            put(CalendarContract.Events.CALENDAR_ID, calID)
//            put(CalendarContract.Events.EVENT_TIMEZONE, TIME_ZONE_ID)
        }

        activity?.let {activity ->
        val updateUri: Uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId)

         activity.contentResolver.update(updateUri, values, null, null)
            Log.i("TAG", "addedit: $updateUri")

        }
    }

    private fun addeventooo(startMillis: Long, endMillis: Long, email: String) {

        val urii: Uri = CalendarContract.Calendars.CONTENT_URI
        val selection: String = "((${CalendarContract.Calendars.ACCOUNT_NAME} = ?) AND (" +
                "${CalendarContract.Calendars.ACCOUNT_TYPE} = ?) AND (" +
                "${CalendarContract.Calendars.OWNER_ACCOUNT} = ?))"
        val selectionArgs: Array<String> =
            arrayOf(email, GOOGLE_EMAIL_TYPE, email)


        activity?.let {
            val cut: Cursor? =
                it.contentResolver.query(
                    urii,
                    EVENT_PROJECTION,
                    selection,
                    selectionArgs,
                    null
                )

            cut?.let { cursor ->
                while (cursor.moveToNext()) {
                    calID = cursor.getLong(PROJECTION_ID_INDEX)
                }
                cut.close()
            }

            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, startMillis)
                put(CalendarContract.Events.DTEND, endMillis)
                put(CalendarContract.Events.TITLE, model.getName())
                put(CalendarContract.Events.DESCRIPTION, model.getDescription())
                put(CalendarContract.Events.CALENDAR_ID, calID)
                put(CalendarContract.Events.EVENT_TIMEZONE, TIME_ZONE_ID)
            }

            val contentResolver = it.contentResolver
            val uri: Uri? = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)

            uri?.lastPathSegment?.let {id ->
                val eventID: Long = id.toLong()
                model.setEventId(eventID)
                Log.i("TAG", "addeventooo: $eventID")
            }
        }
    }

    private fun converterLongToDate(time: Long): Triple<Int, Int, Int> {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = time
            timeZone = TimeZone.getTimeZone(TIME_ZONE_ID)
        }

        val ano = calendar.get(Calendar.YEAR)
        val mes = calendar.get(Calendar.MONTH)
        val dia = calendar.get(Calendar.DAY_OF_MONTH)
        return Triple(ano, mes, dia)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
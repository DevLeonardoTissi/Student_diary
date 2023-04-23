package com.example.studentdiary.ui.fragment.disciplineFormFragment

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.text.format.DateFormat.is24HourFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.core.util.component1
import androidx.core.util.component2
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.studentdiary.R
import com.example.studentdiary.databinding.FragmentDisciplineFormBinding
import com.example.studentdiary.extensions.snackBar
import com.example.studentdiary.extensions.tryLoadImage
import com.example.studentdiary.ui.AppViewModel
import com.example.studentdiary.ui.EVENT_PROJECTION
import com.example.studentdiary.ui.NavigationComponents
import com.example.studentdiary.ui.PROJECTION_ID_INDEX
import com.example.studentdiary.ui.TAG_DATA_PICKER
import com.example.studentdiary.ui.TAG_TIME_PICKER
import com.example.studentdiary.ui.TIME_ZONE_ID
import com.example.studentdiary.ui.dialog.DisciplineFormDialog
import com.example.studentdiary.ui.fragment.baseFragment.BaseFragment
import com.example.studentdiary.utils.EmailType
import com.example.studentdiary.utils.concatenateDateValues
import com.example.studentdiary.utils.concatenateTimeValues
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar
import java.util.TimeZone

class DisciplineFormFragment : BaseFragment() {

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
    private val appViewModel: AppViewModel by activityViewModel()

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                binding.disciplineFormFragmentSwitchAddReminder.isChecked = true
            }
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
        setupNavigationComponents()
        setupFavoriteCheckbox()
        setupCompleteCheckbox()
        fabInsetImage()
        switchAddReminder()
        configureAndSaveTextFields()
        startTimeButton()
        endTimeButton()
        calendarButton()
        doneButton()
    }

    override fun onStart() {
        super.onStart()
        updateUi()
        searchDisciplineId()
    }

    private fun setupNavigationComponents() {
        appViewModel.hasNavigationComponents = NavigationComponents(navigationIcon = true, menuDrawer = true)
    }


    private fun setupFavoriteCheckbox() {
        binding.disciplineFormFragmentCheckBoxFavorite.setOnClickListener {
            if (it is CheckBox) model.setFavorite(it.isChecked)
        }
    }

    private fun setupCompleteCheckbox() {
        binding.disciplineFormFragmentCheckBoxCompleted.setOnClickListener {
            if (it is CheckBox) model.setCompleted(it.isChecked)
        }
    }

    private fun fabInsetImage() {
        binding.disciplineFormFragmentFabImg.setOnClickListener {
            clearFocusTextFields()
            context?.let {
                DisciplineFormDialog(it)
                    .show(model.getImg()) { url ->
                        model.setImg(url)
                    }
            }
        }
    }

    private fun switchAddReminder() {
        val switchAddReminder = binding.disciplineFormFragmentSwitchAddReminder
        val textInputEmail = binding.disciplineFormFragmentTextfieldEmail
        val textInputEmailType = binding.disciplineFormFragmentTextfieldEmailType
        switchAddReminder.isChecked = false
        textInputEmail.isEnabled = switchAddReminder.isChecked
        textInputEmailType.isEnabled = switchAddReminder.isChecked

        switchAddReminder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkCalendarPermission { isGranted ->
                    if (isGranted) {
                        textInputEmailType.isEnabled = true
                        textInputEmail.isEnabled = true
                    } else {
                        textInputEmail.isEnabled = false
                        switchAddReminder.isChecked = false
                        textInputEmailType.isEnabled = false
                    }
                }
            } else {
                textInputEmail.isEnabled = false
                textInputEmailType.isEnabled = false
            }
        }
    }

    private fun checkCalendarPermission(granted: (isGranted: Boolean) -> Unit) {

        context?.let {

            val permission = Manifest.permission.READ_CALENDAR
            if (ContextCompat.checkSelfPermission(
                    it,
                    permission
                ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.WRITE_CALENDAR
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                granted(true)
            } else {
                granted(false)
                requestPermission.launch(permission)
                requestPermission.launch(Manifest.permission.WRITE_CALENDAR)
            }
        }
    }

    private fun configureAndSaveTextFields() {
        val textFieldName = binding.disciplineFormFragmentTextfieldName.editText
        val textFieldDescription =
            binding.disciplineFormFragmentTextfieldDescription.editText
        val textFieldEmail = binding.disciplineFormFragmentTextfieldEmail.editText
        val textFieldEmailType = binding.disciplineFormFragmentTextfieldEmailType.editText


        val adapter = context?.let {
            ArrayAdapter(
                it, R.layout.dropdown_menu_item,
                enumValues<EmailType>()
            )
        }


        (textFieldEmailType as? MaterialAutoCompleteTextView)?.setAdapter(adapter)
        (textFieldEmailType as? MaterialAutoCompleteTextView)?.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                adapter?.getItem(position)?.let { model.setUserEmailType(it.getEmailType()) }
            }


        textFieldEmail?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (textFieldEmail?.isEnabled == true && textFieldEmail.text.toString().isNotBlank())  {
                    model.setUserCalendarEmail(textFieldEmail.text.toString())
                }
            }
        }

        textFieldName?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && textFieldName?.text.toString().isNotBlank()) {
                    model.setName(textFieldName?.text.toString())
            }
        }

        textFieldDescription?.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && textFieldDescription?.text.toString().isNotBlank()) {
                    model.setDescription(textFieldDescription?.text.toString())
                }
            }
    }


    private fun clearFocusTextFields() {
        binding.disciplineFormFragmentTextfieldName.editText?.clearFocus()
        binding.disciplineFormFragmentTextfieldDescription.editText?.clearFocus()
        binding.disciplineFormFragmentTextfieldEmail.editText?.clearFocus()
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
            clearErrorField()
            val isValid = validate()
            if (isValid) {
                alertDialogConfirm()
            }
        }
    }

    private fun clearErrorField() {
        binding.disciplineFormFragmentTextfieldName.error = null
        binding.disciplineFormFragmentTextfieldEmail.error = null
        binding.disciplineFormFragmentTextfieldEmailType.error = null
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


        val textFieldEmail = binding.disciplineFormFragmentTextfieldEmail
        if (textFieldEmail.isEnabled) {
            val email = textFieldEmail.editText?.text.toString()
            if (email.isBlank()) {
                textFieldEmail.error =
                    getString(R.string.discipline_form_fragment_text_field_email_error)
                valid = false
                if (name.isNotBlank()) {
                    textFieldEmail.requestFocus()
                }
            }
        }


        val textFieldEmailType = binding.disciplineFormFragmentTextfieldEmailType
        if (textFieldEmailType.isEnabled) {
            val emailType = textFieldEmailType.editText?.text.toString()
            if (emailType.isBlank()) {
                textFieldEmailType.error =
                    getString(R.string.discipline_form_fragment_text_field_email_type_error)
                valid = false
            }
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
                    val switchAddReminder = binding.disciplineFormFragmentSwitchAddReminder
                    if (switchAddReminder.isChecked) {
                        addReminder()
                    }
                    insert()
                    controller.navigate(R.id.action_disciplineFormFragment_to_disciplinesFragment)
                }
                .show()
        }
    }

    private fun addReminder() {
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

            model.getEventId()?.let { eventId ->
                editCalendarEvent(startMillis, endMillis, eventId)
            } ?: createEventInCalendar(startMillis, endMillis)

        }
    }

    private fun converterLongToDate(time: Long): Triple<Int, Int, Int> {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = time
            timeZone = TimeZone.getTimeZone(TIME_ZONE_ID)
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return Triple(year, month, day)
    }

    private fun createEventInCalendar(startMillis: Long, endMillis: Long) {
        lifecycleScope.launch {
            context?.let { context ->
                var calID: Long = 0
                val email = model.getUserCalendarEmail()
                email?.let { userEmail ->
                    val contentUri: Uri = CalendarContract.Calendars.CONTENT_URI
                    val selection: String =
                        "((${CalendarContract.Calendars.ACCOUNT_NAME} = ?) AND (" +
                                "${CalendarContract.Calendars.ACCOUNT_TYPE} = ?) AND (" +
                                "${CalendarContract.Calendars.OWNER_ACCOUNT} = ?))"


                    model.getUserEmailType()?.let { emailType ->
                        val selectionArgs: Array<String> =
                            arrayOf(userEmail, emailType, userEmail)

                        val cursor: Cursor? =
                            context.contentResolver.query(
                                contentUri,
                                EVENT_PROJECTION,
                                selection,
                                selectionArgs,
                                null
                            )

                        cursor?.let {
                            while (it.moveToNext()) {
                                calID = it.getLong(PROJECTION_ID_INDEX)
                            }
                            cursor.close()
                        }

                        val values = ContentValues().apply {
                            put(CalendarContract.Events.DTSTART, startMillis)
                            put(CalendarContract.Events.DTEND, endMillis)
                            put(CalendarContract.Events.TITLE, model.getName())
                            put(CalendarContract.Events.DESCRIPTION, model.getDescription())
                            put(CalendarContract.Events.CALENDAR_ID, calID)
                            put(CalendarContract.Events.EVENT_TIMEZONE, TIME_ZONE_ID)
                        }

                        val contentResolver = context.contentResolver
                        val uri: Uri? =
                            contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)

                        uri?.lastPathSegment?.let { id ->
                            val eventID: Long = id.toLong()
                            model.setEventId(eventID)
                        }
                    }
                }
            }
        }
    }

    private fun editCalendarEvent(startMillis: Long, endMillis: Long, eventId: Long) {
        lifecycleScope.launch {
            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, startMillis)
                put(CalendarContract.Events.DTEND, endMillis)
                put(CalendarContract.Events.TITLE, model.getName())
                put(CalendarContract.Events.DESCRIPTION, model.getDescription())
            }
            context?.let { context ->
                val updateUri: Uri =
                    ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId)

                val projection = arrayOf(CalendarContract.Events._ID)
                val selection = "${CalendarContract.Events._ID} = ?"
                val selectionArgs = arrayOf(eventId.toString())

                val cursor = context.contentResolver.query(
                    updateUri,
                    projection,
                    selection,
                    selectionArgs,
                    null
                )
                val eventExists = (cursor?.count ?: 0) > 0
                cursor?.close()

                if (eventExists) {
                    context.contentResolver.update(updateUri, values, null, null)
                } else {
                    createEventInCalendar(startMillis, endMillis)
                }
            }
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
        model.discipline.observe(this@DisciplineFormFragment) { discipline ->
            discipline?.let {

                discipline.userEmailType?.let { userEmailType ->

                    val value = enumValues<EmailType>().find { it.getEmailType() == userEmailType }
                    value?.let {
                        val position = enumValues<EmailType>().indexOf(value)
                        val autoCompleteTextView =
                            binding.disciplineFormFragmentTextfieldEmailType.editText
                        (autoCompleteTextView as? MaterialAutoCompleteTextView)?.setText(
                            autoCompleteTextView.adapter.getItem(position).toString(),
                            false
                        )
                    }
                }


                discipline.favorite.let { favorite ->
                    if (binding.disciplineFormFragmentCheckBoxFavorite.isChecked != favorite) {
                        binding.disciplineFormFragmentCheckBoxFavorite.isChecked = favorite
                    }
                }

                discipline.completed.let { completed ->
                    if (binding.disciplineFormFragmentCheckBoxCompleted.isChecked != completed) {
                        binding.disciplineFormFragmentCheckBoxCompleted.isChecked = completed
                    }
                }

                discipline.userCalendarEmail?.let { userCalendarEmail ->
                    val textInputLayoutEmail = binding.disciplineFormFragmentTextfieldEmail
                    textInputLayoutEmail.editText?.setText(userCalendarEmail)
                    textInputLayoutEmail.editText?.setSelection(userCalendarEmail.length)
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

    override fun onDestroy() {
        super.onDestroy()
        model.discipline.removeObservers(this@DisciplineFormFragment)
        _binding = null
    }
}
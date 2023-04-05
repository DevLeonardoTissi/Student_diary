package com.example.studentdiary.ui.fragment.disciplineFormFragment

import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.studentdiary.R
import com.example.studentdiary.databinding.FragmentDisciplineFormBinding
import com.example.studentdiary.extensions.tryLoadImage
import com.example.studentdiary.model.Discipline
import com.example.studentdiary.ui.dialog.DisciplineFormDialog
import com.example.studentdiary.utils.formatTime
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
        doneButton()
    }

    private fun searchDisciplineId() {
        lifecycleScope.launch {
            disciplineId?.let {
                searchData(it)
            }
        }
    }

    private suspend fun searchData(it: String) {
        val discipline = model.searDisciplideForId(it)
        initialMinute = discipline.initialMinute
        initialHour = discipline.initialHourt
        finalHour = discipline.finalHour
        finalMinute = discipline.finalMinute
        favorite = discipline.favorite
        binding.disciplineFormFragmentTextfieldName.editText?.setText(discipline.name)
        binding.disciplineFormFragmentTextfieldDescription.editText?.setText(discipline.description)
        binding.disciplineFormCheckBox.isChecked = favorite
        discipline.img?.let { url ->
            binding.disciplineFormFragmentImageView.tryLoadImage(url)
            this@DisciplineFormFragment.url = url
        }
        binding.disciplineFormFragmentStartTime.text = formatTime(initialHour, initialMinute)
        binding.disciplineFormFragmentEndTime.text = formatTime(finalHour, finalMinute)
    }

    private fun checkBoxFavorite() {
        binding.disciplineFormCheckBox.setOnCheckedChangeListener { _, isChecked ->
            favorite = isChecked
        }
    }

    private fun startTimeButton() {
        val startTimeButton = binding.disciplineFormFragmentStartTime
        startTimeButton.setOnClickListener {
            context?.let { context ->
                val picker =
                    timePicker(
                        hour = initialHour,
                        minute = initialMinute,
                        message = context.getString(R.string.discipline_form_fragment_title_timePicker_start)
                    )
                picker.show(childFragmentManager, "")
                picker.addOnPositiveButtonClickListener {
                    initialHour = picker.hour
                    initialMinute = picker.minute
                    startTimeButton.text = formatTime(initialHour, initialMinute)
                }
            }
        }
    }

    private fun fabInsetImage() {
        binding.disciplineFormFabImg.setOnClickListener {
            context?.let {
                DisciplineFormDialog(it)
                    .show(url) { url ->
                        binding.disciplineFormFragmentImageView.tryLoadImage(url)
                        this.url = url
                    }
            }
        }
    }

    private fun endTimeButton() {
        val endTimeButton = binding.disciplineFormFragmentEndTime
        endTimeButton.setOnClickListener {
            context?.let { context ->
                val picker =
                    timePicker(
                        hour = finalHour,
                        minute = finalMinute,
                        message = context.getString(R.string.discipline_form_fragment_title_timePicker_end)
                    )
                picker.show(childFragmentManager, "")
                picker.addOnPositiveButtonClickListener {
                    finalHour = picker.hour
                    finalMinute = picker.minute
                    endTimeButton.text = formatTime(finalHour, finalMinute)
                }
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

    private fun doneButton() {
        val doneButton = binding.disciplineFormFabDone
        doneButton.setOnClickListener {
            cleanField()
            val name = binding.disciplineFormFragmentTextfieldName.editText?.text.toString()
            val isvalid = validate(name)
            if (isvalid) {
                val discipline = createDiscipline()
                insert(discipline)
                controller.popBackStack()
            }
        }
    }

    private fun validate(name: String): Boolean {
        var valid = true
        if (name.isBlank()) {
            binding.disciplineFormFragmentTextfieldName.error =
                context?.getString(R.string.discipline_form_fragment_text_field_name_error)
            valid = false
        }
        return valid
    }

    private fun cleanField() {
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}
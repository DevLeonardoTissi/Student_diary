package com.example.studentdiary.ui.fragment.pomodoroFragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.studentdiary.R
import com.example.studentdiary.databinding.FragmentPomodoroBinding
import com.example.studentdiary.extensions.formatTimeLeft
import com.example.studentdiary.ui.dialog.SelectPomodoroCyclesBottomSheet
import com.example.studentdiary.ui.fragment.baseFragment.BaseFragment
import com.example.studentdiary.utils.services.PomodoroService
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.text.NumberFormat

class PomodoroFragment : BaseFragment() {

    private var _binding: FragmentPomodoroBinding? = null
    private val binding get() = _binding!!

    private val model: PomodoroViewModel by activityViewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPomodoroBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClickStartButton()
        onClickPauseButton()
        onClickStopButton()
        setupObserverPomodoroTimeLeft()
        setupObserverIsRunning()
        setupObserverSliderTimer()
        setupObserverSliderInterval()
        setupObserverSliderExtraInterval()
        setupSliders()
        setupObserverPomodoroCycles()
        observerIntervalTimeLeft()
        observerExtraIntervalTimeLeft()
    }

    private fun setupObserverPomodoroCycles() {
        model.pomodoroCycles.observe(viewLifecycleOwner) {
            binding.pomodoroFragmentButtonSelectCycles.text = String.format(
                "%s %d",
                getString(R.string.pomodoro_fragment_label_select_cycles_button_concatenated_text),
                it
            )
            setupButtonSelectCycles(it)
        }
    }

    private fun setupButtonSelectCycles(cycles: Int) {
        binding.pomodoroFragmentButtonSelectCycles.setOnClickListener {
            context?.let { context ->
                SelectPomodoroCyclesBottomSheet(context).show(cycles) {
                    model.setPomodoroCycles(it)
                }
            }

        }
    }


    private fun setupObserverSliderTimer() {
        model.pomodoroStartTime.observe(viewLifecycleOwner) {
//            val progress = it.toFloat() / 1000 / 60
//            binding.pomodoroFragmentSliderTimer.setValues(progress)
            binding.pomodoroFragmentTextViewSelectPomodoroTimerLabel.text = String.format(
                "%s\n%s",
                getString(R.string.pomodoro_fragment_textView_select_time_label),
                formatTimeLeft(it)
            )
        }
    }

    private fun setupObserverSliderExtraInterval() {
        model.extraIntervalStartTime.observe(viewLifecycleOwner) {
//            val progress = it.toFloat() / 1000 / 60
//            binding.pomodoroFragmentSliderExtraInterval.setValues(progress)
            binding.pomodoroFragmentTextViewSelectExtraIntervalTimerLabel.text = String.format(
                "%s\n%s",
                getString(R.string.pomodoro_fragment_textView_select_extra_interval_label),
                formatTimeLeft(it)
            )
        }
    }

    private fun setupObserverSliderInterval() {
        model.intervalStartTime.observe(viewLifecycleOwner) {
//            val progress = it.toFloat() / 1000 / 60
//            binding.pomodoroFragmentSliderInterval.setValues(progress)
            binding.pomodoroFragmentTextViewSelectIntervalTimerLabel.text = String.format(
                    "%s\n%s",
                    getString(R.string.pomodoro_fragment_textView_select_interval_label),
                    formatTimeLeft(it)
                )
        }
    }

    private fun observerIntervalTimeLeft() {
//        model.intervalLeftTime.observe(viewLifecycleOwner) { intervalTime ->
//            binding.pomodoroFragmentTextViewInterval.text = String.format(
//                "%s %s",
//                getString(R.string.pomodoro_fragment_textView_interval_timer),
//                formatTimeLeft(intervalTime)
//            )
//        }
    }

    private fun observerExtraIntervalTimeLeft() {
//        model.extraIntervalLeftTime.observe(viewLifecycleOwner) { extraIntervalTime ->
//            binding.pomodoroFragmentTextViewExtraInterval.text = String.format(
//                "%s %s",
//                getString(R.string.pomodoro_fragment_textView_extra_interval_timer),
//                formatTimeLeft(extraIntervalTime)
//            )
//
//        }
    }

    private fun setupSliders() {
        val timerSlider = binding.pomodoroFragmentSliderTimer
        val intervalSlider = binding.pomodoroFragmentSliderInterval
        val extraIntervalSlider = binding.pomodoroFragmentSliderExtraInterval
        timerSlider.addOnChangeListener { _, value, _ ->
            if (value in 5.0..60.0) {
                model.setValuePomodoroStartTime((value * 60000).toLong())
            }

        }

        timerSlider.setLabelFormatter { value: Float ->
            val format = NumberFormat.getInstance()
            format.maximumFractionDigits = 0
            val formattedValue = format.format(value.toDouble())
            "$formattedValue min"
        }

        intervalSlider.addOnChangeListener { _, value, _ ->
            if (value in 5.0..60.0) {
                model.setValueIntervalStartTime((value * 60000).toLong())
            }
        }

        intervalSlider.setLabelFormatter { value: Float ->
            val format = NumberFormat.getInstance()
            format.maximumFractionDigits = 0
            val formattedValue = format.format(value.toDouble())
            "$formattedValue min"
        }

        extraIntervalSlider.addOnChangeListener { _, value, _ ->
            if (value in 15.0..60.0) {
                model.setValueExtraIntervalStartTime((value * 60000).toLong())
            }
        }

        extraIntervalSlider.setLabelFormatter { value: Float ->
            val format = NumberFormat.getInstance()
            format.maximumFractionDigits = 0
            val formattedValue = format.format(value.toDouble())
            "$formattedValue min"
        }


    }

    private fun setupObserverIsRunning() {
        model.timerIsRunning.observe(viewLifecycleOwner) {
            if (it){
                model.pomodoroState.observe(viewLifecycleOwner){pomodoroState->
                    binding.pomodoroFragmentTextViewState.visibility = View.VISIBLE
                    binding.pomodoroFragmentTextViewState.text = pomodoroState.toString()
                }
            }else{
                binding.pomodoroFragmentTextViewState.visibility = View.GONE
            }
            binding.pomodoroFragmentButtonStart.isEnabled = !it
            binding.pomodoroFragmentButtonPause.isEnabled = it
            binding.pomodoroFragmentSliderTimer.isEnabled = !it
            binding.pomodoroFragmentSliderInterval.isEnabled = !it
            binding.pomodoroFragmentSliderExtraInterval.isEnabled = !it
            binding.pomodoroFragmentButtonSelectCycles.isEnabled = !it
            if (it) {
                binding.pomodoroFragmentAnimationChronometer.playAnimation()
            } else {
                binding.pomodoroFragmentAnimationChronometer.cancelAnimation()
            }
        }
    }

    private fun setupObserverPomodoroTimeLeft() {
        model.pomodoroLeftTime.observe(viewLifecycleOwner) { timeLeftInMillis ->
            binding.pomodoroFragmentTextViewTimer.text = String.format(
                "%s %s",
                getString(R.string.pomodoro_fragment_textView_pomodoro_timer),
                formatTimeLeft(timeLeftInMillis)
            )
        }
    }

    private fun onClickStopButton() {
        binding.pomodoroFragmentButtonStop.setOnClickListener {
            val intent = Intent(context, PomodoroService::class.java)
            context?.stopService(intent)

        }
    }

    private fun onClickPauseButton() {
        binding.pomodoroFragmentButtonPause.setOnClickListener {
            model.pauseTimer()
        }
    }

    private fun onClickStartButton() {
        binding.pomodoroFragmentButtonStart.setOnClickListener {
            val intent = Intent(context, PomodoroService::class.java)
            context?.startService(intent)

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
package com.example.studentdiary.ui.fragment.pomodoroFragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.studentdiary.databinding.FragmentPomodoroBinding
import com.example.studentdiary.extensions.formatTimeLeft
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
        setupObserverTimer()
        setupObserverIsRunning()
        setupObserverSliderTimer()
        setupObserverSliderInterval()
        setupSliders()
    }



    private fun setupObserverSliderTimer() {
        model.pomodoroStartTime.observe(viewLifecycleOwner) {
            val progress = it.toFloat() / 1000 / 60
            binding.pomodoroFragmentSliderTimer.setValues(progress)
        }
    }

    private fun setupObserverSliderInterval(){
        model.intervalTime.observe(viewLifecycleOwner){
            val progress = it.toFloat() / 1000 / 60
            binding.pomodoroFragmentSliderInterval.setValues(progress)
            binding.pomodoroFragmentTextViewInterval.text = formatTimeLeft(it)
        }
    }

    private fun setupSliders(){
        val timerSlider = binding.pomodoroFragmentSliderTimer
        val intervalSlider = binding.pomodoroFragmentSliderInterval
        timerSlider.addOnChangeListener { _, value, _ ->
            if (value in 5.0..60.0){
                model.setValuePomodoroTimer((value * 60 * 1000).toLong())
            }

        }

        timerSlider.setLabelFormatter { value: Float ->
            val format = NumberFormat.getInstance()
            format.maximumFractionDigits = 0
            val formattedValue = format.format(value.toDouble())
            "$formattedValue min"
        }

        intervalSlider.addOnChangeListener { _, value, _ ->
            if (value in 5.0..60.0){
                model.setValueIntervalTimer((value * 60 * 1000).toLong())
            }
        }

        intervalSlider.setLabelFormatter { value: Float ->
            val format = NumberFormat.getInstance()
            format.maximumFractionDigits = 0
            val formattedValue = format.format(value.toDouble())
            "$formattedValue min"
        }
    }

    private fun setupObserverIsRunning() {
        model.timerIsRunning.observe(viewLifecycleOwner) {
            updateButtons(it)
            binding.pomodoroFragmentSliderTimer.isEnabled = !it
            binding.pomodoroFragmentSliderInterval.isEnabled = !it
            if (it){
                binding.pomodoroFragmentAnimationChronometer.playAnimation()
            }else{
                binding.pomodoroFragmentAnimationChronometer.cancelAnimation()
            }

        }
    }

    private fun setupObserverTimer() {
        model.timeLeftInMillis.observe(viewLifecycleOwner) { timeLeftInMillis ->
            binding.pomodoroFragmentTextViewTime.text =
                formatTimeLeft(timeLeftInMillis ?: model.getValuePomodoroTimer())
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


    private fun updateButtons(isTimerRunning: Boolean) {
        binding.pomodoroFragmentButtonStart.isEnabled = !isTimerRunning
        binding.pomodoroFragmentButtonPause.isEnabled = isTimerRunning
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
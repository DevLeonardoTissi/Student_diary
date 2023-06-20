package com.example.studentdiary.ui.fragment.pomodoroFragment

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.studentdiary.databinding.FragmentPomodoroBinding
import com.example.studentdiary.ui.fragment.baseFragment.BaseFragment
import com.example.studentdiary.utils.services.PomodoroService
import org.koin.androidx.viewmodel.ext.android.activityViewModel

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
    }

    private fun setupObserverIsRunning() {
        model.timerIsRunning.observe(viewLifecycleOwner) {
            updateButtons(it)
        }
    }

    private fun setupObserverTimer() {
        model.timeLeftInMillis.observe(viewLifecycleOwner) { timeLeftInMillis ->
            timeLeftInMillis?.let {
                updateTimerText(it)
            }?: updateTimerText(model.startTime)
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
            model.pauseTimer(context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        }
    }

    private fun onClickStartButton() {
        binding.pomodoroFragmentButtonStart.setOnClickListener {
            val intent = Intent(context, PomodoroService::class.java)
            context?.startService(intent)

        }
    }


    private fun updateTimerText(timeLeftInMillis: Long) {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        val timeLeftFormatted = String.format("%02d:%02d", minutes, seconds)
        binding.pomodoroFragmentTextViewTime.text = timeLeftFormatted
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
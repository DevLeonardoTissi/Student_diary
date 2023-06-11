package com.example.studentdiary.ui.fragment.pomodoroFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.studentdiary.databinding.FragmentPomodoroBinding
import com.example.studentdiary.ui.fragment.baseFragment.BaseFragment

class PomodoroFragment : BaseFragment() {

    private var _binding: FragmentPomodoroBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPomodoroBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
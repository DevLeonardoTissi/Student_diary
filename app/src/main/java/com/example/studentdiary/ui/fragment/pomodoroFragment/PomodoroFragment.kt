package com.example.studentdiary.ui.fragment.pomodoroFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.studentdiary.databinding.FragmentPomodoroBinding
import com.example.studentdiary.ui.AppViewModel
import com.example.studentdiary.ui.NavigationComponents
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class PomodoroFragment : Fragment() {

    private var _binding: FragmentPomodoroBinding? = null
    private val binding get() = _binding!!
    private val appViewModel: AppViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPomodoroBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigationComponents()
    }
    private fun setupNavigationComponents() {
        appViewModel.hasNavigationComponents = NavigationComponents(navigationIcon = true, menuDrawer = true)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
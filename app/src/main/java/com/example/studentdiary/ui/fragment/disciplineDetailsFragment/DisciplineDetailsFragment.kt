package com.example.studentdiary.ui.fragment.disciplineDetailsFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.studentdiary.databinding.FragmentDisciplineDetailsBinding

class DisciplineDetailsFragment : Fragment() {

    private var _binding: FragmentDisciplineDetailsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDisciplineDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}


package com.example.studentdiary.ui.fragment.profileFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.studentdiary.databinding.FragmentProfileBinding
import com.example.studentdiary.ui.fragment.baseFragment.BaseFragment


class ProfileFragment : BaseFragment() {

    private var _binding:FragmentProfileBinding?= null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }






}
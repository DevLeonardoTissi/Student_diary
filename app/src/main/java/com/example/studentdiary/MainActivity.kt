package com.example.studentdiary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.studentdiary.databinding.ActivityMainBinding
import com.example.studentdiary.model.User
import com.example.studentdiary.ui.fragment.registerFragment.RegisterViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity() : AppCompatActivity() {

     private val binding by lazy{
         ActivityMainBinding.inflate(layoutInflater)
     }

    private val model: RegisterViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        model.register(User("leonardo.tissi.si@gmail.com", "12345678"))

    }
}
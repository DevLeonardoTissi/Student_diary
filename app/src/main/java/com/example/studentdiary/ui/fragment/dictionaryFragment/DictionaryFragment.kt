package com.example.studentdiary.ui.fragment.dictionaryFragment

import android.os.Bundle
import android.text.style.ImageSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.studentdiary.databinding.FragmentDictionaryBinding
import com.example.studentdiary.ui.AppViewModel
import com.example.studentdiary.ui.NavigationComponents
import com.example.studentdiary.ui.fragment.baseFragment.BaseFragment
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class DictionaryFragment : BaseFragment() {

    private var _binding: FragmentDictionaryBinding? = null
    private val binding get() = _binding!!
    private val model: DictionaryViewModel by viewModel()
    private val appViewModel: AppViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDictionaryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigationComponents()
        search()
    }

    private fun setupNavigationComponents() {
        appViewModel.hasNavigationComponents = NavigationComponents(navigationIcon = true, menuDrawer = true)
    }

    private fun search(){
        binding.fragmentDictionarySearchButton.apply {
            setOnClickListener {
                val fieldWord = binding.fragmentDictionaryFieldWord.editText
                val word = fieldWord?.text.toString()
                lifecycleScope.launch {
                    try {
                        Log.i("TAG", "search:foi" )
                       val l = model.searchMeaning(word)
                        Log.i("TAG", "search: $l")
                        binding.fragmentDictionaryTextViewResult.setText(l.toString())
                    }catch (e:Exception){
                        Log.i("TAG", "search:erro $e" )
                    }

                }
//

            }
        }
    }

    private fun setupChips(){
        val synonymChip = binding.fragmentDictionarySynonymChip
        val meaningChip = binding.fragmentDictionaryMeaningChip
        val syllabicSeparationChip = binding.fragmentDictionarySyllabicSeparationChip

        synonymChip.setOnCheckedChangeListener { _, isChecked ->  }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
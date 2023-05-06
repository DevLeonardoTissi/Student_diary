package com.example.studentdiary.ui.fragment.dictionaryFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.studentdiary.databinding.FragmentDictionaryBinding
import com.example.studentdiary.ui.AppViewModel
import com.example.studentdiary.ui.NavigationComponents
import com.example.studentdiary.ui.fragment.baseFragment.BaseFragment
import com.google.android.material.chip.Chip
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

    }

    override fun onResume() {
        super.onResume()
        updateVisibilityBasedOnCheckedChips()
        observerResults()
        search()
        saveFieldValue()
        observerFieldWord()
    }

    private fun setupNavigationComponents() {
        appViewModel.hasNavigationComponents =
            NavigationComponents(navigationIcon = true, menuDrawer = true)
    }

    private fun search() {
        binding.fragmentDictionarySearchButton.apply {
            setOnClickListener {
                val fieldWord = binding.fragmentDictionaryFieldWord
                fieldWord.error = null
                val isValid = validate()
                if (isValid) {
                    val word = fieldWord.editText?.text.toString()
                    handleResultVisibility(
                        binding.fragmentDictionaryMeaningChip,
                        binding.fragmentDictionaryTextViewMeaningResult
                    ) {
                        model.searchMeaning(word)
                    }
                    handleResultVisibility(
                        binding.fragmentDictionarySynonymChip,
                        binding.fragmentDictionaryTextViewSynonymsResult
                    ) {
                        model.searchSynonyms(word)
                    }
                    handleResultVisibility(
                        binding.fragmentDictionarySyllabicSeparationChip,
                        binding.fragmentDictionaryTextViewSyllablesSeparationResult
                    ) {
                        model.searchSyllables(word)
                    }
                    handleResultVisibility(
                        binding.fragmentDictionarySentencesChip,
                        binding.fragmentDictionaryTextViewSentencesResult
                    ) {
                        model.searchSentences(word)
                    }
                }
            }
        }
    }

    private fun validate(): Boolean {
        var valid = true
        val fieldWord = binding.fragmentDictionaryFieldWord
        if (fieldWord.editText?.text.toString().isBlank()) {
            fieldWord.error = "Campo vazio"
            valid = false
        }else{
            if (binding.fragmentDictionaryChipGroup.checkedChipIds.isEmpty()) {
                fieldWord.error = "Selecione pelo meno um item para pesquisa"
                valid = false
            }
        }

        return valid
    }

    private inline fun handleResultVisibility(chip: Chip, textView: TextView, block: () -> Unit) {
        if (chip.isChecked) {
            textView.visibility = View.VISIBLE
            block()
        } else {
            textView.visibility = View.GONE
        }
    }


    private fun updateVisibilityBasedOnCheckedChips() {
        val checkedChipIds = binding.fragmentDictionaryChipGroup.checkedChipIds

        setResultVisibility(
            binding.fragmentDictionaryTextViewMeaningResult,
            checkedChipIds.contains(binding.fragmentDictionaryMeaningChip.id)
        )

        setResultVisibility(
            binding.fragmentDictionaryTextViewSynonymsResult,
            checkedChipIds.contains(binding.fragmentDictionarySynonymChip.id)
        )

        setResultVisibility(
            binding.fragmentDictionaryTextViewSyllablesSeparationResult,
            checkedChipIds.contains(binding.fragmentDictionarySyllabicSeparationChip.id)
        )

        setResultVisibility(
            binding.fragmentDictionaryTextViewSentencesResult,
            checkedChipIds.contains(binding.fragmentDictionarySentencesChip.id)
        )
    }

    private fun setResultVisibility(textView: TextView, isVisible: Boolean) {
        textView.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun observerResults() {
        model.synonyms.observe(viewLifecycleOwner) {
            binding.fragmentDictionaryTextViewSynonymsResult.text = it.toString()
        }

        model.meaning.observe(viewLifecycleOwner) {
            binding.fragmentDictionaryTextViewMeaningResult.text = it.toString()
        }

        model.syllables.observe(viewLifecycleOwner) {
            binding.fragmentDictionaryTextViewSyllablesSeparationResult.text = it.toString()
        }

        model.senteces.observe(viewLifecycleOwner) {
            binding.fragmentDictionaryTextViewSentencesResult.text = it.toString()
        }
    }

    private fun observerFieldWord() {
        model.fieldWord.observe(viewLifecycleOwner) {
            binding.fragmentDictionaryFieldWord.editText?.setText(it)
        }
    }

    private fun saveFieldValue() {
        binding.fragmentDictionaryFieldWord.editText?.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    model.setQuery(binding.fragmentDictionaryFieldWord.editText?.text.toString())
                }
            }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
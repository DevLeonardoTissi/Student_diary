package com.example.studentdiary.ui.fragment.dictionaryFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.example.studentdiary.R
import com.example.studentdiary.databinding.FragmentDictionaryBinding
import com.example.studentdiary.extensions.isOnline
import com.example.studentdiary.extensions.showToastNoConnectionMessage
import com.example.studentdiary.ui.AppViewModel
import com.example.studentdiary.ui.NavigationComponents
import com.example.studentdiary.ui.dialog.LoadAlertDialog
import com.example.studentdiary.ui.fragment.baseFragment.BaseFragment
import com.google.android.material.chip.Chip
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
        observerResults()
        observerFieldWord()
        saveFieldValue()
        search()
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
                    if (context.isOnline()) {
                        lifecycleScope.launch {
                            val word = fieldWord.editText?.text.toString()
                            val loadAlertDialog = LoadAlertDialog(context)
                            loadAlertDialog.showLoadDialog()
                            model.cleanSearched()
                            updateVisibilityAndSearch(word)
                            loadAlertDialog.closeLoadDialog()
                        }

                    } else {
                        context.showToastNoConnectionMessage()
                    }
                }
            }
        }
    }

    private suspend fun updateVisibilityAndSearch(word: String) {
        handleResultVisibility(
            binding.fragmentDictionaryMeaningChip,
        ) {
            model.searchMeaning(word)
        }
        handleResultVisibility(
            binding.fragmentDictionarySynonymChip,
        ) {
            model.searchSynonyms(word)
        }
        handleResultVisibility(
            binding.fragmentDictionarySyllabicSeparationChip,
        ) {
            model.searchSyllables(word)
        }
        handleResultVisibility(
            binding.fragmentDictionarySentencesChip,
        ) {
            model.searchSentences(word)
        }
    }

    private fun validate(): Boolean {
        var valid = true
        val fieldWord = binding.fragmentDictionaryFieldWord
        if (fieldWord.editText?.text.toString().isBlank()) {
            fieldWord.error = getString(R.string.dictionary_fragment_error_empty_field_message)
            valid = false
        } else {
            if (binding.fragmentDictionaryChipGroup.checkedChipIds.isEmpty()) {
                fieldWord.error =
                    getString(R.string.dictionary_fragment_error_no_chip_select_message)
                valid = false
            }
        }
        return valid
    }

    private inline fun handleResultVisibility(
        chip: Chip,
        block: () -> Unit
    ) {
        if (chip.isChecked) {
            block()
        }
    }

    private fun setResultVisibility(
        resultTextView: TextView,
        labelTextView: TextView,
        isVisible: Boolean
    ) {
        resultTextView.visibility = if (isVisible) View.VISIBLE else View.GONE
        labelTextView.visibility = if (isVisible) View.VISIBLE else View.GONE

    }

    private fun observerResults() {
        model.synonyms.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                binding.fragmentDictionaryTextViewSynonymsResult.text =
                    getString(R.string.dictionary_fragment_synonyms_not_Found_message)
            } else {
                binding.fragmentDictionaryTextViewSynonymsResult.text = it.toString()
            }
            model.synonymsWasResearched()?.let { wasResearched ->
                setResultVisibility(
                    binding.fragmentDictionaryTextViewSynonymsResult,
                    binding.fragmentDictionaryTextViewSynonymsLabel,
                    wasResearched && binding.fragmentDictionaryChipGroup.checkedChipIds.contains(
                        binding.fragmentDictionarySynonymChip.id
                    )
                )
            }

        }

        model.meaning.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                binding.fragmentDictionaryTextViewMeaningResult.text =
                    getString(R.string.dictionary_fragment_meaning_not_Found_message)
            } else {
                binding.fragmentDictionaryTextViewMeaningResult.text = it.toString()
            }

            model.meaningWasResearched()?.let { wasResearched ->
                setResultVisibility(
                    binding.fragmentDictionaryTextViewMeaningResult,
                    binding.fragmentDictionaryTextViewMeaningLabel,
                    wasResearched && binding.fragmentDictionaryChipGroup.checkedChipIds.contains(
                        binding.fragmentDictionaryMeaningChip.id
                    )
                )
            }
        }

        model.syllables.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                binding.fragmentDictionaryTextViewSyllablesSeparationResult.text =
                    getString(R.string.dictionary_fragment_syllables_not_Found_message)
            } else {
                binding.fragmentDictionaryTextViewSyllablesSeparationResult.text = it.toString()
            }
            model.syllablesWasResearched()?.let { wasResearched ->
                setResultVisibility(
                    binding.fragmentDictionaryTextViewSyllablesSeparationResult,
                    binding.fragmentDictionaryTextViewSyllablesSeparationLabel,
                    wasResearched && binding.fragmentDictionaryChipGroup.checkedChipIds.contains(
                        binding.fragmentDictionarySyllabicSeparationChip.id
                    )
                )
            }

        }

        model.sentences.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                binding.fragmentDictionaryTextViewSentencesResult.text =
                    getString(R.string.dictionary_fragment_sentences_not_Found_message)
            } else {
                binding.fragmentDictionaryTextViewSentencesResult.text = it.toString()

            }
            model.sentencesWasResearched()?.let { wasResearched ->
                setResultVisibility(
                    binding.fragmentDictionaryTextViewSentencesResult,
                    binding.fragmentDictionaryTextViewSentencesLabel,
                    wasResearched && binding.fragmentDictionaryChipGroup.checkedChipIds.contains(
                        binding.fragmentDictionarySentencesChip.id
                    )
                )
            }

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
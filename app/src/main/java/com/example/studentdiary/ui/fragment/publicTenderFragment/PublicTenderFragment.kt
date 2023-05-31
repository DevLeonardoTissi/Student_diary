package com.example.studentdiary.ui.fragment.publicTenderFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentdiary.R
import com.example.studentdiary.databinding.FragmentPublicTenderBinding
import com.example.studentdiary.model.PublicTender
import com.example.studentdiary.ui.AppViewModel
import com.example.studentdiary.ui.NavigationComponents
import com.example.studentdiary.ui.dialog.PublicTenderSuggestionDialog
import com.example.studentdiary.ui.fragment.baseFragment.BaseFragment
import com.example.studentdiary.ui.recyclerView.adapter.PublicTenderAdapter
import com.example.studentdiary.utils.goToUri
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PublicTenderFragment : BaseFragment() {

    private var _binding: FragmentPublicTenderBinding? = null
    private val binding get() = _binding!!
    private val appViewModel: AppViewModel by activityViewModel()
    private val model: PublicTenderViewModel by viewModel()
    private val adapter: PublicTenderAdapter by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPublicTenderBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigationComponents()
        setupRecyclerview()
        updateUi()
        setupFABTips()
        onClickCardViewSuggestion()
    }


    private fun updateUi() {
        model.publicTenderList.observe(viewLifecycleOwner) {
            buttonToggleGroupFilterList(it)
        }
    }

    private fun buttonToggleGroupFilterList(list: List<PublicTender>) {
        val toggleGroup = binding.publicTenderFragmentButtonToggleGroup
        checkButtonCheckedAndUpdateList(toggleGroup.checkedButtonId, list)

        toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                checkButtonCheckedAndUpdateList(checkedId, list)
            }
        }
    }

    private fun checkButtonCheckedAndUpdateList(checkedId: Int, list: List<PublicTender>) {
        when (checkedId) {
            R.id.publicTenderFragment_toggle_button_contest -> {
                updateList(list.filter { it.contest })

            }

            R.id.publicTenderFragment_toggle_button_courses -> {
                updateList(list.filter {
                    it.course
                })
            }
        }
    }

    private fun updateList(list: List<PublicTender>) {
        adapter.submitList(list)
        messageEmptyList(list)
    }

    private fun messageEmptyList(list: List<PublicTender>) {
        val visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        binding.publicTenderFragmentLottieAnimationViewEmptyBox.visibility = visibility
        binding.publicTenterFragmentTextViewEmpty.visibility = visibility
    }


    private fun setupRecyclerview() {
        val recyclerView = binding.publicTenterFragmentRecyclerView
        recyclerView.adapter = adapter
        context?.let { context ->
            recyclerView.layoutManager = LinearLayoutManager(context)
            adapter.onItemClick = { url ->
                goToUri(url, context)
            }
        }

    }

    private fun setupNavigationComponents() {
        appViewModel.hasNavigationComponents =
            NavigationComponents(navigationIcon = true, menuDrawer = true)
    }

    private fun setupFABTips() {
        openCardViewSuggestion(model.getIsOpen())
        binding.publicTenderFragmentFabTips.apply {
            setOnClickListener {
                toggleAndOpenCardViewSuggestion()
            }
        }
    }

    private fun onClickCardViewSuggestion() {
        binding.publicTenderFragmentCardViewSuggestion.setOnClickListener {
            openAlertDialogSuggestion()
        }
    }

    private fun openAlertDialogSuggestion() {
        context?.let { context ->
            PublicTenderSuggestionDialog(context)
                .show { publicTenderSuggestionNonNull ->
                    publicTenderSuggestionNonNull?.let { publicTenderSuggestion ->
                        model.add(publicTenderSuggestion)
                    }
                    toggleAndOpenCardViewSuggestion()
                    showAnimationViewSuggestionDone()

                }
        }
    }

    private fun showAnimationViewSuggestionDone() {
        val lottieAnimationViewSuggestionDone =
            binding.publicTenderFragmentLottieAnimationViewSuggestionDone
        lottieAnimationViewSuggestionDone.visibility = View.VISIBLE
        lottieAnimationViewSuggestionDone.postDelayed({
            lottieAnimationViewSuggestionDone.visibility = View.GONE
        }, 3000)
    }

    private fun toggleAndOpenCardViewSuggestion() {
        model.setIsOpen(!model.getIsOpen())
        openCardViewSuggestion(model.getIsOpen())
    }

    private fun openCardViewSuggestion(enable: Boolean) {
        val cardViewSuggestion = binding.publicTenderFragmentCardViewSuggestion
        val fabSuggestions = binding.publicTenderFragmentFabTips

        if (enable && cardViewSuggestion.visibility != View.VISIBLE) {
            cardViewSuggestion.visibility = View.VISIBLE
            val enterAnimation = AnimationUtils.loadAnimation(context, R.anim.enter_from_botton)
            cardViewSuggestion.startAnimation(enterAnimation)
            fabSuggestions.startAnimation(enterAnimation)
        } else {
            val exitAnimation = AnimationUtils.loadAnimation(context, R.anim.exit_to_bottom)
            cardViewSuggestion.startAnimation(exitAnimation)
            fabSuggestions.startAnimation(exitAnimation)

            cardViewSuggestion.postDelayed({
                cardViewSuggestion.visibility = View.GONE
            }, 1000)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
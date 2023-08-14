package com.example.studentdiary.ui.fragment.publicTenderFragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentdiary.R
import com.example.studentdiary.databinding.FragmentPublicTenderBinding
import com.example.studentdiary.extensions.snackBar
import com.example.studentdiary.model.PublicTender
import com.example.studentdiary.notifications.Notification
import com.example.studentdiary.ui.dialog.PublicTenderSuggestionDialog
import com.example.studentdiary.ui.fragment.baseFragment.BaseFragment
import com.example.studentdiary.ui.recyclerView.adapter.PublicTenderAdapter
import com.example.studentdiary.utils.goToUri
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PublicTenderFragment : BaseFragment() {

    private var _binding: FragmentPublicTenderBinding? = null
    private val binding get() = _binding!!
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
                updateList(list.filter { it.isContest })
            }

            R.id.publicTenderFragment_toggle_button_courses -> {
                updateList(list.filter {
                    it.isCourse
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


    private fun setupFABTips() {
        openCardViewSuggestion(model.getIsOpen())
        binding.publicTenderFragmentFabTips.setOnClickListener {
            toggleAndOpenCardViewSuggestion()
        }
    }

    private fun onClickCardViewSuggestion() {
        binding.publicTenderFragmentCardViewSuggestion.setOnClickListener {
            openAlertDialogSuggestion()
        }
    }

    private fun openAlertDialogSuggestion() {
        context?.let { context ->
            PublicTenderSuggestionDialog(context, this@PublicTenderFragment)
                .show { publicTenderSuggestion ->
                    publicTenderSuggestion?.let { publicTenderSuggestionNotNull ->
                        model.addPublicTenderSuggestion(
                            publicTenderSuggestionNotNull,
                            onSuccess = {
                                showNotificationSendPublicTenderSuggestion(context)
                            },
                            onFailure = { snackBar(getString(R.string.public_tender_fragment_snackbar_message_error_upload_public_tender_suggestion)) })
                    }
                    toggleAndOpenCardViewSuggestion()
                    showAnimationViewSuggestionDone()

                }
        }
    }

    private fun showNotificationSendPublicTenderSuggestion(context: Context) {
        val imgSuggestionsNotification =
            "https://img.freepik.com/psd-gratuitas/colecao-3d-com-maos-fazendo-o-simbolo-do-coracao_23-2148938879.jpg?w=1060&t=st=1692051287~exp=1692051887~hmac=606fb4a3bbfdf247a7214ab24386d265105ca01bd630be18b2516a383d03cf4b"
        Notification(context).show(
            title = getString(R.string.suggestion_notification_title),
            description = getString(R.string.suggestion_notification_description),
            img = imgSuggestionsNotification,
            iconId = R.drawable.ic_notification_suggestion_send
        )
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
            fabSuggestions.setImageResource(R.drawable.ic_down)
        } else {
            val exitAnimation = AnimationUtils.loadAnimation(context, R.anim.exit_to_bottom)
            cardViewSuggestion.startAnimation(exitAnimation)
            fabSuggestions.startAnimation(exitAnimation)
            fabSuggestions.setImageResource(R.drawable.ic_tips)

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
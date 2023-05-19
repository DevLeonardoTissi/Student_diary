package com.example.studentdiary.ui.fragment.publicTenderFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentdiary.R
import com.example.studentdiary.databinding.FragmentPublicTenderBinding
import com.example.studentdiary.model.PublicTender
import com.example.studentdiary.ui.AppViewModel
import com.example.studentdiary.ui.NavigationComponents
import com.example.studentdiary.ui.fragment.baseFragment.BaseFragment
import com.example.studentdiary.ui.recyclerView.adapter.PublicTenderAdapter
import com.example.studentdiary.utils.goToUri
import com.google.android.material.divider.MaterialDividerItemDecoration
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class PublicTenderFragment : BaseFragment() {

    private var _binding: FragmentPublicTenderBinding? = null
    private val binding get() = _binding!!
    private val appViewModel: AppViewModel by activityViewModel()
    private val model: PublicTenderViewModel by inject()
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
        binding.publicTenderFragmentLottieAnimationViewGhost.visibility = visibility
        binding.publicTenterFragmentTextViewEmpty.visibility = visibility
    }


    private fun setupRecyclerview() {
        val recyclerView = binding.publicTenterFragmentRecyclerView
        recyclerView.adapter = adapter
        context?.let { context ->
            recyclerView.layoutManager = LinearLayoutManager(context)
            val divider = MaterialDividerItemDecoration(context, LinearLayoutManager.VERTICAL)
            recyclerView.addItemDecoration(divider)
            divider.dividerColor = context.getColor(R.color.colorOutline)
            adapter.onItemClick = { url ->
                goToUri(url, context)
            }
        }

    }

    private fun setupNavigationComponents() {
        appViewModel.hasNavigationComponents =
            NavigationComponents(navigationIcon = true, menuDrawer = true)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
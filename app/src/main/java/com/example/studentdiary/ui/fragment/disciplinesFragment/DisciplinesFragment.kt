package com.example.studentdiary.ui.fragment.disciplinesFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentdiary.R
import com.example.studentdiary.databinding.FragmentDisciplinesBinding
import com.example.studentdiary.model.Discipline
import com.example.studentdiary.ui.recyclerView.adapter.DisciplineListAdapter
import com.google.android.material.divider.MaterialDividerItemDecoration
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class DisciplinesFragment : Fragment() {

    private var _binding: FragmentDisciplinesBinding? = null
    private val binding get() = _binding!!
    private val adapter: DisciplineListAdapter by inject()
    private val model: DisciplinesViewModel by viewModel()
    private val controller by lazy {
        findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDisciplinesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureDisciplineObserver()
        configureRecyclerView()
        configureFab()
    }


    private fun configureDisciplineObserver() {
        model.disciplineList.observe(viewLifecycleOwner) { list ->
            buttonToggleGroupFilterList(list)
        }
    }

    private fun messageEmptyList(list: List<Discipline>) {
        val visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        binding.disciplinesFragmentImageViewStudent.visibility = visibility
        binding.disciplinesFragmentTextViewStudent.visibility = visibility
    }

    private fun buttonToggleGroupFilterList(list: List<Discipline>) {
        val toggleButton = binding.disciplinesFragmentButtonToggleGroup

        checkButtonCheckedAndUpdateList(toggleButton.checkedButtonId, list)

        toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                checkButtonCheckedAndUpdateList(checkedId, list)
            }
        }
    }

    private fun checkButtonCheckedAndUpdateList(checkedId: Int, list: List<Discipline>) {
        when (checkedId) {
            R.id.disciplinesFragment_toggle_button_all -> {
                adapter.submitList(list)
                messageEmptyList(list)
            }
            R.id.disciplinesFragment_toggle_button_favorites -> {
                val favoriteList = list.filter { it.favorite }
                adapter.submitList(favoriteList)
                messageEmptyList(favoriteList)
            }
            R.id.disciplinesFragment_toggle_button_completed -> {
                val completedList = list.filter { it.completed }
                adapter.submitList(completedList)
                messageEmptyList(completedList)
            }
        }
    }

    private fun configureRecyclerView() {
        val recycler = binding.disciplinesFragmentRecyclerView
        recycler.adapter = adapter
        context?.let {
            recycler.layoutManager = LinearLayoutManager(it)
            val divider = MaterialDividerItemDecoration(it, LinearLayoutManager.VERTICAL)
            recycler.addItemDecoration(divider)
            adapter.onItemClick = { disciplineId ->
                goToDisciplineDetails(disciplineId)

            }
//            divider.dividerColor = it.getColor(R.color.secondary)
        }
    }

    private fun goToDisciplineDetails(disciplineId: String) {
//        val direction = DisciplinesFragmentDirections.actionDisciplinesFragmentToDisciplineDetailsFragment(disciplineId)
//        controller.navigate(direction)

        val direction =
            DisciplinesFragmentDirections.actionDisciplinesFragmentToDisciplineFormFragment(
                disciplineId
            )
        controller.navigate(direction)

    }

    private fun configureFab() {
        val fab = binding.disciplinesFragmentFabInsert
        fab.setOnClickListener {
            val direction =
                DisciplinesFragmentDirections.actionDisciplinesFragmentToDisciplineFormFragment(null)
            controller.navigate(direction)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
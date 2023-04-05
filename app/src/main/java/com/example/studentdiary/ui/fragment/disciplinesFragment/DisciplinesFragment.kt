package com.example.studentdiary.ui.fragment.disciplinesFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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
        configureDiscilpineObserver()
        configureRecyclerView()
        configureFab()
    }


    private fun configureDiscilpineObserver() {
        model.disciplineList.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            configureSwitchFavorite(list)
        }
    }

    private fun messageEmptyList(list: List<Discipline>) {
        val visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        binding.disciplinesFragmentImageViewStudent.visibility = visibility
        binding.disciplinesFragmentTextViewStudent.visibility = visibility
    }

    private fun configureSwitchFavorite(list: List<Discipline>) {
        val switchFavorite = binding.disciplineFragmentSwitchFavorite
        val filterList = list.filter { it.favorite }

        if (switchFavorite.isChecked) {
            adapter.submitList(filterList)
            messageEmptyList(filterList)
        } else {
            adapter.submitList(list)
            messageEmptyList(list)
        }

        switchFavorite.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                messageEmptyList(filterList)
                adapter.submitList(filterList)

            } else {
                messageEmptyList(list)
                adapter.submitList(list)
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
            adapter.onItemClick=  {disciplineId ->
                goToDisciplineDetails(disciplineId)

            }
//            divider.dividerColor = it.getColor(R.color.secondary)
        }
    }

    private fun goToDisciplineDetails(disciplineId: String) {
//        val direction = DisciplinesFragmentDirections.actionDisciplinesFragmentToDisciplineDetailsFragment(disciplineId)
//        controller.navigate(direction)

        val direction = DisciplinesFragmentDirections.actionDisciplinesFragmentToDisciplineFormFragment(disciplineId)
        controller.navigate(direction)

    }

    private fun configureFab() {
        val fab = binding.disciplinesFragmentFabInsert
        fab.setOnClickListener {
            val direction = DisciplinesFragmentDirections.actionDisciplinesFragmentToDisciplineFormFragment(null)
            controller.navigate(direction)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
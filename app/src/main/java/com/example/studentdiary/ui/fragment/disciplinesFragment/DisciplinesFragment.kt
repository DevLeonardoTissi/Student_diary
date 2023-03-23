package com.example.studentdiary.ui.fragment.disciplinesFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentdiary.databinding.FragmentDisciplinesBinding
import com.example.studentdiary.ui.recyclerView.adapter.DisciplineListAdapter
import com.google.android.material.divider.MaterialDividerItemDecoration
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class DisciplinesFragment : Fragment() {

    private var _binding: FragmentDisciplinesBinding? = null
    private val binding get() = _binding!!
    private val adapter: DisciplineListAdapter by inject()
    private val model: DisciplinesViewModel by viewModel()

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
        searchDisciplines()
        configureFab()
    }

    private fun configureDiscilpineObserver() {
        model.disciplineList.observe(viewLifecycleOwner) { list ->
            list?.let {
                adapter.submitList(list)
            }
        }
    }

    private fun configureRecyclerView() {
        val recycler = binding.disciplinesFragmentRecyclerView
        recycler.adapter = adapter
        context?.let {
            recycler.layoutManager = LinearLayoutManager(context)
            val divider = MaterialDividerItemDecoration(it, LinearLayoutManager.VERTICAL)
            recycler.addItemDecoration(divider)
            divider.isLastItemDecorated = false
        }


    }

    private fun configureFab() {
        val fab = binding.disciplinesFragmentFabInsert
        fab.setOnClickListener {
            insert()
        }
    }

    private fun searchDisciplines() {
        lifecycleScope.launch {
            model.searchAll()
        }

    }

    private fun insert() {
        lifecycleScope.launch {
            model.insert()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
package com.example.studentdiary.ui.fragment.disciplinesFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentdiary.R
import com.example.studentdiary.databinding.FragmentDisciplinesBinding
import com.example.studentdiary.model.Discipline
import com.example.studentdiary.ui.recyclerView.adapter.DisciplineListAdapter
import com.google.android.material.divider.MaterialDividerItemDecoration
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


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
        configureFab()
    }

    private fun configureDiscilpineObserver() {
        model.disciplineList.observe(viewLifecycleOwner) { list ->
            configureSwitchFavorite(list)
        }
    }

    private fun messageEmptyList(list: List<Discipline>) {
        val visibility = if (list.isEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
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
            divider.dividerColor = it.getColor(R.color.secondary)
        }
    }

    private fun configureFab() {
        val fab = binding.disciplinesFragmentFabInsert
        fab.setOnClickListener {
            insert(
                Discipline(
                    name = UUID.randomUUID().toString(),
                    favorite = true,
                    img = "https://st2.depositphotos.com/1594308/12210/i/450/depositphotos_122104490-stock-photo-smiing-female-college-student.jpg"
                )
            )
        }
    }


    private fun insert(discipline: Discipline) {
        lifecycleScope.launch {
            model.insert(discipline)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
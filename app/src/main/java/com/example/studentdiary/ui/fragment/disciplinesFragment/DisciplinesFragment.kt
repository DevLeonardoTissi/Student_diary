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
            val visibility = if (list.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
            binding.disciplinesFragmentImageViewStudent.visibility = visibility
            binding.disciplinesFragmentTextViewStudent.visibility = visibility

            adapter.submitList(list)
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
                    img = "https://www.facebook.com/photo/?fbid=2611089512365404&set=a.112877185519995"
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
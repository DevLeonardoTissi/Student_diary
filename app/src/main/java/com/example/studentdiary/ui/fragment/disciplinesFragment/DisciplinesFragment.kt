package com.example.studentdiary.ui.fragment.disciplinesFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentdiary.R
import com.example.studentdiary.databinding.FragmentDisciplinesBinding
import com.example.studentdiary.extensions.alertDialog
import com.example.studentdiary.extensions.converterToPercent
import com.example.studentdiary.model.Discipline
import com.example.studentdiary.ui.fragment.baseFragment.BaseFragment
import com.example.studentdiary.ui.recyclerView.adapter.DisciplineListAdapter
import com.google.android.material.divider.MaterialDividerItemDecoration
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class DisciplinesFragment : BaseFragment() {

    private var _binding: FragmentDisciplinesBinding? = null
    private val binding get() = _binding!!
    private val adapter: DisciplineListAdapter by inject()
    private val model: DisciplinesViewModel by viewModel()
    private val controller by lazy {
        findNavController()
    }

    lateinit var filterList: List<Discipline>

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
        clearQuery()
        configureRecyclerView()
        configureFab()
        addMenuProvider()
    }

    private fun clearQuery() {
        model.clearQuery()
    }


    private fun configureDisciplineObserver() {
        model.disciplineList.observe(viewLifecycleOwner) { list ->
            buttonToggleGroupFilterList(list)
        }
    }

    private fun messageEmptyList(list: List<Discipline>) {
        val visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        binding.disciplinesFragmentLottieAnimationViewEmpty.visibility = visibility
        binding.disciplinesFragmentTextViewEmpty.visibility = visibility
    }

    private fun buttonToggleGroupFilterList(list: List<Discipline>) {
        val toggleGroup = binding.disciplinesFragmentButtonToggleGroup

        checkButtonCheckedAndUpdateList(toggleGroup.checkedButtonId, list)

        toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                checkButtonCheckedAndUpdateList(checkedId, list)
            }
        }
    }

    private fun checkButtonCheckedAndUpdateList(checkedId: Int, list: List<Discipline>) {
        when (checkedId) {
            R.id.disciplinesFragment_toggle_button_all -> {
                updateList(list.filter { it.name?.contains(model.getQuery(), true) == true })
                filterList = list
            }

            R.id.disciplinesFragment_toggle_button_favorites -> {
                val favoriteList = list.filter { it.favorite }
                filterList = favoriteList
                updateList(favoriteList.filter {
                    it.name?.contains(
                        model.getQuery(),
                        true
                    ) == true
                })
            }

            R.id.disciplinesFragment_toggle_button_completed -> {
                val completedList = list.filter { it.completed }
                filterList = completedList
                updateList(completedList.filter {
                    it.name?.contains(
                        model.getQuery(),
                        true
                    ) == true
                })
            }

            R.id.disciplinesFragment_toggle_button_descending -> {
                val descendingList = list.sortedByDescending { it.name }
                filterList = descendingList
                updateList(descendingList.filter {
                    it.name?.contains(
                        model.getQuery(),
                        true
                    ) == true
                })
            }

            R.id.disciplinesFragment_toggle_button_growing -> {
                val growingList = list.sortedBy { it.name }
                filterList = growingList
                updateList(growingList.filter { it.name?.contains(model.getQuery(), true) == true })
            }

            R.id.disciplinesFragment_toggle_button_date -> {
                val growingDateList = list.sortedBy { it.date?.first }
                filterList = growingDateList
                updateList(growingDateList.filter {
                    it.name?.contains(
                        model.getQuery(),
                        true
                    ) == true
                })
            }
        }
    }

    private fun updateList(list: List<Discipline>) {
        adapter.submitList(list)
        messageEmptyList(list)
        updateProgressIndicator(list)

    }

    private fun updateProgressIndicator(list: List<Discipline>) {
        val visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
        val percentageOfCoursesCompleted =
            (list.count { it.completed }.toDouble() / list.size) * 100
        binding.disciplinesFragmentProgressIndicator.progress = percentageOfCoursesCompleted.toInt()
        binding.disciplinesFragmentProgressIndicator.visibility = visibility
        binding.disciplinesFragmentTextViewIndicatorLabel.visibility = visibility
        binding.disciplinesFragmentTextViewIndicatorPercent.visibility = visibility
        binding.disciplinesFragmentTextViewIndicatorPercent.text =
            String.converterToPercent(percentageOfCoursesCompleted)
    }

    private fun configureRecyclerView() {
        val recyclerView = binding.disciplinesFragmentRecyclerView
        recyclerView.adapter = adapter
        context?.let {
            recyclerView.layoutManager = LinearLayoutManager(it)
            val divider = MaterialDividerItemDecoration(it, LinearLayoutManager.VERTICAL)
            recyclerView.addItemDecoration(divider)
            divider.dividerColor = it.getColor(R.color.colorOutline)
            adapter.onItemClick = { disciplineId ->
                goToDisciplineDetails(disciplineId)
            }
            adapter.onClickingOnOptionEdit = { disciplineId ->
                goToDisciplineForm(disciplineId)
            }

            adapter.onClickingOnOptionDetails = { disciplineId ->
                goToDisciplineDetails(disciplineId)

            }

            adapter.onClickingOnOptionDelete = { disciplineId ->
                context?.let { context ->
                    context.alertDialog(
                        icon = R.drawable.ic_delete,
                        title = getString(R.string.discipline_fragment_delete_dialog_title),
                        message = getString(R.string.discipline_fragment_delete_dialog_message),
                        onClickingOnPositiveButton = {
                            model.delete(disciplineId, context)
                        }
                    )
                }
            }
        }
    }

    private fun goToDisciplineForm(disciplineId: String?) {
        val direction =
            DisciplinesFragmentDirections.actionDisciplinesFragmentToDisciplineFormFragment(
                disciplineId
            )
        controller.navigate(direction)

    }

    private fun goToDisciplineDetails(disciplineId: String) {
        val direction =
            DisciplinesFragmentDirections.actionDisciplinesFragmentToDisciplineDetailsFragment(
                disciplineId
            )
        controller.navigate(direction)
    }

    private fun configureFab() {
        val fab = binding.disciplinesFragmentFabInsert
        fab.setOnClickListener {
            goToDisciplineForm(null)
        }
    }


    private fun addMenuProvider() {
        activity?.let {
            val menuHost: MenuHost = it
            menuHost.invalidateMenu()
            menuHost.addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.disciplines_fragment_menu, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.menuItem_disciplines_fragment_search -> {
                            val searchView = menuItem.actionView as? SearchView
                            setupSearchView(searchView)
                            true
                        }

                        else -> false
                    }
                }

            }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        }
    }

    private fun setupSearchView(searchView: SearchView?) {
        searchView?.let {
            searchView.isSubmitButtonEnabled = false
            searchView.queryHint =
                getString(R.string.disciplines_fragment_menu_menuItem_search_hint)
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let {
                        updateList(filterList.filter {
                            it.name?.contains(
                                newText,
                                ignoreCase = true
                            ) == true
                        })
                        model.setQuery(newText)
                    }
                    return true
                }
            })
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
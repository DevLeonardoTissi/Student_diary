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
import com.example.studentdiary.model.Discipline
import com.example.studentdiary.ui.AppViewModel
import com.example.studentdiary.ui.NavigationComponents
import com.example.studentdiary.ui.fragment.baseFragment.BaseFragment
import com.example.studentdiary.ui.recyclerView.adapter.DisciplineListAdapter
import com.google.android.material.divider.MaterialDividerItemDecoration
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DisciplinesFragment : BaseFragment() {

    private var _binding: FragmentDisciplinesBinding? = null
    private val binding get() = _binding!!
    private val adapter: DisciplineListAdapter by inject()
    private val model: DisciplinesViewModel by viewModel()
    private val controller by lazy {
        findNavController()
    }
    private val appViewModel: AppViewModel by activityViewModel()
     lateinit var  filterList : List<Discipline>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDisciplinesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigationComponents()
        configureDisciplineObserver()
        configureRecyclerView()
        configureFab()
        addMenuProvider()
    }

    private fun setupNavigationComponents() {
        appViewModel.hasNavigationComponents =
            NavigationComponents(navigationIcon = true, menuDrawer = true)
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
                updateList(list)
                filterList = list
            }

            R.id.disciplinesFragment_toggle_button_favorites -> {
                val favoriteList = list.filter { it.favorite }
                filterList = favoriteList
                updateList(favoriteList)
            }

            R.id.disciplinesFragment_toggle_button_completed -> {
                val completedList = list.filter { it.completed }
                filterList = completedList
                updateList(completedList)
            }

            R.id.disciplinesFragment_toggle_button_descending -> {
                val descendingList = list.sortedByDescending { it.name }
                filterList = descendingList
                updateList(descendingList)
            }

            R.id.disciplinesFragment_toggle_button_growing -> {
                val growingList = list.sortedBy { it.name }
                filterList = growingList
                updateList(growingList)
            }
            R.id.disciplinesFragment_toggle_button_date -> {
                val growingDateList = list.sortedBy { it.date?.first }
                filterList = growingDateList
                updateList(growingDateList)
            }
        }
    }

    private fun updateList(list: List<Discipline>) {
        adapter.submitList(list)
        messageEmptyList(list)

    }
    private fun configureRecyclerView() {
        val recycler = binding.disciplinesFragmentRecyclerView
        recycler.adapter = adapter
        context?.let {
            recycler.layoutManager = LinearLayoutManager(it)
            val divider = MaterialDividerItemDecoration(it, LinearLayoutManager.VERTICAL)
            recycler.addItemDecoration(divider)
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
                        title = getString(R.string.discipline_fragment_delete_dialog_title),
                        message = getString(R.string.discipline_fragment_delete_dialog_message), onClickingOnPositiveButton = {
                            model.delete(disciplineId)
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
                        R.id.menuItem_disciplines_fragment_search-> {
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
                        updateList(filterList.filter { it.name?.contains(newText, ignoreCase = true) == true })
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
package com.example.studentdiary.ui.fragment.disciplineDetailsFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.studentdiary.R
import com.example.studentdiary.databinding.FragmentDisciplineDetailsBinding
import com.example.studentdiary.extensions.alertDialog
import com.example.studentdiary.ui.AppViewModel
import com.example.studentdiary.ui.NavigationComponents
import com.example.studentdiary.ui.fragment.baseFragment.BaseFragment
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DisciplineDetailsFragment : BaseFragment() {

    private var _binding: FragmentDisciplineDetailsBinding? = null
    private val binding get() = _binding!!
    private val appViewModel: AppViewModel by activityViewModel()
    private val arguments by navArgs<DisciplineDetailsFragmentArgs>()
    private val disciplineId by lazy {
        arguments.disciplineId
    }
    private val model: DisciplineDetailsViewModel by viewModel{ parametersOf(disciplineId) }
    private val controller by lazy {
        findNavController()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDisciplineDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigationComponents()
        addMenuProvider()
        updateUi()
    }

    private fun updateUi(){
        model.foundDiscipline.observe(viewLifecycleOwner){discipline ->

        }
    }

    private fun setupNavigationComponents() {
        appViewModel.hasNavigationComponents =
            NavigationComponents(navigationIcon = true, menuDrawer = true)
    }

    private fun addMenuProvider() {
        activity?.let {
            val menuHost: MenuHost = it
            menuHost.invalidateMenu()
            menuHost.addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.discipline_details_menu, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.menuItem_discipline_details_edit -> {
                            goToDisciplineForm()
                            true
                        }

                        R.id.menuItem_discipline_details_remove -> {
                            context?.let { context ->
                                context.alertDialog(
                                    title = getString(R.string.discipline_form_fragment_delete_dialog_title),
                                    message = getString(R.string.discipline_form_fragment_delete_dialog_message),
                                    onClickingOnPositiveButton = {
                                        model.delete()
                                        controller.popBackStack()
                                    }
                                )
                            }
                            true
                        }
                        else -> false
                    }
                }

            }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        }
    }

    private fun goToDisciplineForm() {
        val direction =
            DisciplineDetailsFragmentDirections.actionDisciplineDetailsFragmentToDisciplineFormFragment(
                disciplineId
            )
        controller.navigate(direction)
    }



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}


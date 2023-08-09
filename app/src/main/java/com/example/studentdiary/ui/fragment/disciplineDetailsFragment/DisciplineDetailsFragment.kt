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
import com.example.studentdiary.extensions.tryLoadImage
import com.example.studentdiary.ui.fragment.baseFragment.BaseFragment
import com.example.studentdiary.utils.concatUtils.concatenateDateValues
import com.example.studentdiary.utils.concatUtils.concatenateTimeValues
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class DisciplineDetailsFragment : BaseFragment() {

    private var _binding: FragmentDisciplineDetailsBinding? = null
    private val binding get() = _binding!!
    private val arguments by navArgs<DisciplineDetailsFragmentArgs>()
    private val disciplineId by lazy {
        arguments.disciplineId
    }
    private val model: DisciplineDetailsViewModel by viewModel { parametersOf(disciplineId) }
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
        addMenuProvider()
        updateUi()
    }

    private fun updateUi() {
        model.foundDiscipline.observe(viewLifecycleOwner) { discipline ->

            binding.disciplineDetailsFragmentShapeableImageViewDiscipline.tryLoadImage(discipline.img)

            binding.disciplineDetailsFragmentTextViewDisciplineName.text = discipline.name

            binding.disciplineDetailsFragmentTextViewDisciplineDescription.text =
                discipline.description

            binding.disciplineDetailsFragmentImageViewFavorite.apply {
                visibility = if (discipline.favorite) View.VISIBLE else View.GONE
            }

            binding.disciplineDetailsFragmentImageViewCompleted.apply {
                visibility = if (discipline.completed) View.VISIBLE else View.GONE
            }

            discipline.startTime?.let {
                binding.disciplineDetailsFragmentTextViewStartTime.text = concatenateTimeValues(it)
            }

            discipline.endTime?.let {
                binding.disciplineDetailsFragmentTextViewEndTime.text = concatenateTimeValues(it)
            }

            binding.disciplineDetailsFragmentTextViewDate.apply {
                discipline.date?.let {
                    this.visibility = View.VISIBLE
                    this.text = concatenateDateValues(it)
                } ?: kotlin.run {
                    visibility = View.GONE
                }
            }
        }
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
                                    icon = R.drawable.ic_delete,
                                    title = getString(R.string.discipline_form_fragment_delete_dialog_title),
                                    message = getString(R.string.discipline_form_fragment_delete_dialog_message),
                                    onClickingOnPositiveButton = {
                                        model.delete(context)
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


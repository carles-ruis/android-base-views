package com.carles.simple.ui.hyrule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import com.carles.simple.R
import com.carles.simple.ui.common.dp
import com.carles.simple.ui.common.BaseFragment
import com.carles.simple.databinding.FragmentMonstersBinding
import com.carles.simple.ui.hyrule.ErrorDialogFragment.Companion.REQUEST_CODE_RETRY
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MonstersFragment : BaseFragment<FragmentMonstersBinding>() {

    private val viewModel: MonstersViewModel by viewModels()

    override val progress: View
        get() = binding.monstersProgress.progress

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResultListener()
    }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMonstersBinding {
        return FragmentMonstersBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()
        binding.monstersRecycler.addItemDecoration(
            MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL).apply {
                dividerInsetStart = 16.dp
                dividerInsetEnd = 16.dp
            }
        )
        binding.monstersRecycler.adapter = MonstersAdapter { monster ->
            viewModel.onMonsterClicked(monster)
        }
        observeMonsters()
    }

    private fun setResultListener() {
        setFragmentResultListener(REQUEST_CODE_RETRY) { _, _ ->
            viewModel.retry()
        }
    }

    private fun setupMenu() {
        (activity as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.monsters_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return menuItem.onNavDestinationSelected(findNavController())
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun observeMonsters() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MonstersState.Success -> {
                    hideProgress()
                    (binding.monstersRecycler.adapter as MonstersAdapter).submitList(state.monsters)
                }
                is MonstersState.Error -> {
                    hideProgress()
                    viewModel.onErrorDisplayed(state.message ?: getString(R.string.error_server_response))
                }
                is MonstersState.Loading -> showProgress()
                else -> { } // when expression must be exhausive false positive
            }
        }
    }
}
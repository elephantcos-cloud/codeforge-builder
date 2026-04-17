package com.codeforge.builder.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.codeforge.builder.R
import com.codeforge.builder.data.local.entity.Project
import com.codeforge.builder.data.repository.GitHubRepository
import com.codeforge.builder.databinding.FragmentHomeBinding
import com.codeforge.builder.utils.hide
import com.codeforge.builder.utils.show
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

// ── ViewModel ─────────────────────────────────────────────────
class HomeViewModel(private val repo: GitHubRepository) : ViewModel() {

    val projects: LiveData<List<Project>> = repo.getAllProjects().asLiveData()

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            repo.deleteProject(project)
        }
    }
}

class HomeViewModelFactory(private val repo: GitHubRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return HomeViewModel(repo) as T
    }
}

// ── Fragment ───────────────────────────────────────────────────
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(GitHubRepository(requireContext()))
    }

    private lateinit var projectAdapter: ProjectAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        projectAdapter = ProjectAdapter(
            onItemClick = { project ->
                val action = HomeFragmentDirections.actionHomeToEditor(project.id)
                findNavController().navigate(action)
            },
            onDeleteClick = { project ->
                showDeleteDialog(project)
            }
        )
        binding.rvProjects.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = projectAdapter
        }
    }

    private fun setupObservers() {
        viewModel.projects.observe(viewLifecycleOwner) { projects ->
            projectAdapter.submitList(projects)
            if (projects.isEmpty()) {
                binding.layoutEmpty.show()
                binding.rvProjects.hide()
            } else {
                binding.layoutEmpty.hide()
                binding.rvProjects.show()
            }
        }
    }

    private fun setupClickListeners() {
        binding.fabNewProject.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_newProject)
        }
        binding.btnCreateFirst.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_newProject)
        }
    }

    private fun showDeleteDialog(project: Project) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Project")
            .setMessage("Delete \"${project.name}\"? This cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteProject(project)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

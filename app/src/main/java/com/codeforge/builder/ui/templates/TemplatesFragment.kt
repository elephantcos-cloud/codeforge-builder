package com.codeforge.builder.ui.templates

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codeforge.builder.data.local.entity.Project
import com.codeforge.builder.data.local.entity.ProjectFile
import com.codeforge.builder.data.repository.GitHubRepository
import com.codeforge.builder.databinding.FragmentTemplatesBinding
import com.codeforge.builder.databinding.ItemTemplateBinding
import com.codeforge.builder.utils.Constants
import com.codeforge.builder.utils.hide
import com.codeforge.builder.utils.show
import com.codeforge.builder.utils.showToast
import kotlinx.coroutines.launch

// ── Template Data Model ───────────────────────────────────────
data class AppTemplate(
    val id: Int,
    val name: String,
    val description: String,
    val type: String,
    val category: String,
    val colorHex: String = "#6200EE",
    val files: List<TemplateFile>
)

data class TemplateFile(
    val fileName: String,
    val language: String,
    val content: String,
    val isMain: Boolean = false
)

// ── ViewModel ─────────────────────────────────────────────────
class TemplatesViewModel(private val repo: GitHubRepository) : ViewModel() {

    val templates = MutableLiveData<List<AppTemplate>>(TemplateLibrary.getAllTemplates())
    val filteredTemplates = MutableLiveData<List<AppTemplate>>(TemplateLibrary.getAllTemplates())

    fun filterByCategory(category: String) {
        filteredTemplates.value = if (category == "All") {
            TemplateLibrary.getAllTemplates()
        } else {
            TemplateLibrary.getAllTemplates().filter { it.category == category }
        }
    }

    fun useTemplate(template: AppTemplate, projectName: String, packageName: String, onDone: (Long) -> Unit) {
        viewModelScope.launch {
            val project = Project(
                name = projectName,
                description = "Created from ${template.name} template",
                type = template.type,
                packageName = packageName,
                appName = projectName
            )
            val projectId = repo.insertProject(project)
            val files = template.files.map { tf ->
                ProjectFile(
                    projectId = projectId,
                    fileName = tf.fileName,
                    filePath = tf.fileName,
                    language = tf.language,
                    content = tf.content,
                    isMainFile = tf.isMain
                )
            }
            repo.insertFiles(files)
            onDone(projectId)
        }
    }
}

class TemplatesViewModelFactory(private val repo: GitHubRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return TemplatesViewModel(repo) as T
    }
}

// ── Fragment ───────────────────────────────────────────────────
class TemplatesFragment : Fragment() {

    private var _binding: FragmentTemplatesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TemplatesViewModel by viewModels {
        TemplatesViewModelFactory(GitHubRepository(requireContext()))
    }

    private lateinit var adapter: TemplateAdapter
    private val categories = listOf("All", "Starter", "Game", "Productivity", "Social", "Utility")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTemplatesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChips()
        setupRecyclerView()
        setupObservers()
    }

    private fun setupChips() {
        categories.forEach { cat ->
            val chip = com.google.android.material.chip.Chip(requireContext()).apply {
                text = cat
                isCheckable = true
                isChecked = cat == "All"
                setOnCheckedChangeListener { _, checked ->
                    if (checked) viewModel.filterByCategory(cat)
                }
            }
            binding.chipGroupCategories.addView(chip)
        }
    }

    private fun setupRecyclerView() {
        adapter = TemplateAdapter { template ->
            showUseTemplateDialog(template)
        }
        binding.rvTemplates.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvTemplates.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.filteredTemplates.observe(viewLifecycleOwner) { templates ->
            adapter.submitList(templates)
        }
    }

    private fun showUseTemplateDialog(template: AppTemplate) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(
            com.codeforge.builder.R.layout.dialog_use_template, null
        )
        val etName = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(
            com.codeforge.builder.R.id.et_project_name
        )
        val etPkg = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(
            com.codeforge.builder.R.id.et_package_name
        )
        etName?.setText(template.name)
        etPkg?.setText("com.myapp.${template.name.lowercase().replace(" ", "")}")

        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle("Use Template: ${template.name}")
            .setView(dialogView)
            .setPositiveButton("Create") { _, _ ->
                val name = etName?.text?.toString()?.trim() ?: template.name
                val pkg = etPkg?.text?.toString()?.trim() ?: "com.myapp.app"
                binding.progressUseTemplate.show()
                viewModel.useTemplate(template, name, pkg) { projectId ->
                    binding.progressUseTemplate.hide()
                    val action = TemplatesFragmentDirections.actionTemplatesToEditor(projectId)
                    findNavController().navigate(action)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// ── Adapter ────────────────────────────────────────────────────
class TemplateAdapter(
    private val onUse: (AppTemplate) -> Unit
) : ListAdapter<AppTemplate, TemplateAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val b: ItemTemplateBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(t: AppTemplate) {
            b.tvTemplateName.text = t.name
            b.tvTemplateDesc.text = t.description
            b.tvTemplateType.text = t.type
            b.tvCategory.text = t.category
            try {
                val color = android.graphics.Color.parseColor(t.colorHex)
                b.viewColorBar.setBackgroundColor(color)
                b.tvTemplateName.setTextColor(color)
            } catch (e: Exception) {}
            b.btnUseTemplate.setOnClickListener { onUse(t) }
            b.root.setOnClickListener { onUse(t) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val b = ItemTemplateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(b)
    }
    override fun onBindViewHolder(h: ViewHolder, pos: Int) = h.bind(getItem(pos))

    class DiffCallback : DiffUtil.ItemCallback<AppTemplate>() {
        override fun areItemsTheSame(a: AppTemplate, b: AppTemplate) = a.id == b.id
        override fun areContentsTheSame(a: AppTemplate, b: AppTemplate) = a == b
    }
}

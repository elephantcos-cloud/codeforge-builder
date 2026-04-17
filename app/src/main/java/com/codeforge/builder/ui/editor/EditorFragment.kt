package com.codeforge.builder.ui.editor

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.webkit.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.codeforge.builder.R
import com.codeforge.builder.data.local.entity.BuildRecord
import com.codeforge.builder.data.local.entity.Project
import com.codeforge.builder.data.local.entity.ProjectFile
import com.codeforge.builder.data.preferences.AppPreferences
import com.codeforge.builder.data.remote.model.ApiResult
import com.codeforge.builder.data.repository.GitHubRepository
import com.codeforge.builder.databinding.FragmentEditorBinding
import com.codeforge.builder.utils.*
import com.codeforge.builder.worker.BuildMonitorWorker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

// ── ViewModel ─────────────────────────────────────────────────
class EditorViewModel(
    private val repo: GitHubRepository,
    private val prefs: AppPreferences
) : ViewModel() {

    var currentProject: Project? = null
    var currentFile: ProjectFile? = null
    var pendingContent: String = ""

    val files = MutableLiveData<List<ProjectFile>>()
    val buildState = MutableLiveData<BuildState>()
    val errorMsg = MutableLiveData<String>()
    val successMsg = MutableLiveData<String>()

    sealed class BuildState {
        object Idle : BuildState()
        data class Pushing(val progress: Int, val total: Int) : BuildState()
        object WaitingForRun : BuildState()
        data class Building(val runId: Long) : BuildState()
        data class Done(val record: BuildRecord) : BuildState()
        data class Error(val msg: String) : BuildState()
    }

    fun loadProject(projectId: Long) {
        viewModelScope.launch {
            currentProject = repo.getProjectById(projectId)
            repo.getFilesForProject(projectId).onEach { list ->
                files.value = list
                if (currentFile == null && list.isNotEmpty()) {
                    currentFile = list.first { it.isMainFile } ?: list.first()
                }
            }.launchIn(this)
        }
    }

    fun saveCurrentFile(content: String, onSaved: () -> Unit) {
        val file = currentFile ?: return
        viewModelScope.launch {
            repo.updateFileContent(file.id, content)
            currentFile = file.copy(content = content, updatedAt = System.currentTimeMillis())
            repo.touchProject(currentProject?.id ?: return@launch)
            onSaved()
        }
    }

    fun addFile(fileName: String, language: String) {
        val project = currentProject ?: return
        viewModelScope.launch {
            val file = ProjectFile(
                projectId = project.id,
                fileName = fileName,
                filePath = fileName,
                language = language,
                content = getDefaultContent(fileName, language)
            )
            val id = repo.insertFile(file)
            currentFile = file.copy(id = id)
        }
    }

    fun deleteFile(file: ProjectFile) {
        viewModelScope.launch { repo.deleteFile(file) }
    }

    fun buildProject() {
        val project = currentProject ?: return
        viewModelScope.launch {
            try {
                buildState.value = BuildState.Pushing(0, 1)

                val token = prefs.githubToken.first()
                val username = prefs.githubUsername.first()
                if (token.isEmpty() || username.isEmpty()) {
                    buildState.value = BuildState.Error("Please configure GitHub token in Settings first")
                    return@launch
                }

                // Ensure repo exists
                val repoName = if (project.githubRepoName.isNotEmpty()) {
                    project.githubRepoName
                } else {
                    project.name.sanitizeRepoName()
                }

                // Create repo if needed
                if (!project.isGithubConnected) {
                    val repoResult = repo.createRepo(repoName)
                    if (repoResult is ApiResult.Error && repoResult.code != 422) {
                        buildState.value = BuildState.Error("Failed to create repo: ${repoResult.message}")
                        return@launch
                    }
                    val repoUrl = "https://github.com/$username/$repoName"
                    repo.updateGithubInfo(project.id, repoName, repoUrl)
                    currentProject = project.copy(githubRepoName = repoName, githubRepoUrl = "https://github.com/$username/$repoName", isGithubConnected = true)
                }

                // Generate files
                val userFiles = repo.getFilesSync(project.id)
                val updatedProject = currentProject!!.copy(githubRepoName = repoName)
                val filesToPush = ProjectGenerator.generateProjectFiles(updatedProject, userFiles)

                buildState.value = BuildState.Pushing(0, filesToPush.size)

                // Push files
                val pushResult = repo.pushFilesToRepo(
                    owner = username,
                    repoName = repoName,
                    files = filesToPush,
                    commitMessage = "Build: ${System.currentTimeMillis()}"
                )

                if (pushResult is ApiResult.Error) {
                    buildState.value = BuildState.Error("Push failed: ${pushResult.message}")
                    return@launch
                }

                buildState.value = BuildState.WaitingForRun
                delay(5000) // Wait for GitHub to register the workflow run

                // Get latest run
                val runResult = repo.getLatestWorkflowRun(username, repoName)
                if (runResult is ApiResult.Error) {
                    buildState.value = BuildState.Error("Could not get workflow run. Check GitHub Actions.")
                    return@launch
                }

                val run = (runResult as ApiResult.Success).data
                val buildRecord = BuildRecord(
                    projectId = project.id,
                    projectName = project.name,
                    githubRunId = run.id,
                    githubRunUrl = run.htmlUrl,
                    githubRepoOwner = username,
                    githubRepoName = repoName,
                    status = run.status,
                    buildNumber = run.runNumber,
                    commitMessage = run.headCommit?.message ?: ""
                )
                val buildId = repo.insertBuild(buildRecord)
                repo.updateBuildStatus(project.id, run.status, System.currentTimeMillis())

                buildState.value = BuildState.Building(run.id)

                // Start background worker to monitor
                BuildMonitorWorker.start(
                    context = CodeForgeApp.instance,
                    runId = run.id,
                    repoOwner = username,
                    repoName = repoName,
                    buildRecordId = buildId
                )

            } catch (e: Exception) {
                buildState.value = BuildState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun getDefaultContent(fileName: String, language: String): String = when (language) {
        "html" -> "<!DOCTYPE html>\n<html>\n<head><title>$fileName</title></head>\n<body>\n\n</body>\n</html>"
        "css" -> "/* $fileName styles */\n"
        "javascript" -> "// $fileName\n"
        "kotlin" -> "// $fileName\n"
        "java" -> "// $fileName\n"
        "xml" -> "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
        else -> ""
    }
}

class EditorViewModelFactory(
    private val repo: GitHubRepository,
    private val prefs: AppPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return EditorViewModel(repo, prefs) as T
    }
}

// ── Fragment ───────────────────────────────────────────────────
class EditorFragment : Fragment() {

    private var _binding: FragmentEditorBinding? = null
    private val binding get() = _binding!!
    private val args: EditorFragmentArgs by navArgs()

    private val viewModel: EditorViewModel by viewModels {
        EditorViewModelFactory(
            GitHubRepository(requireContext()),
            AppPreferences.getInstance(requireContext())
        )
    }

    private var editorWebView: WebView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditorBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEditorWebView()
        setupObservers()
        setupClickListeners()
        viewModel.loadProject(args.projectId)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupEditorWebView() {
        editorWebView = binding.webViewEditor
        editorWebView?.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccess = true
            }
            addJavascriptInterface(EditorBridge(), "EditorBridge")
            webViewClient = WebViewClient()
            loadUrl("file:///android_asset/editor/editor.html")
        }
    }

    private fun setupObservers() {
        viewModel.files.observe(viewLifecycleOwner) { files ->
            updateFileTabs(files)
        }

        viewModel.buildState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is EditorViewModel.BuildState.Idle -> hideBuildProgress()
                is EditorViewModel.BuildState.Pushing -> {
                    binding.buildStatusBar.show()
                    binding.tvBuildStatus.text = "Pushing files (${state.progress}/${state.total})..."
                    binding.buildProgressBar.isIndeterminate = true
                }
                is EditorViewModel.BuildState.WaitingForRun -> {
                    binding.buildStatusBar.show()
                    binding.tvBuildStatus.text = "Waiting for GitHub Actions..."
                    binding.buildProgressBar.isIndeterminate = true
                }
                is EditorViewModel.BuildState.Building -> {
                    binding.buildStatusBar.show()
                    binding.tvBuildStatus.text = "Building APK (Run #${state.runId})..."
                    binding.buildProgressBar.isIndeterminate = true
                }
                is EditorViewModel.BuildState.Done -> {
                    hideBuildProgress()
                    if (state.record.isSuccess) {
                        binding.root.showSnackbarWithAction("APK Ready!", "Download") {
                            // Navigate to builds
                            findNavController().navigate(R.id.buildsFragment)
                        }
                    } else {
                        binding.root.showSnackbar("Build ${state.record.conclusion}")
                    }
                }
                is EditorViewModel.BuildState.Error -> {
                    hideBuildProgress()
                    binding.root.showSnackbar(state.msg)
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.fabBuild.setOnClickListener { startBuild() }

        binding.btnAddFile.setOnClickListener { showAddFileDialog() }

        binding.btnSave.setOnClickListener {
            getEditorContent { content ->
                viewModel.saveCurrentFile(content) {
                    binding.root.showSnackbar("Saved!")
                }
            }
        }

        binding.btnFormatCode.setOnClickListener {
            editorWebView?.evaluateJavascript("window.editor.formatCode()", null)
        }

        binding.btnUndo.setOnClickListener {
            editorWebView?.evaluateJavascript("window.editor.undo()", null)
        }

        binding.btnRedo.setOnClickListener {
            editorWebView?.evaluateJavascript("window.editor.redo()", null)
        }
    }

    private fun updateFileTabs(files: List<ProjectFile>) {
        binding.tabFiles.removeAllTabs()
        files.forEach { file ->
            binding.tabFiles.addTab(
                binding.tabFiles.newTab().setText(file.fileName)
            )
        }
        binding.tabFiles.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val file = files.getOrNull(tab.position) ?: return
                viewModel.currentFile = file
                loadFileInEditor(file)
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // Select main file
        val mainIndex = files.indexOfFirst { it.isMainFile }.coerceAtLeast(0)
        if (files.isNotEmpty()) {
            binding.tabFiles.selectTab(binding.tabFiles.getTabAt(mainIndex))
            loadFileInEditor(files[mainIndex])
        }
    }

    private fun loadFileInEditor(file: ProjectFile) {
        val escapedContent = file.content
            .replace("\\", "\\\\")
            .replace("`", "\\`")
            .replace("$", "\\$")
        editorWebView?.evaluateJavascript(
            "window.editor.setValue(`$escapedContent`, '${file.language}')",
            null
        )
    }

    private fun getEditorContent(callback: (String) -> Unit) {
        editorWebView?.evaluateJavascript("window.editor.getValue()") { result ->
            // Result comes back as JSON string (with surrounding quotes)
            val content = result?.removeSurrounding("\"")
                ?.replace("\\n", "\n")
                ?.replace("\\t", "\t")
                ?.replace("\\\"", "\"")
                ?.replace("\\\\", "\\")
                ?: ""
            callback(content)
        }
    }

    private fun startBuild() {
        // Save current file first, then build
        getEditorContent { content ->
            viewModel.saveCurrentFile(content) {
                viewModel.buildProject()
            }
        }
    }

    private fun showAddFileDialog() {
        val project = viewModel.currentProject ?: return
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_file, null)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add File")
            .setView(view)
            .setPositiveButton("Add") { _, _ ->
                val nameInput = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etFileName)
                val fileName = nameInput?.text?.toString()?.trim() ?: return@setPositiveButton
                if (fileName.isNotEmpty()) {
                    val lang = fileName.substringAfterLast('.', "")
                    viewModel.addFile(fileName, lang)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun hideBuildProgress() {
        binding.buildStatusBar.hide()
    }

    // JavaScript Bridge for the editor WebView
    inner class EditorBridge {
        @JavascriptInterface
        fun onContentChanged(content: String) {
            viewModel.pendingContent = content
        }

        @JavascriptInterface
        fun log(msg: String) {
            android.util.Log.d("Editor", msg)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

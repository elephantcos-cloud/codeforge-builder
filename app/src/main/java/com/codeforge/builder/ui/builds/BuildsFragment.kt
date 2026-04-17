package com.codeforge.builder.ui.builds

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codeforge.builder.data.local.entity.BuildRecord
import com.codeforge.builder.data.preferences.AppPreferences
import com.codeforge.builder.data.remote.model.ApiResult
import com.codeforge.builder.data.repository.GitHubRepository
import com.codeforge.builder.databinding.FragmentBuildsBinding
import com.codeforge.builder.databinding.ItemBuildBinding
import com.codeforge.builder.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

// ── ViewModel ─────────────────────────────────────────────────
class BuildsViewModel(
    private val repo: GitHubRepository,
    private val prefs: AppPreferences
) : ViewModel() {

    val builds: LiveData<List<BuildRecord>> = repo.getAllBuilds().asLiveData()
    val downloadState = MutableLiveData<DownloadState>()

    sealed class DownloadState {
        object Idle : DownloadState()
        data class Downloading(val buildId: Long) : DownloadState()
        data class Done(val file: File) : DownloadState()
        data class Error(val msg: String) : DownloadState()
    }

    fun downloadApk(build: BuildRecord) {
        viewModelScope.launch {
            if (build.apkLocalPath.isNotEmpty() && File(build.apkLocalPath).exists()) {
                downloadState.value = DownloadState.Done(File(build.apkLocalPath))
                return@launch
            }
            if (build.apkDownloadUrl.isEmpty()) {
                downloadState.value = DownloadState.Error("No download URL available")
                return@launch
            }

            downloadState.value = DownloadState.Downloading(build.id)
            try {
                val token = prefs.githubToken.first()
                val destDir = File(CodeForgeApp.instance.cacheDir, "apks").also { it.mkdirs() }
                val destFile = File(destDir, "${build.projectName}_${build.id}.zip")

                val result = repo.downloadArtifact(build.apkDownloadUrl, destFile)
                if (result is ApiResult.Success) {
                    repo.updateApkInfo(build.id, build.apkDownloadUrl, destFile.absolutePath, destFile.length())
                    downloadState.value = DownloadState.Done(result.data)
                } else {
                    downloadState.value = DownloadState.Error("Download failed")
                }
            } catch (e: Exception) {
                downloadState.value = DownloadState.Error(e.message ?: "Download error")
            }
        }
    }

    fun openInBrowser(url: String) {
        CodeForgeApp.instance.openUrl(url)
    }

    fun deleteOldBuilds() {
        viewModelScope.launch { repo.pruneOldBuilds() }
    }
}

class BuildsViewModelFactory(
    private val repo: GitHubRepository,
    private val prefs: AppPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return BuildsViewModel(repo, prefs) as T
    }
}

// ── Fragment ───────────────────────────────────────────────────
class BuildsFragment : Fragment() {

    private var _binding: FragmentBuildsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BuildsViewModel by viewModels {
        BuildsViewModelFactory(
            GitHubRepository(requireContext()),
            AppPreferences.getInstance(requireContext())
        )
    }

    private lateinit var adapter: BuildAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBuildsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        binding.swipeRefresh.setOnRefreshListener { binding.swipeRefresh.isRefreshing = false }
    }

    private fun setupRecyclerView() {
        adapter = BuildAdapter(
            onDownload = { build -> viewModel.downloadApk(build) },
            onOpenGitHub = { build -> viewModel.openInBrowser(build.githubRunUrl) },
            onPreview = { build ->
                if (build.apkLocalPath.isNotEmpty()) {
                    val action = BuildsFragmentDirections.actionBuildsToPreview(build.apkLocalPath)
                    // navigate handled after download
                }
            }
        )
        binding.rvBuilds.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBuilds.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.builds.observe(viewLifecycleOwner) { builds ->
            adapter.submitList(builds)
            if (builds.isEmpty()) {
                binding.layoutEmpty.show()
                binding.rvBuilds.hide()
            } else {
                binding.layoutEmpty.hide()
                binding.rvBuilds.show()
            }
        }

        viewModel.downloadState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is BuildsViewModel.DownloadState.Idle -> {}
                is BuildsViewModel.DownloadState.Downloading -> {
                    binding.root.showSnackbar("Downloading APK...")
                }
                is BuildsViewModel.DownloadState.Done -> {
                    binding.root.showSnackbarWithAction("Downloaded!", "Open") {
                        installApk(state.file)
                    }
                }
                is BuildsViewModel.DownloadState.Error -> {
                    binding.root.showSnackbar(state.msg)
                }
            }
        }
    }

    private fun installApk(file: File) {
        try {
            val uri = androidx.core.content.FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                file
            )
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (e: Exception) {
            binding.root.showSnackbar("Cannot open APK: ${e.message}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// ── Adapter ────────────────────────────────────────────────────
class BuildAdapter(
    private val onDownload: (BuildRecord) -> Unit,
    private val onOpenGitHub: (BuildRecord) -> Unit,
    private val onPreview: (BuildRecord) -> Unit
) : ListAdapter<BuildRecord, BuildAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val b: ItemBuildBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(build: BuildRecord) {
            b.tvProjectName.text = build.projectName
            b.tvBuildNumber.text = "#${build.buildNumber}"
            b.tvCommitMsg.text = build.commitMessage.ifEmpty { "Manual build" }
            b.tvBuildTime.text = build.startedAt.toRelativeTime()
            b.tvRepoName.text = "${build.githubRepoOwner}/${build.githubRepoName}"

            val statusText = when {
                build.isCompleted && build.isSuccess -> "SUCCESS"
                build.isCompleted -> build.conclusion.uppercase()
                build.status == "in_progress" -> "BUILDING"
                else -> "QUEUED"
            }
            b.tvStatus.text = statusText
            b.tvStatus.setTextColor(
                if (build.isCompleted) {
                    if (build.isSuccess) 0xFF4CAF50.toInt() else 0xFFF44336.toInt()
                } else 0xFF2196F3.toInt()
            )

            if (build.durationMs > 0) {
                val mins = build.durationMs / 60000
                val secs = (build.durationMs % 60000) / 1000
                b.tvDuration.text = if (mins > 0) "${mins}m ${secs}s" else "${secs}s"
                b.tvDuration.show()
            } else {
                b.tvDuration.hide()
            }

            if (build.apkSize > 0) {
                b.tvApkSize.text = build.apkSize.toReadableSize()
                b.tvApkSize.show()
            } else b.tvApkSize.hide()

            b.btnDownload.isEnabled = build.isSuccess
            b.btnDownload.alpha = if (build.isSuccess) 1f else 0.4f
            b.btnDownload.setOnClickListener { onDownload(build) }
            b.btnOpenGithub.setOnClickListener { onOpenGitHub(build) }
            b.btnPreview.isEnabled = build.hasApk
            b.btnPreview.setOnClickListener { onPreview(build) }

            // Progress bar for running builds
            if (build.isRunning) b.buildProgress.show() else b.buildProgress.hide()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val b = ItemBuildBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(b)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<BuildRecord>() {
        override fun areItemsTheSame(a: BuildRecord, b: BuildRecord) = a.id == b.id
        override fun areContentsTheSame(a: BuildRecord, b: BuildRecord) = a == b
    }
}

// make CodeForgeApp accessible in this file
private val CodeForgeApp get() = com.codeforge.builder.CodeForgeApp.instance
private fun com.codeforge.builder.CodeForgeApp.openUrl(url: String) {
    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

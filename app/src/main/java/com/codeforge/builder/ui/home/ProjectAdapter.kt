package com.codeforge.builder.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codeforge.builder.data.local.entity.Project
import com.codeforge.builder.databinding.ItemProjectBinding
import com.codeforge.builder.utils.toBuildStatusColor
import com.codeforge.builder.utils.toRelativeTime

class ProjectAdapter(
    private val onItemClick: (Project) -> Unit,
    private val onDeleteClick: (Project) -> Unit
) : ListAdapter<Project, ProjectAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val b: ItemProjectBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(project: Project) {
            b.tvProjectName.text = project.name
            b.tvProjectType.text = project.type
            b.tvPackageName.text = project.packageName
            b.tvUpdatedAt.text = project.updatedAt.toRelativeTime()

            // Build status badge
            if (project.lastBuildStatus.isNotEmpty()) {
                b.tvBuildStatus.text = project.lastBuildStatus.uppercase()
                b.tvBuildStatus.setTextColor(
                    project.lastBuildStatus.toBuildStatusColor(b.root.context)
                )
            } else {
                b.tvBuildStatus.text = "NOT BUILT"
            }

            // GitHub connected badge
            b.imgGithub.alpha = if (project.isGithubConnected) 1f else 0.3f

            b.root.setOnClickListener { onItemClick(project) }
            b.btnDelete.setOnClickListener { onDeleteClick(project) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Project>() {
        override fun areItemsTheSame(a: Project, b: Project) = a.id == b.id
        override fun areContentsTheSame(a: Project, b: Project) = a == b
    }
}

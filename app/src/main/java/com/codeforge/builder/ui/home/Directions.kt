package com.codeforge.builder.ui.home

import androidx.navigation.NavDirections
import com.codeforge.builder.R

// ── HomeFragment directions ───────────────────────
object HomeFragmentDirections {
    fun actionHomeToEditor(projectId: Long): NavDirections =
        object : NavDirections {
            override val actionId = R.id.action_home_to_editor
            override val arguments = androidx.core.os.bundleOf("projectId" to projectId)
        }
}

// ── NewProjectFragment directions ─────────────────
object NewProjectFragmentDirections {
    fun actionNewProjectToEditor(projectId: Long): NavDirections =
        object : NavDirections {
            override val actionId = R.id.action_newProject_to_editor
            override val arguments = androidx.core.os.bundleOf("projectId" to projectId)
        }
}

package com.codeforge.builder.ui.templates

import androidx.navigation.NavDirections
import com.codeforge.builder.R

object TemplatesFragmentDirections {
    fun actionTemplatesToEditor(projectId: Long): NavDirections =
        object : NavDirections {
            override val actionId = R.id.action_templates_to_editor
            override val arguments = androidx.core.os.bundleOf("projectId" to projectId)
        }
}

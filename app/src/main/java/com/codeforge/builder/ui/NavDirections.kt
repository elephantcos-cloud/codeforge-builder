package com.codeforge.builder.ui.builds

import android.os.Bundle
import androidx.navigation.NavDirections
import com.codeforge.builder.R

object BuildsFragmentDirections {
    fun actionBuildsToPreview(apkPath: String): NavDirections =
        object : NavDirections {
            override val actionId = R.id.action_builds_to_preview
            override val arguments = androidx.core.os.bundleOf("apkPath" to apkPath)
        }
}

// ─────────────────────────────────────────────────
package com.codeforge.builder.ui.preview

import android.os.Bundle

class ApkPreviewFragmentArgs(val apkPath: String) {
    companion object {
        fun fromBundle(bundle: Bundle): ApkPreviewFragmentArgs {
            return ApkPreviewFragmentArgs(bundle.getString("apkPath") ?: "")
        }
    }
}

fun androidx.fragment.app.Fragment.navArgs(): Lazy<ApkPreviewFragmentArgs> = lazy {
    ApkPreviewFragmentArgs.fromBundle(requireArguments())
}

// ─────────────────────────────────────────────────
package com.codeforge.builder.ui.templates

import android.os.Bundle
import androidx.navigation.NavDirections
import com.codeforge.builder.R

object TemplatesFragmentDirections {
    fun actionTemplatesToEditor(projectId: Long): NavDirections =
        object : NavDirections {
            override val actionId = R.id.action_templates_to_editor
            override val arguments = androidx.core.os.bundleOf("projectId" to projectId)
        }
}

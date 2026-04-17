package com.codeforge.builder.ui.builds

import androidx.navigation.NavDirections
import com.codeforge.builder.R

object BuildsFragmentDirections {
    fun actionBuildsToPreview(apkPath: String): NavDirections =
        object : NavDirections {
            override val actionId = R.id.action_builds_to_preview
            override val arguments = androidx.core.os.bundleOf("apkPath" to apkPath)
        }
}

package com.codeforge.builder.ui.preview

import android.os.Bundle
import androidx.fragment.app.Fragment

class ApkPreviewFragmentArgs(val apkPath: String) {
    companion object {
        fun fromBundle(bundle: Bundle): ApkPreviewFragmentArgs {
            return ApkPreviewFragmentArgs(bundle.getString("apkPath") ?: "")
        }
    }
}

fun Fragment.navArgs(): Lazy<ApkPreviewFragmentArgs> = lazy {
    ApkPreviewFragmentArgs.fromBundle(requireArguments())
}

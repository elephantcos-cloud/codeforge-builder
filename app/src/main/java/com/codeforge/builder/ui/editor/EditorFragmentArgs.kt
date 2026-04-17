package com.codeforge.builder.ui.editor

import android.os.Bundle
import androidx.navigation.NavDirections
import com.codeforge.builder.R

// ── Args ─────────────────────────────────────────
class EditorFragmentArgs(val projectId: Long) {
    companion object {
        fun fromBundle(bundle: Bundle): EditorFragmentArgs {
            return EditorFragmentArgs(bundle.getLong("projectId", -1L))
        }
    }
}

// This replaces the SafeArgs generated class
fun androidx.fragment.app.Fragment.navArgs(): Lazy<EditorFragmentArgs> = lazy {
    EditorFragmentArgs.fromBundle(requireArguments())
}

// ── Directions ────────────────────────────────────
object EditorFragmentDirections {
    fun actionEditorToBuilds(): NavDirections =
        object : NavDirections {
            override val actionId = R.id.buildsFragment
            override val arguments = Bundle.EMPTY
        }
}

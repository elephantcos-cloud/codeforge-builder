package com.codeforge.builder.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

// View Extensions
fun View.show() { visibility = View.VISIBLE }
fun View.hide() { visibility = View.GONE }
fun View.invisible() { visibility = View.INVISIBLE }
fun View.isVisible() = visibility == View.VISIBLE

fun View.showSnackbar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, message, duration).show()
}

fun View.showSnackbarWithAction(
    message: String,
    actionText: String,
    action: () -> Unit
) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG)
        .setAction(actionText) { action() }
        .show()
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

// Context Extensions
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.copyToClipboard(label: String, text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
}

fun Context.openUrl(url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    } catch (e: Exception) {
        showToast("Cannot open URL: $url")
    }
}

// Fragment Extensions
fun Fragment.showToast(message: String) {
    requireContext().showToast(message)
}

// String Extensions
fun String.toBase64(): String {
    return android.util.Base64.encodeToString(toByteArray(Charsets.UTF_8), android.util.Base64.NO_WRAP)
}

fun String.fromBase64(): String {
    return String(android.util.Base64.decode(this, android.util.Base64.NO_WRAP), Charsets.UTF_8)
}

fun String.sanitizeRepoName(): String {
    return replace(Regex("[^a-zA-Z0-9._-]"), "-")
        .replace(Regex("-+"), "-")
        .trim('-')
        .lowercase()
        .take(100)
}

fun String.sanitizePackageName(): String {
    return replace(Regex("[^a-zA-Z0-9.]"), "_")
        .split(".")
        .filter { it.isNotEmpty() }
        .joinToString(".")
        .lowercase()
}

fun String.getFileExtension(): String {
    return if (contains('.')) substringAfterLast('.', "") else ""
}

fun String.getFileNameWithoutExtension(): String {
    return if (contains('.')) substringBeforeLast('.') else this
}

fun String.isValidGitHubToken(): Boolean {
    return matches(Regex("(ghp|ghs|gho|ghr|github_pat)_[a-zA-Z0-9_]{36,}"))
        || matches(Regex("[a-zA-Z0-9_]{40}")) // classic token
}

fun String.isValidGitHubUsername(): Boolean {
    return matches(Regex("[a-zA-Z0-9]([a-zA-Z0-9-]{0,37}[a-zA-Z0-9])?"))
}

fun String.isValidRepoName(): Boolean {
    return matches(Regex("[a-zA-Z0-9._-]{1,100}"))
}

// Date Extensions
fun Long.toFormattedDate(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(this))
}

fun Long.toRelativeTime(): String {
    val now = System.currentTimeMillis()
    val diff = now - this
    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "${diff / 60_000}m ago"
        diff < 86_400_000 -> "${diff / 3_600_000}h ago"
        diff < 604_800_000 -> "${diff / 86_400_000}d ago"
        else -> toFormattedDate()
    }
}

// Number Extensions
fun Long.toReadableSize(): String {
    return when {
        this < 1024 -> "$this B"
        this < 1024 * 1024 -> "${this / 1024} KB"
        else -> String.format("%.1f MB", this / (1024.0 * 1024.0))
    }
}

// Build Status Color Helper
fun String.toBuildStatusColor(context: Context): Int {
    return when (this) {
        "success" -> ContextCompat.getColor(context, android.R.color.holo_green_dark)
        "failure" -> ContextCompat.getColor(context, android.R.color.holo_red_dark)
        "in_progress", "queued" -> ContextCompat.getColor(context, android.R.color.holo_blue_dark)
        "cancelled" -> ContextCompat.getColor(context, android.R.color.darker_gray)
        else -> ContextCompat.getColor(context, android.R.color.darker_gray)
    }
}

fun String.toBuildStatusEmoji(): String {
    return when (this) {
        "success" -> "✅"
        "failure" -> "❌"
        "in_progress" -> "⏳"
        "queued" -> "🕐"
        "cancelled" -> "⛔"
        else -> "❓"
    }
}

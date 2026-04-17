package com.codeforge.builder.ui.preview

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.codeforge.builder.databinding.FragmentApkPreviewBinding
import com.codeforge.builder.utils.hide
import com.codeforge.builder.utils.show
import com.codeforge.builder.utils.toReadableSize
import java.io.File
import java.util.zip.ZipFile

class ApkPreviewFragment : Fragment() {

    private var _binding: FragmentApkPreviewBinding? = null
    private val binding get() = _binding!!
    private val args: ApkPreviewFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApkPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val apkPath = args.apkPath
        if (apkPath.isNotEmpty()) {
            loadApkInfo(apkPath)
        } else {
            binding.tvApkPath.text = "No APK selected"
        }
    }

    private fun loadApkInfo(path: String) {
        val file = File(path)
        if (!file.exists()) {
            binding.tvApkPath.text = "File not found: $path"
            return
        }

        binding.tvApkPath.text = file.name
        binding.tvApkSize.text = file.length().toReadableSize()

        // Try to read as ZIP (APK is a ZIP)
        try {
            val zip = ZipFile(file)
            val entries = zip.entries().toList()
            binding.tvFileCount.text = "${entries.size} files"
            val dexFiles = entries.filter { it.name.endsWith(".dex") }.size
            binding.tvDexCount.text = "$dexFiles DEX files"
            zip.close()
        } catch (e: Exception) {
            binding.tvFileCount.text = "Cannot read ZIP"
        }

        // Install button
        binding.btnInstall.setOnClickListener {
            installApk(file)
        }
    }

    private fun installApk(file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (e: Exception) {
            binding.tvApkPath.text = "Install error: ${e.message}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Standalone activity for APK preview (from notifications)
class ApkPreviewActivity : androidx.appcompat.app.AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Just navigate to the preview fragment via nav controller
        finish()
    }
}

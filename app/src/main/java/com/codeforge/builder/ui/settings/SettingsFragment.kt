package com.codeforge.builder.ui.settings

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import com.codeforge.builder.data.preferences.AppPreferences
import com.codeforge.builder.data.remote.model.ApiResult
import com.codeforge.builder.data.repository.GitHubRepository
import com.codeforge.builder.databinding.FragmentSettingsBinding
import com.codeforge.builder.utils.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repo: GitHubRepository,
    private val prefs: AppPreferences
) : ViewModel() {

    val githubToken = prefs.githubToken.asLiveData()
    val githubUsername = prefs.githubUsername.asLiveData()
    val githubEmail = prefs.githubEmail.asLiveData()
    val themeMode = prefs.themeMode.asLiveData()
    val editorFontSize = prefs.editorFontSize.asLiveData()
    val editorTheme = prefs.editorTheme.asLiveData()

    val userValidationState = MutableLiveData<ValidationState>()

    sealed class ValidationState {
        object Idle : ValidationState()
        object Loading : ValidationState()
        data class Success(val username: String, val name: String?) : ValidationState()
        data class Error(val msg: String) : ValidationState()
    }

    fun saveToken(token: String) { viewModelScope.launch { prefs.setGithubToken(token) } }
    fun saveUsername(username: String) { viewModelScope.launch { prefs.setGithubUsername(username) } }
    fun saveEmail(email: String) { viewModelScope.launch { prefs.setGithubEmail(email) } }
    fun saveTheme(mode: Int) { viewModelScope.launch { prefs.setThemeMode(mode) } }
    fun saveFontSize(size: Int) { viewModelScope.launch { prefs.setEditorFontSize(size) } }
    fun saveEditorTheme(theme: String) { viewModelScope.launch { prefs.setEditorTheme(theme) } }

    fun validateToken() {
        viewModelScope.launch {
            val token = prefs.githubToken.first()
            if (token.isEmpty()) { userValidationState.value = ValidationState.Error("Enter GitHub token first"); return@launch }
            userValidationState.value = ValidationState.Loading
            val result = repo.getAuthenticatedUser()
            if (result is ApiResult.Success) {
                prefs.setGithubUsername(result.data.login)
                result.data.email?.let { if (it.isNotEmpty()) prefs.setGithubEmail(it) }
                userValidationState.value = ValidationState.Success(result.data.login, result.data.name)
            } else {
                userValidationState.value = ValidationState.Error("Invalid token or no network")
            }
        }
    }

    fun clearAll() {
        viewModelScope.launch { prefs.clearAll() }
    }
}

class SettingsViewModelFactory(
    private val repo: GitHubRepository,
    private val prefs: AppPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SettingsViewModel(repo, prefs) as T
    }
}

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(
            GitHubRepository(requireContext()),
            AppPreferences.getInstance(requireContext())
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.githubToken.observe(viewLifecycleOwner) { token ->
            if (token.isNotEmpty() && binding.etToken.text.isNullOrEmpty()) {
                binding.etToken.setText(token)
            }
        }
        viewModel.githubUsername.observe(viewLifecycleOwner) { username ->
            binding.tvGithubUser.text = if (username.isNotEmpty()) "Logged in as: @$username" else "Not connected"
        }
        viewModel.githubEmail.observe(viewLifecycleOwner) { email ->
            if (email.isNotEmpty() && binding.etEmail.text.isNullOrEmpty()) {
                binding.etEmail.setText(email)
            }
        }
        viewModel.editorFontSize.observe(viewLifecycleOwner) { size ->
            binding.sliderFontSize.value = size.toFloat()
            binding.tvFontSizeValue.text = "${size}sp"
        }
        viewModel.editorTheme.observe(viewLifecycleOwner) { theme ->
            binding.rgEditorTheme.check(
                when (theme) {
                    Constants.EDITOR_THEME_LIGHT -> com.codeforge.builder.R.id.rbLight
                    Constants.EDITOR_THEME_MONOKAI -> com.codeforge.builder.R.id.rbMonokai
                    else -> com.codeforge.builder.R.id.rbDark
                }
            )
        }
        viewModel.themeMode.observe(viewLifecycleOwner) { mode ->
            binding.rgTheme.check(
                when (mode) {
                    1 -> com.codeforge.builder.R.id.rbLight2
                    2 -> com.codeforge.builder.R.id.rbDark2
                    else -> com.codeforge.builder.R.id.rbSystem
                }
            )
        }
        viewModel.userValidationState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SettingsViewModel.ValidationState.Idle -> {
                    binding.progressValidate.hide()
                    binding.btnValidateToken.isEnabled = true
                }
                is SettingsViewModel.ValidationState.Loading -> {
                    binding.progressValidate.show()
                    binding.btnValidateToken.isEnabled = false
                }
                is SettingsViewModel.ValidationState.Success -> {
                    binding.progressValidate.hide()
                    binding.btnValidateToken.isEnabled = true
                    val name = state.name ?: state.username
                    binding.root.showSnackbar("✅ Connected as $name (@${state.username})")
                }
                is SettingsViewModel.ValidationState.Error -> {
                    binding.progressValidate.hide()
                    binding.btnValidateToken.isEnabled = true
                    binding.root.showSnackbar("❌ ${state.msg}")
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnSaveToken.setOnClickListener {
            val token = binding.etToken.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            if (token.isEmpty()) { showToast("Token cannot be empty"); return@setOnClickListener }
            if (!token.isValidGitHubToken()) {
                binding.root.showSnackbar("Token format looks incorrect. Verify on GitHub.")
            }
            viewModel.saveToken(token)
            if (email.isNotEmpty()) viewModel.saveEmail(email)
            hideKeyboard()
            binding.root.showSnackbar("Saved! Tap 'Validate' to test connection.")
        }

        binding.btnValidateToken.setOnClickListener {
            hideKeyboard()
            viewModel.validateToken()
        }

        binding.btnClearAll.setOnClickListener {
            com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Clear All Data")
                .setMessage("This will remove all tokens and preferences. Are you sure?")
                .setPositiveButton("Clear") { _, _ ->
                    viewModel.clearAll()
                    binding.etToken.text?.clear()
                    binding.etEmail.text?.clear()
                    showToast("Data cleared")
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        binding.btnOpenGithubTokens.setOnClickListener {
            requireContext().openUrl("https://github.com/settings/tokens/new?scopes=repo,workflow&description=CodeForge")
        }

        binding.sliderFontSize.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                binding.tvFontSizeValue.text = "${value.toInt()}sp"
                viewModel.saveFontSize(value.toInt())
            }
        }

        binding.rgEditorTheme.setOnCheckedChangeListener { _, id ->
            val theme = when (id) {
                com.codeforge.builder.R.id.rbLight -> Constants.EDITOR_THEME_LIGHT
                com.codeforge.builder.R.id.rbMonokai -> Constants.EDITOR_THEME_MONOKAI
                else -> Constants.EDITOR_THEME_DARK
            }
            viewModel.saveEditorTheme(theme)
        }

        binding.rgTheme.setOnCheckedChangeListener { _, id ->
            val mode = when (id) {
                com.codeforge.builder.R.id.rbLight2 -> 1
                com.codeforge.builder.R.id.rbDark2 -> 2
                else -> 0
            }
            viewModel.saveTheme(mode)
            AppCompatDelegate.setDefaultNightMode(
                when (mode) {
                    1 -> AppCompatDelegate.MODE_NIGHT_NO
                    2 -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
            )
        }
    }

    private fun hideKeyboard() {
        binding.root.hideKeyboard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.codeforge.builder.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.codeforge.builder.R
import com.codeforge.builder.data.local.entity.Project
import com.codeforge.builder.data.local.entity.ProjectFile
import com.codeforge.builder.data.repository.GitHubRepository
import com.codeforge.builder.databinding.FragmentNewProjectBinding
import com.codeforge.builder.utils.Constants
import com.codeforge.builder.utils.hide
import com.codeforge.builder.utils.sanitizePackageName
import com.codeforge.builder.utils.show
import com.codeforge.builder.utils.showToast
import kotlinx.coroutines.launch

class NewProjectViewModel(private val repo: GitHubRepository) : ViewModel() {
    var createdProjectId: Long = -1L

    fun createProject(
        name: String,
        description: String,
        type: String,
        packageName: String,
        appName: String,
        onDone: (Long) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (name.isBlank()) { onError("Project name is required"); return@launch }
            if (packageName.isBlank()) { onError("Package name is required"); return@launch }

            val project = Project(
                name = name.trim(),
                description = description.trim(),
                type = type,
                packageName = packageName.trim(),
                appName = appName.trim().ifEmpty { name.trim() }
            )
            val projectId = repo.insertProject(project)

            // Insert default starter files
            val starterFiles = getStarterFiles(projectId, type)
            repo.insertFiles(starterFiles)

            onDone(projectId)
        }
    }

    private fun getStarterFiles(projectId: Long, type: String): List<ProjectFile> {
        return when (type) {
            Constants.PROJECT_TYPE_HTML -> listOf(
                ProjectFile(projectId = projectId, fileName = "index.html",
                    filePath = "assets/index.html", language = "html", isMainFile = true,
                    content = DEFAULT_HTML),
                ProjectFile(projectId = projectId, fileName = "style.css",
                    filePath = "assets/style.css", language = "css",
                    content = DEFAULT_CSS),
                ProjectFile(projectId = projectId, fileName = "app.js",
                    filePath = "assets/app.js", language = "javascript",
                    content = DEFAULT_JS)
            )
            Constants.PROJECT_TYPE_KOTLIN -> listOf(
                ProjectFile(projectId = projectId, fileName = "MainActivity.kt",
                    filePath = "MainActivity.kt", language = "kotlin", isMainFile = true,
                    content = DEFAULT_KOTLIN_ACTIVITY)
            )
            Constants.PROJECT_TYPE_JAVA -> listOf(
                ProjectFile(projectId = projectId, fileName = "MainActivity.java",
                    filePath = "MainActivity.java", language = "java", isMainFile = true,
                    content = DEFAULT_JAVA_ACTIVITY)
            )
            else -> emptyList()
        }
    }
}

class NewProjectViewModelFactory(private val repo: GitHubRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return NewProjectViewModel(repo) as T
    }
}

class NewProjectFragment : Fragment() {

    private var _binding: FragmentNewProjectBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewProjectViewModel by viewModels {
        NewProjectViewModelFactory(GitHubRepository(requireContext()))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNewProjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Auto-fill package name from app name
        binding.etAppName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && binding.etPackageName.text.isNullOrBlank()) {
                val appName = binding.etAppName.text.toString().trim()
                if (appName.isNotEmpty()) {
                    val pkg = "com.myapp.${appName.lowercase().replace(" ", "")}".sanitizePackageName()
                    binding.etPackageName.setText(pkg)
                }
            }
        }

        binding.btnCreate.setOnClickListener { createProject() }
        binding.btnCancel.setOnClickListener { findNavController().popBackStack() }
    }

    private fun createProject() {
        val name = binding.etProjectName.text.toString()
        val appName = binding.etAppName.text.toString()
        val description = binding.etDescription.text.toString()
        val packageName = binding.etPackageName.text.toString()
        val type = when (binding.rgProjectType.checkedRadioButtonId) {
            R.id.rbHtml -> Constants.PROJECT_TYPE_HTML
            R.id.rbKotlin -> Constants.PROJECT_TYPE_KOTLIN
            R.id.rbJava -> Constants.PROJECT_TYPE_JAVA
            else -> Constants.PROJECT_TYPE_HTML
        }

        binding.btnCreate.isEnabled = false
        binding.progressCreate.show()

        viewModel.createProject(
            name = name,
            description = description,
            type = type,
            packageName = packageName,
            appName = appName,
            onDone = { projectId ->
                binding.btnCreate.isEnabled = true
                binding.progressCreate.hide()
                val action = NewProjectFragmentDirections.actionNewProjectToEditor(projectId)
                findNavController().navigate(action)
            },
            onError = { error ->
                binding.btnCreate.isEnabled = true
                binding.progressCreate.hide()
                showToast(error)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// ── Default starter code ──────────────────────────────────────
private const val DEFAULT_HTML = """<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
<title>My App</title>
<link rel="stylesheet" href="style.css">
</head>
<body>
  <div class="container">
    <h1>Hello, World!</h1>
    <p>Start editing to build your app</p>
    <button onclick="greet()">Click Me</button>
    <div id="output"></div>
  </div>
  <script src="app.js"></script>
</body>
</html>"""

private const val DEFAULT_CSS = """* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Segoe UI', sans-serif;
  background: #1a1a2e;
  color: #eee;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 24px;
}

.container {
  text-align: center;
  max-width: 400px;
  width: 100%;
}

h1 {
  font-size: 2rem;
  color: #e94560;
  margin-bottom: 12px;
}

p {
  color: #aaa;
  margin-bottom: 24px;
}

button {
  background: #e94560;
  color: white;
  border: none;
  padding: 12px 32px;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: opacity 0.2s;
}

button:hover { opacity: 0.85; }

#output {
  margin-top: 20px;
  font-size: 1.1rem;
  color: #e94560;
}"""

private const val DEFAULT_JS = """function greet() {
  const messages = [
    'Hello from CodeForge!',
    'You clicked it!',
    'Keep building!'
  ];
  const msg = messages[Math.floor(Math.random() * messages.length)];
  document.getElementById('output').textContent = msg;
}

// App initialized
console.log('App loaded successfully!');"""

private const val DEFAULT_KOTLIN_ACTIVITY = """package your.package.name

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(0xFF1a1a2e.toInt())
            setPadding(48, 48, 48, 48)
        }

        val title = TextView(this).apply {
            text = "Hello, World!"
            textSize = 28f
            setTextColor(0xFFe94560.toInt())
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 24)
        }

        val btn = MaterialButton(this).apply {
            text = "Click Me"
            setOnClickListener { title.text = "You tapped the button!" }
        }

        layout.addView(title)
        layout.addView(btn)
        setContentView(layout)
    }
}"""

private const val DEFAULT_JAVA_ACTIVITY = """package your.package.name;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(0xFF1a1a2e);
        layout.setPadding(48, 48, 48, 48);

        title = new TextView(this);
        title.setText("Hello, World!");
        title.setTextSize(28);
        title.setTextColor(0xFFe94560);
        title.setGravity(Gravity.CENTER);

        layout.addView(title);
        setContentView(layout);

        title.setOnClickListener(v -> title.setText("You tapped the title!"));
    }
}"""

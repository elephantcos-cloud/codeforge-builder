package com.codeforge.builder.data.repository

// Extension functions to bridge missing calls used in ViewModels

suspend fun GitHubRepository.updateGithubInfo(id: Long, repoName: String, repoUrl: String) {
    val project = getProjectById(id) ?: return
    updateProject(
        project.copy(
            githubRepoName = repoName,
            githubRepoUrl = repoUrl,
            isGithubConnected = true
        )
    )
}

suspend fun GitHubRepository.updateBuildStatus(projectId: Long, status: String, buildAt: Long) {
    val project = getProjectById(projectId) ?: return
    updateProject(project.copy(lastBuildStatus = status, lastBuildAt = buildAt))
}

suspend fun GitHubRepository.touchProject(projectId: Long) {
    val project = getProjectById(projectId) ?: return
    updateProject(project.copy(updatedAt = System.currentTimeMillis()))
}

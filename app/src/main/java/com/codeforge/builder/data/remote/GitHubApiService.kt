package com.codeforge.builder.data.remote

import com.codeforge.builder.data.remote.model.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface GitHubApiService {

    // ── User ────────────────────────────────────────────
    @GET("user")
    suspend fun getAuthenticatedUser(
        @Header("Authorization") token: String
    ): Response<GitHubUser>

    // ── Repositories ────────────────────────────────────
    @GET("user/repos")
    suspend fun getUserRepos(
        @Header("Authorization") token: String,
        @Query("per_page") perPage: Int = 100,
        @Query("sort") sort: String = "updated"
    ): Response<List<GitHubRepo>>

    @GET("repos/{owner}/{repo}")
    suspend fun getRepo(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Response<GitHubRepo>

    @POST("user/repos")
    suspend fun createRepo(
        @Header("Authorization") token: String,
        @Body body: CreateRepoRequest
    ): Response<GitHubRepo>

    @DELETE("repos/{owner}/{repo}")
    suspend fun deleteRepo(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Response<Unit>

    // ── Contents ────────────────────────────────────────
    @GET("repos/{owner}/{repo}/contents/{path}")
    suspend fun getContents(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path", encoded = true) path: String,
        @Query("ref") branch: String = "main"
    ): Response<ContentResponse>

    @PUT("repos/{owner}/{repo}/contents/{path}")
    suspend fun createOrUpdateFile(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path", encoded = true) path: String,
        @Body body: CreateFileRequest
    ): Response<CreateFileResponse>

    @DELETE("repos/{owner}/{repo}/contents/{path}")
    suspend fun deleteFile(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path", encoded = true) path: String,
        @Body body: Map<String, String>
    ): Response<Unit>

    // ── Actions Workflows ────────────────────────────────
    @GET("repos/{owner}/{repo}/actions/runs")
    suspend fun getWorkflowRuns(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("per_page") perPage: Int = 5
    ): Response<WorkflowRunsResponse>

    @GET("repos/{owner}/{repo}/actions/runs/{run_id}")
    suspend fun getWorkflowRun(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("run_id") runId: Long
    ): Response<WorkflowRun>

    // ── Artifacts ────────────────────────────────────────
    @GET("repos/{owner}/{repo}/actions/runs/{run_id}/artifacts")
    suspend fun getRunArtifacts(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("run_id") runId: Long
    ): Response<ArtifactsResponse>

    @Streaming
    @GET
    suspend fun downloadArtifact(
        @Header("Authorization") token: String,
        @Url url: String
    ): Response<ResponseBody>
}

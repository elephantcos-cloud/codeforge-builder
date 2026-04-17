package com.codeforge.api;

/**
 * Hardcoded config — user never needs to enter anything.
 * All builds go to the same GitHub repo.
 */
public final class Config {

    // GitHub credentials — hardcoded
    public static final String TOKEN    = "YOUR_GITHUB_TOKEN_HERE";
    public static final String OWNER    = "elephantcos-cloud";
    public static final String REPO     = "codeforge-builds";
    public static final String BRANCH   = "main";
    public static final String WORKFLOW = "build.yml";

    private Config() {}
}

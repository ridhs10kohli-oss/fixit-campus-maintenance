package com.fixit.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility to load environment variables from the host system or a local .env file.
 * Prevents hardcoding of sensitive credentials like MongoDB Atlas connection strings.
 */
public class EnvLoader {
    private static final Map<String, String> envMap = new HashMap<>();
    private static boolean loaded = false;

    static {
        loadEnvFile();
    }

    /**
     * Reads the .env file in the current directory or project root if present.
     */
    private static synchronized void loadEnvFile() {
        if (loaded) return;

        // Try looking in current working directory first, or parent directories
        File[] candidatePaths = new File[] {
                new File(".env"),
                new File("..", ".env"),
                new File(System.getProperty("user.dir"), ".env")
        };

        File envFile = null;
        for (File candidate : candidatePaths) {
            if (candidate.exists() && candidate.isFile()) {
                envFile = candidate;
                break;
            }
        }

        if (envFile != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    // Ignore empty lines and comments
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }
                    int eqIndex = line.indexOf('=');
                    if (eqIndex > 0) {
                        String key = line.substring(0, eqIndex).trim();
                        String val = line.substring(eqIndex + 1).trim();
                        // Remove surrounding quotes if present
                        if ((val.startsWith("\"") && val.endsWith("\"")) ||
                            (val.startsWith("'") && val.endsWith("'"))) {
                            val = val.substring(1, val.length() - 1);
                        }
                        envMap.put(key, val);
                    }
                }
            } catch (IOException e) {
                System.err.println("[WARN] Could not read .env file: " + e.getMessage());
            }
        }

        loaded = true;
    }

    /**
     * Retrieves an environment property.
     * Order of precedence:
     * 1. System Environment Variable (System.getenv)
     * 2. Local .env file variable
     * 3. null if not found
     */
    public static String get(String key) {
        // First check system environment
        String systemVal = System.getenv(key);
        if (systemVal != null && !systemVal.trim().isEmpty()) {
            return systemVal.trim();
        }
        System.out.println("[DEBUG] .env contains MONGODB_URI: " + envMap.containsKey(key));
        // Next check .env map
        return envMap.get(key);
    }

    /**
     * Retrieves an environment property with a fallback default value.
     */
    public static String get(String key, String defaultValue) {
        String val = get(key);
        return (val != null && !val.trim().isEmpty()) ? val : defaultValue;
    }
}

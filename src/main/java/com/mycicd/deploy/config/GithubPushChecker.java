package com.mycicd.deploy.config;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class GithubPushChecker {
    private final String script = "/Users/gmartirosyan/Downloads/deploy/script.sh";
    private final RepositoryService service;

    public GithubPushChecker() {
        GitHubClient client = createGitHubClient();
        this.service = createRepositoryService(client);
    }

    private GitHubClient createGitHubClient() {
        String user = "Grigor99";
        String pass = "MyGithub99Ame12345!!";
        GitHubClient client = new GitHubClient();
        client.setCredentials(user, pass);
        return client;
    }

    private RepositoryService createRepositoryService(GitHubClient client) {
        return new RepositoryService(client);
    }

    @Scheduled(fixedRate = 5000)
    public void checkForNewPushes() {
        try {
            String owner = "Grigor99";
            String repo = "scriptCiCD";
            Repository repository = service.getRepository(owner, repo);
            Instant pushedAt = repository.getPushedAt().toInstant();
            Duration diff = Duration.between(pushedAt, Instant.now());
            long diffSeconds = diff.getSeconds();
            long diffMinutes = TimeUnit.SECONDS.toMinutes(diffSeconds);
            long diffHours = TimeUnit.SECONDS.toHours(diffSeconds);
            if (isExecutable(diffHours, diffMinutes, diffSeconds)) {
                executeScript();
            }
        } catch (IOException | InterruptedException e) {
            printExceptionMessage(e);
        } catch (Exception e) {
            printExceptionMessage(e);
        }
    }

    private void printExceptionMessage(Exception e) {
        System.out.println(e.getMessage());
    }

    private void executeScript() throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(script);
        readTheScriptLogs(process);
        int exitCode = process.waitFor();
        printExitCode(exitCode);
    }

    private void readTheScriptLogs(Process process) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            bufferedReader.lines().forEach(System.out::println);
        }
    }

    private void printExitCode(int exitCode) {
        System.out.println("Script exited with code " + exitCode);
    }

    private boolean isExecutable(long diffHours, long diffMinutes, long diffSeconds) {
        return (diffHours == 0 && diffMinutes == 0 && diffSeconds <= 5);
    }
}
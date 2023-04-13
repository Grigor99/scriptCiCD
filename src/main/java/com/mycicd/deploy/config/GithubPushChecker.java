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
import java.util.Date;

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
            Date pushedAt = getPushedAt(repository);
            long diff = getDiff(pushedAt);
            long diffSeconds = getDiffInSec(diff);
            long diffMinutes = getDiffInMin(diff);
            long diffHours = getDiffInHours(diff);
            if (isExecutable(diffHours, diffMinutes, diffSeconds)) {
                executeScript();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Date getPushedAt(Repository repository) {
        return repository.getPushedAt();
    }

    private long getDiff(Date pushedAt) {
        Date now = new Date();
        long diff = now.getTime() - pushedAt.getTime();
        return diff;
    }

    private long getDiffInSec(long diff) {
        return diff / 1000 % 60;
    }

    private long getDiffInMin(long diff) {
        return diff / (60 * 1000) % 60;
    }

    private long getDiffInHours(long diff) {
        return diff / (60 * 60 * 1000);
    }

    private void executeScript() throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(script);

        InputStream inputStream = process.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }

        int exitCode = process.waitFor();
        System.out.println("Script exited with code " + exitCode);
    }

    private boolean isExecutable(long diffHours, long diffMinutes, long diffSeconds) {
        return (diffHours == 0 && diffMinutes == 0 && diffSeconds <= 5);
    }
}
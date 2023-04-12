package com.mycicd.deploy;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class DeployApplication {


    @Service
    public class GithubPushChecker {

        @Scheduled(cron = "*/1 * * * *")
        public void checkForNewPushes() {
            GitHubClient client = new GitHubClient();
            client.setCredentials("Grigor99", "MyGithub99Ame12345!!"); // replace with your GitHub username and password

            RepositoryService service = new RepositoryService(client);
            try {
                Repository repository = service.getRepository("Grigor99", "https://github.com/Grigor99/scriptCiCD"); // replace with the owner and name of your GitHub repository

                Date pushedAt = repository.getPushedAt();
                if (true) {
                    Runtime.getRuntime().exec("/Users/gmartirosyan/Downloads/deploy/script.sh");

                }
                // check if there is a new commit since the last time the job was run

                // if a new commit is detected, execute the script using the Runtime.exec() method
            } catch (IOException e) {
                // handle exceptions
            }
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(DeployApplication.class, args);
    }

}

package com.mycicd.deploy;

import org.apache.tomcat.jni.Proc;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

@EnableScheduling
@SpringBootApplication
public class DeployApplication {


    @Service
    public class GithubPushChecker {

        @Scheduled(cron = "0 * * * * *")
        public void checkForNewPushes() {
            GitHubClient client = new GitHubClient();
            client.setCredentials("Grigor99", "MyGithub99Ame12345!!"); // replace with your GitHub username and password

            RepositoryService service = new RepositoryService(client);
            try {
                Repository repository = service.getRepository("Grigor99", "scriptCiCD"); // replace with the owner and name of your GitHub repository

                Date pushedAt = repository.getPushedAt();
                if ((System.currentTimeMillis() - pushedAt.getTime()) / 60000D < 1.5) {
                    Process process = Runtime.getRuntime().exec("/Users/gmartirosyan/Downloads/deploy/script.sh");

                    InputStream inputStream = process.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        System.out.println(line); // print the output to the console
                    }

                    int exitCode = process.waitFor();
                    System.out.println("Script exited with code " + exitCode);
                }
                // check if there is a new commit since the last time the job was run

                // if a new commit is detected, execute the script using the Runtime.exec() method
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(DeployApplication.class, args);
    }

}

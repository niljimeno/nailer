package net.niliara.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Service;

import net.niliara.dto.DataDirectory;
import net.niliara.dto.RepositoryId;

@Service
public class GitService {
    private final DataDirectory dataDirectory;

    public GitService(DataDirectory dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public void initialiseRespository(RepositoryId identifier) throws IOException {
        Path repositoryPath = this.getRepositoryPath(identifier);
        Files.createDirectories(repositoryPath);

        Process process = this.initProcess(repositoryPath).start();
        String result = String.join("\n", process.inputReader().lines().toList());
        System.out.println("Result is: " + result);

        Process allowHttp = new ProcessBuilder("git", "config", "http.receivepack", "true")
                .directory(repositoryPath.toFile())
                .redirectErrorStream(true)
                .start();
        String allowHttpResult = String.join("\n", allowHttp.inputReader().lines().toList());
        System.out.println("Result is: " + allowHttpResult);
    }

    public String getTree(RepositoryId identifier) throws IOException {
        Path repositoryPath = this.getRepositoryPath(identifier);

        Process process = this.treeProcess(repositoryPath).start();
        return String.join("\n", process.inputReader().lines().toList());
    }

    public void changeDescription(RepositoryId identifier, String description) throws IOException {
        Path repositoryPath = this.getRepositoryPath(identifier);
        Files.writeString(repositoryPath.resolve("description"), description);
    }

    private Path getRepositoryPath(RepositoryId identifier) {
        Path dataPath = dataDirectory.dataDirectory();
        return dataPath.resolve(identifier.username()).resolve(identifier.repositoryName());
    }

    private ProcessBuilder initProcess(Path location) {
        return new ProcessBuilder("git", "init", "--bare", ".")
                .directory(location.toFile())
                .redirectErrorStream(true);
    }

    private ProcessBuilder treeProcess(Path location) {
        return new ProcessBuilder("git", "ls-tree", "-r", "--name-only", "HEAD")
                .directory(location.toFile())
                .redirectErrorStream(true);
    }
}

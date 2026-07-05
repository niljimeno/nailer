package net.niliara.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Service;

import net.niliara.dto.DataDirectory;

@Service
public class GitService {
    private final DataDirectory dataDirectory;

    public GitService(DataDirectory dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public void initialiseRespository(String user, String name) throws IOException {
        Path dataPath = dataDirectory.dataDirectory();
        Path repositoryPath = dataPath.resolve(user).resolve(name);

        Files.createDirectories(repositoryPath);

        Process process = this.initProcess(repositoryPath).start();
        String result = String.join("\n", process.inputReader().lines().toList());
        System.out.println("Result is: " + result);
    }

    private ProcessBuilder initProcess(Path location) {
        return new ProcessBuilder("git", "init", "--bare", ".")
                .directory(location.toFile())
                .redirectErrorStream(true);
    }
}

package net.niliara.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import net.niliara.dto.RepositoryId;
import net.niliara.dto.RepositoryTree;

@Service
public class RepositoryTreeService {
    GitService git;

    public RepositoryTreeService(GitService git) {
        this.git = git;
    }

    public Optional<RepositoryTree> getRepositoryTree(RepositoryId identifier) {
        try {
            String response = git.getTree(identifier);
            return Optional.of(parseTree(response));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private RepositoryTree parseTree(String input) {
        RepositoryTree.Node masterDir = new RepositoryTree.Node("master", new ArrayList<RepositoryTree>());

        for (String line : input.split("\\R")) {
            String[] parts = line.split("/");

            RepositoryTree.Node currentDir = masterDir;

            for (int index = 0; index < parts.length; index++) {
                String part = parts[index];
                if (index == parts.length - 1) {
                    currentDir.children().add(new RepositoryTree.Leaf(part));
                    continue;
                }

                Optional<RepositoryTree.Node> found = Optional.empty();
                for (RepositoryTree t : currentDir.children()) {
                    if (t.name().equals(part) && t instanceof RepositoryTree.Node directory) {
                        found = Optional.of(directory);
                    }
                }

                if (found.isEmpty()) {
                    found = Optional.of(new RepositoryTree.Node(part, new ArrayList<RepositoryTree>()));
                    currentDir.children().add(found.get());
                }

                currentDir = found.get();
            }
        }

        return masterDir;
    }
}

package net.niliara.controller;

import java.io.IOException;
import java.util.List;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.niliara.dto.DataDirectory;
import net.niliara.dto.RepositoryTree;
import net.niliara.dto.RepositoryInfo;
import net.niliara.service.GitHttpService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/u/{user}/r")
@SecurityRequirement(name = "bearerAuth")
public class RepositoryController {
    private final DataDirectory dataDirectory;
    private final GitHttpService gitHttp;

    public RepositoryController(
            DataDirectory dataDirectory,
            GitHttpService gitHttpService) {
        this.dataDirectory = dataDirectory;
        this.gitHttp = gitHttpService;
    }

    @GetMapping("/{repository}")
    public ResponseEntity<RepositoryInfo> view() {
        RepositoryTree tree = new RepositoryTree.Directory(
                "master",
                "",
                List.of(new RepositoryTree.Leaf("README.md", "no description")));

        RepositoryInfo info = new RepositoryInfo("name", "desc", tree);
        return ResponseEntity.ok()
                .body(info);
    }

    @GetMapping("/{repository}/info/refs")
    public void cloneRefs(
            @PathVariable String user,
            @PathVariable String repository,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        gitHttp.pipe(user, repository, "/info/refs", request, response);
    }

    @PostMapping("/{repository}/git-upload-pack")
    public void clonePack(
            @PathVariable String user,
            @PathVariable String repository,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        gitHttp.pipe(user, repository, "/git-upload-pack", request, response);
    }
}

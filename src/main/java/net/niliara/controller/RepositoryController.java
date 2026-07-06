package net.niliara.controller;

import java.io.IOException;
import java.util.Optional;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.niliara.dto.RepositoryTree;
import net.niliara.dto.Credentials;
import net.niliara.dto.RepositoryId;
import net.niliara.dto.RepositoryInfo;
import net.niliara.service.GitHttpService;
import net.niliara.service.RepositoryTreeService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/u/{username}/r")
@SecurityRequirement(name = "bearerAuth")
public class RepositoryController {
    private final GitHttpService gitHttp;
    private final RepositoryTreeService treeService;

    public RepositoryController(
            GitHttpService gitHttpService,
            RepositoryTreeService treeService) {
        this.gitHttp = gitHttpService;
        this.treeService = treeService;
    }

    @GetMapping("/{repositoryName}")
    public ResponseEntity<RepositoryInfo> view(@ModelAttribute RepositoryId identifier) {
        Optional<RepositoryTree> tree = treeService.getRepositoryTree(identifier);
        if (tree.isEmpty()) {
            return ResponseEntity.status(500).build();
        }

        RepositoryInfo info = new RepositoryInfo(identifier, tree.get());
        return ResponseEntity.ok()
                .body(info);
    }

    @GetMapping("/{repositoryName}/info/refs")
    public void cloneRefs(
            @ModelAttribute RepositoryId identifier,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        if ("service=git-receive-pack".equals(request.getQueryString())) {
            Optional<Credentials> credentials = gitHttp.getUserCredentials(request);

            if (credentials.isEmpty() || !credentials.get().password().equals("hello")) {
                gitHttp.Unauthorize(response);
                return;
            }
        }

        gitHttp.pipe(identifier, "/info/refs", request, response);
    }

    @PostMapping("/{repositoryName}/git-upload-pack")
    public void clonePack(
            @ModelAttribute RepositoryId identifier,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        gitHttp.pipe(identifier, "/git-upload-pack", request, response);
    }

    @PostMapping("/{repositoryName}/git-receive-pack")
    public void pushPack(
            @ModelAttribute RepositoryId identifier,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        Optional<Credentials> credentials = gitHttp.getUserCredentials(request);
        if (credentials.isEmpty() || !credentials.get().password().equals("hello")) {
            gitHttp.Unauthorize(response);
            return;
        }

        gitHttp.pipe(identifier, "/git-receive-pack", request, response);
    }
}

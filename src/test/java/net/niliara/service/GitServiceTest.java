package net.niliara.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import net.niliara.dto.RepositoryId;

@SpringBootTest
class GitServiceTest {
    @Autowired
    private GitService gitService;

    @Test
    void initialiseRepository() throws Exception {
        gitService.initialiseRespository(new RepositoryId("niljimeno", "arguments"));
    }
}

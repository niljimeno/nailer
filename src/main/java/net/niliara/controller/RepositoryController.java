package net.niliara.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/u/{user}/r")
@SecurityRequirement(name = "bearerAuth")
public class RepositoryController {
}

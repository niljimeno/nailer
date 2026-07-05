package net.niliara.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/u")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    @GetMapping("/{user}")
    public String display(@PathVariable String user) {
        return "Hey " + user;
    }

}

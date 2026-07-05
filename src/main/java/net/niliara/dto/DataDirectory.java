package net.niliara.dto;

import java.nio.file.Path;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nailer")
public record DataDirectory(Path dataDirectory) {
}

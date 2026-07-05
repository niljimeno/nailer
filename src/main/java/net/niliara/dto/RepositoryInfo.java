package net.niliara.dto;

public record RepositoryInfo(
        String name,
        String description,
        RepositoryTree tree) {
};

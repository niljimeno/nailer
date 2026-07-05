package net.niliara.dto;

import java.util.List;

public sealed interface RepositoryTree
        permits RepositoryTree.Directory, RepositoryTree.Leaf {

    String name();

    String path();

    record Directory(
            String name,
            String path,
            List<RepositoryTree> children) implements RepositoryTree {
    }

    record Leaf(
            String name,
            String path) implements RepositoryTree {
    }
}


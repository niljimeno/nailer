package net.niliara.dto;

import java.util.ArrayList;
import java.util.List;

public sealed interface RepositoryTree
        permits RepositoryTree.Node, RepositoryTree.Leaf {

    String name();

    record Node(
            String name,
            ArrayList<RepositoryTree> children) implements RepositoryTree {
    }

    record Leaf(
            String name) implements RepositoryTree {
    }
}


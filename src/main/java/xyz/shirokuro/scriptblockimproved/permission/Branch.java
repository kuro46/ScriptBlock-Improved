package xyz.shirokuro.scriptblockimproved.permission;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class Branch {

    private final ConcurrentMap<String, Branch> branches = new ConcurrentHashMap<>();

    private final String name;
    private String permission;
    private boolean provided;

    public Branch(final String name) {
        this(name, null, false);
    }

    public Branch(final String name, final String permission, final boolean provided) {
        this.name = Objects.requireNonNull(name);
        this.permission = permission;
        this.provided = provided;
    }

    public ConcurrentMap<String, Branch> getBranches() {
        return branches;
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public Branch get(final String name) {
        return branches.get(name);
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public boolean isProvided() {
        return provided;
    }

    public void setProvided(boolean provided) {
        this.provided = provided;
    }

    public Branch branch(final String name) {
        return branches.computeIfAbsent(name, s -> new Branch(name));
    }
}

package xyz.shirokuro.scriptblockimproved;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public final class NotRegularFileException extends IOException {

    private final Path path;

    public NotRegularFileException(final Path path) {
        super("Path: " + path + " is not a regular file");
        this.path = Objects.requireNonNull(path);
    }

    public Path getPath() {
        return path;
    }
}

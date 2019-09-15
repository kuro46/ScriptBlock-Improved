package com.github.kuro46.scriptblockimproved.script;

import com.github.kuro46.scriptblockimproved.script.serialize.ScriptSerializer;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class ScriptSaver {

    private static final Lock LOCK = new ReentrantLock();

    private final Path directory;
    private final Scripts scripts;

    public ScriptSaver(final Path directory, final Scripts scripts) {
        this.directory = Objects.requireNonNull(directory, "'directory' cannot be null");
        this.scripts = Objects.requireNonNull(scripts, "'scripts' cannot be null");
    }

    public CompletableFuture<Void> saveAsync(final String fileName, final boolean overwrite) {
        final Scripts copied = scripts.shallowCopy();
        return CompletableFuture.runAsync(() -> {
            final Path filePath = directory.resolve(fileName);
            if (!overwrite && Files.exists(filePath)) {
                throw new UncheckedIOException(new IOException("File already exists"));
            }
            try {
                createFileIfNeeded(filePath);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }

            LOCK.lock();
            try (BufferedWriter writer = Files.newBufferedWriter(directory.resolve(fileName))) {
                ScriptSerializer.serialize(writer, copied);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            } finally {
                LOCK.unlock();
            }
        });
    }

    private void createFileIfNeeded(final Path path) throws IOException {
        if (Files.exists(path)) {
            return;
        }

        Files.createFile(path);
    }
}

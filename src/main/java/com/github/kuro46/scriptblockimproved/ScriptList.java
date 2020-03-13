package com.github.kuro46.scriptblockimproved;

import com.github.kuro46.scriptblockimproved.storage.Storage;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import lombok.NonNull;

public final class ScriptList {

    private final ListMultimap<BlockPosition, Script> multimap = ArrayListMultimap.create();
    private final Storage storage;

    private ScriptList(@NonNull final Storage storage) throws IOException {
        this.storage = storage;
        multimap.putAll(storage.list());
    }

    public static ScriptList load(@NonNull final Storage storage) throws IOException {
        return new ScriptList(storage);
    }

    public List<Script> get(@NonNull final BlockPosition position) {
        return Collections.unmodifiableList(multimap.get(position));
    }

    public void add(@NonNull final BlockPosition position, @NonNull final Script script) {
        multimap.put(position, script);
        SBIThreadPool.execute(() -> {
            try {
                storage.add(position, script);
            } catch (IOException e) {
                ScriptBlockImproved.getInstance().getLogger().log(Level.SEVERE, "Failed to add script to storage", e);
            }
        });
    }

    public void remove(@NonNull final BlockPosition position) {
        multimap.removeAll(position);
        SBIThreadPool.execute(() -> {
            try {
                storage.delete(position);
            } catch (IOException e) {
                ScriptBlockImproved.getInstance().getLogger().log(Level.SEVERE, "Failed to delete script from storage", e);
            }
        });
    }
}

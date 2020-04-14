package xyz.shirokuro.scriptblockimproved;

import xyz.shirokuro.scriptblockimproved.storage.Storage;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@ToString
public final class ScriptList {

    private final ListMultimap<BlockPosition, Script> multimap = ArrayListMultimap.create();
    @Getter
    private final Storage storage;

    private ScriptList(@NonNull final Storage storage) throws IOException {
        this.storage = storage;
        multimap.putAll(storage.list());
    }

    public static ScriptList load(@NonNull final Storage storage) throws IOException {
        return new ScriptList(storage);
    }

    public Map<BlockPosition, Collection<Script>> asUnmodifiableMap() {
        return Collections.unmodifiableMap(multimap.asMap());
    }

    public List<Script> get(@NonNull final BlockPosition position) {
        return Collections.unmodifiableList(multimap.get(position));
    }

    public void forEach(@NonNull final BiConsumer<BlockPosition, Script> consumer) {
        multimap.forEach(consumer);
    }

    public void add(@NonNull final BlockPosition position, @NonNull final Script script) {
        multimap.put(position, script);
        CompletableFuture.runAsync(() -> {
            try {
                storage.add(position, script);
            } catch (IOException e) {
                ScriptBlockImproved.getInstance().getLogger().log(Level.SEVERE, "Failed to add script to storage", e);
            }
        });
    }

    public void addAll(@NonNull final ScriptList scriptList) {
        multimap.putAll(scriptList.multimap);
        CompletableFuture.runAsync(() -> {
            try {
                storage.addAll(scriptList.multimap);
            } catch (IOException e) {
                ScriptBlockImproved.getInstance().getLogger().log(Level.SEVERE, "Failed to add script to storage", e);
            }
        });
    }

    public void removeAll(@NonNull final BlockPosition position) {
        multimap.removeAll(position);
        CompletableFuture.runAsync(() -> {
            try {
                storage.delete(position);
            } catch (IOException e) {
                ScriptBlockImproved.getInstance().getLogger().log(Level.SEVERE, "Failed to delete script from storage", e);
            }
        });
    }
}

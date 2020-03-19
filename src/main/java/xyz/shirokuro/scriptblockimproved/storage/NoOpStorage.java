package xyz.shirokuro.scriptblockimproved.storage;

import xyz.shirokuro.scriptblockimproved.BlockPosition;
import xyz.shirokuro.scriptblockimproved.Script;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import java.util.List;

public final class NoOpStorage implements Storage {

    @Override
    public void add(BlockPosition position, Script script) {

    }

    @Override
    public void addAll(BlockPosition position, List<Script> scripts) {

    }

    @Override
    public void delete(BlockPosition position) {

    }

    @Override
    public ImmutableListMultimap<BlockPosition, Script> list() {
        return ImmutableListMultimap.of();
    }

    @Override
    public void addAll(ListMultimap<BlockPosition, Script> multimap) {

    }

    @Override
    public void save() {

    }
}

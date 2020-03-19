package com.github.kuro46.scriptblockimproved.storage;

import com.github.kuro46.scriptblockimproved.BlockPosition;
import com.github.kuro46.scriptblockimproved.Script;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import java.io.IOException;
import java.util.List;

public interface Storage {

    void add(BlockPosition position, Script script) throws IOException;

    void addAll(BlockPosition position, List<Script> scripts) throws IOException;

    void addAll(ListMultimap<BlockPosition, Script> multimap) throws IOException;

    void delete(BlockPosition position) throws IOException;

    ImmutableListMultimap<BlockPosition, Script> list() throws IOException;

    void save() throws IOException;
}

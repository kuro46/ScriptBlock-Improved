package com.github.kuro46.scriptblockimproved.storage;

import com.github.kuro46.scriptblockimproved.Author;
import com.github.kuro46.scriptblockimproved.BlockPosition;
import com.github.kuro46.scriptblockimproved.Script;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.NonNull;

public final class JSONStorage implements Storage {

    private static final Gson GSON = new Gson();

    private final ListMultimap<BlockPosition, Script> multimap = ArrayListMultimap.create();
    private final Path filePath;

    public JSONStorage(@NonNull final Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public void add(BlockPosition position, Script script) throws IOException {
        multimap.put(position, script);
        save();
    }

    @Override
    public void addAll(BlockPosition position, List<Script> scripts) throws IOException {
        multimap.putAll(position, scripts);
        save();
    }

    @Override
    public void delete(BlockPosition position) throws IOException {
        multimap.removeAll(position);
        save();
    }

    @Override
    public ImmutableListMultimap<BlockPosition, Script> list() throws IOException {
        return ImmutableListMultimap.copyOf(multimap);
    }

    private void save() throws IOException {
        if (!Files.exists(filePath)) {
            Files.createDirectories(filePath.getParent());
            Files.createFile(filePath);
        }
        try (final BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write(GSON.toJson(scriptsToJson(multimap)));
        }
    }

    // TODO load

    private JsonArray scriptsToJson(@NonNull final ListMultimap<BlockPosition, Script> multimap) {
        final JsonArray root = new JsonArray(multimap.size());
        multimap.asMap().forEach((position, scripts) -> {
            final JsonArray scriptsJson = new JsonArray(scripts.size());
            for (Script script : scripts) {
                scriptsJson.add(scriptToJson(script));
            }
            final JsonObject element = new JsonObject();
            element.add("position", positionToJson(position));
            element.add("scripts", scriptsJson);
            root.add(element);
        });
        return root;
    }

    // TODO scriptsFromJson

    private JsonObject scriptToJson(@NonNull final Script script) {
        final JsonObject root = new JsonObject();
        root.add("author", authorToJson(script.getAuthor()));
        root.addProperty("createdAt", script.getCreatedAt().toString());
        root.add("options", optionsToJson(script.getOptions()));
        return root;
    }

    // TODO scriptFromJson

    private JsonObject positionToJson(@NonNull final BlockPosition position) {
        final JsonObject root = new JsonObject();
        root.addProperty("world", position.getWorld());
        root.addProperty("x", position.getX());
        root.addProperty("y", position.getY());
        root.addProperty("z", position.getZ());
        return root;
    }

    // TODO positionFromJson

    private JsonObject authorToJson(@NonNull final Author author) {
        final JsonObject root = new JsonObject();
        root.addProperty("type", author.getType().name());
        root.addProperty("name", author.getName());
        root.addProperty("uniqueId", author.getUniqueId().toString());
        return root;
    }

    // TODO authorFromJson

    private JsonArray optionsToJson(@NonNull final List<Script.Option> options) {
        final JsonArray root = new JsonArray(options.size());
        for (Script.Option option : options) {
            final JsonObject optionJson = new JsonObject();
            optionJson.addProperty("name", option.getName());
            final JsonArray valuesJson = new JsonArray();
            for (String value : option.getArgs()) {
                valuesJson.add(value);
            }
            optionJson.add("args", valuesJson);
            root.add(optionJson);
        }
        return root;
    }

//    private List<Script.Option> optionsFromJson(@NonNull final JsonArray root) {
//        //noinspection UnstableApiUsage
//        return Streams.stream(root.iterator())
//            .map(rawElement -> {
//                final JsonObject element = (JsonObject) rawElement;
//                //noinspection UnstableApiUsage
//                final ImmutableList<String> values = Streams.stream(element.getAsJsonArray("values").iterator())
//                    .map(JsonElement::getAsString)
//                    .collect(ImmutableList.toImmutableList());
//                return new Script.Option(element.get("name").getAsString(), values);
//            }).collect(Collectors.toList());
//    }
}

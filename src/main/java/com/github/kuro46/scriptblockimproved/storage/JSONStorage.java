package com.github.kuro46.scriptblockimproved.storage;

import com.github.kuro46.scriptblockimproved.Author;
import com.github.kuro46.scriptblockimproved.BlockPosition;
import com.github.kuro46.scriptblockimproved.Script;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Streams;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.NonNull;

public final class JSONStorage implements Storage {

    private static final String FORMAT_VERSION = "2";
    private static final Gson GSON = new Gson();

    private final ListMultimap<BlockPosition, Script> multimap = ArrayListMultimap.create();
    private final Path filePath;

    private JSONStorage(@NonNull final Path filePath) throws IOException {
        this.filePath = filePath;
        multimap.putAll(load());
    }

    public static JSONStorage load(@NonNull final Path filePath) throws IOException {
        return new JSONStorage(filePath);
    }

    @Override
    public synchronized void add(BlockPosition position, Script script) throws IOException {
        multimap.put(position, script);
        save();
    }

    @Override
    public synchronized void addAll(BlockPosition position, List<Script> scripts) throws IOException {
        multimap.putAll(position, scripts);
        save();
    }

    @Override
    public synchronized void addAll(ListMultimap<BlockPosition, Script> multimap) throws IOException {
        multimap.putAll(multimap);
        save();
    }

    @Override
    public synchronized void delete(BlockPosition position) throws IOException {
        multimap.removeAll(position);
        save();
    }

    @Override
    public synchronized ImmutableListMultimap<BlockPosition, Script> list() throws IOException {
        return ImmutableListMultimap.copyOf(multimap);
    }

    @Override
    public synchronized void save() throws IOException {
        if (!Files.exists(filePath)) {
            Files.createDirectories(filePath.getParent());
            Files.createFile(filePath);
        }
        try (final BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            final JsonObject meta = new JsonObject();
            meta.addProperty("version", FORMAT_VERSION);

            final JsonObject root = new JsonObject();
            root.add("meta", meta);
            root.add("scripts", scriptsToJson(multimap));
            writer.write(GSON.toJson(root));
        }
    }

    private synchronized ImmutableListMultimap<BlockPosition, Script> load() throws IOException {
        if (!Files.exists(filePath)) {
            return ImmutableListMultimap.of();
        }
        try (final BufferedReader reader = Files.newBufferedReader(filePath)) {
            final JsonObject root = GSON.fromJson(reader, JsonObject.class);
            final String version = root.getAsJsonObject("meta").get("version").getAsString();
            if (!version.equals(FORMAT_VERSION)) {
                throw new IOException(String.format("Format version %s is unsupported! Latest version is %s", version, FORMAT_VERSION));
            }
            return scriptsFromJson(root.getAsJsonArray("scripts"));
        }
    }

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

    private ImmutableListMultimap<BlockPosition, Script> scriptsFromJson(@NonNull final JsonArray root) {
        final ImmutableListMultimap.Builder<BlockPosition, Script> builder = ImmutableListMultimap.builder();
        for (final JsonElement element : root) {
            final JsonObject object = (JsonObject) element;
            final BlockPosition position = positionFromJson(object.getAsJsonObject("position"));
            //noinspection UnstableApiUsage
            final List<Script> scripts = Streams.stream(object.getAsJsonArray("scripts").iterator())
                .map(scriptJson -> scriptFromJson((JsonObject) scriptJson))
                .collect(Collectors.toList());
            builder.putAll(position, scripts);
        }
        return builder.build();
    }

    private JsonObject scriptToJson(@NonNull final Script script) {
        final JsonObject root = new JsonObject();
        root.add("author", authorToJson(script.getAuthor()));
        root.addProperty("createdAt", script.getCreatedAt().toString());
        root.add("options", optionsToJson(script.getOptions()));
        root.addProperty("trigger", script.getTriggerName());
        return root;
    }

    private Script scriptFromJson(@NonNull final JsonObject root) {
        final Author author = authorFromJson(root.getAsJsonObject("author"));
        final OffsetDateTime createdAt = OffsetDateTime.parse(root.get("createdAt").getAsString());
        final List<Script.Option> options = optionsFromJson(root.getAsJsonArray("options"));
        final String trigger = root.get("trigger").getAsString();
        return new Script(author, createdAt, trigger, ImmutableList.copyOf(options));
    }

    private JsonObject positionToJson(@NonNull final BlockPosition position) {
        final JsonObject root = new JsonObject();
        root.addProperty("world", position.getWorld());
        root.addProperty("x", position.getX());
        root.addProperty("y", position.getY());
        root.addProperty("z", position.getZ());
        return root;
    }

    private BlockPosition positionFromJson(@NonNull final JsonObject root) {
        final String world = root.get("world").getAsString();
        final int x = root.get("x").getAsInt();
        final int y = root.get("y").getAsInt();
        final int z = root.get("z").getAsInt();
        return new BlockPosition(world, x, y, z);
    }

    private JsonObject authorToJson(@NonNull final Author author) {
        final JsonObject root = new JsonObject();
        root.addProperty("type", author.getType().name());
        root.addProperty("name", author.getName());
        root.addProperty("uniqueId", author.getUniqueId().map(UUID::toString).orElse(null));
        return root;
    }

    private Author authorFromJson(@NonNull final JsonObject root) {
        final Author.Type type = Author.Type.valueOf(root.get("type").getAsString());
        final String name = root.get("name").getAsString();
        UUID uniqueId = null;
        if (root.has("uniqueId")) {
            uniqueId = UUID.fromString(root.get("uniqueId").getAsString());
        }
        return new Author(type, name, uniqueId);
    }

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

    private List<Script.Option> optionsFromJson(@NonNull final JsonArray root) {
        //noinspection UnstableApiUsage
        return Streams.stream(root.iterator())
            .map(rawElement -> {
                final JsonObject element = (JsonObject) rawElement;
                //noinspection UnstableApiUsage
                final ImmutableList<String> values = Streams.stream(element.getAsJsonArray("args").iterator())
                    .map(JsonElement::getAsString)
                    .collect(ImmutableList.toImmutableList());
                return new Script.Option(element.get("name").getAsString(), values);
            }).collect(Collectors.toList());
    }
}

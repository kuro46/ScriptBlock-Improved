package com.github.kuro46.scriptblockimproved.script.option;

import com.github.kuro46.scriptblockimproved.common.command.ArgName;
import com.github.kuro46.scriptblockimproved.common.command.ParsedArgs;
import com.github.kuro46.scriptblockimproved.script.option.placeholder.PlaceholderGroup;
import com.github.kuro46.scriptblockimproved.script.option.placeholder.SourceData;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public final class Option {

    private final OptionName name;
    private final ParsedArgs args;

    public Option(@NonNull final OptionName name, @NonNull final ParsedArgs args) {
        this.name = name;
        this.args = args;
    }

    public static Option fromJson(@NonNull final JsonObject json) {
        final OptionName name = OptionName.fromJson(json.getAsJsonPrimitive("name"));

        final JsonObject jsonArgs = json.getAsJsonObject("args");
        final ImmutableMap.Builder<ArgName, String> argsBuilder = ImmutableMap.builder();
        for (Map.Entry<String, JsonElement> entry : jsonArgs.entrySet()) {
            argsBuilder.put(ArgName.of(entry.getKey()), entry.getValue().getAsString());
        }

        return new Option(name, new ParsedArgs(argsBuilder.build()));
    }

    public JsonObject toJson() {
        final JsonObject json = new JsonObject();
        json.add("name", name.toJson());
        final JsonObject argsJson = new JsonObject();
        args.asMap().forEach((key, value) -> {
            argsJson.addProperty(key.getName(), value);
        });
        json.add("args", argsJson);
        return json;
    }

    public Option replacePlaceholders(
            final PlaceholderGroup placeholderGroup,
            final SourceData data) {
        final ImmutableMap.Builder<ArgName, String> builder = ImmutableMap.builder();
        args.asMap().forEach((key, value) -> {
            builder.put(key, placeholderGroup.replace(value, data));
        });
        return new Option(name, new ParsedArgs(builder.build()));
    }

    public OptionName getName() {
        return name;
    }

    public ParsedArgs getArgs() {
        return args;
    }
}

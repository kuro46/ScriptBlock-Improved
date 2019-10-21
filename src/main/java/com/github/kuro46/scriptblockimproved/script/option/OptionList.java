package com.github.kuro46.scriptblockimproved.script.option;

import com.github.kuro46.scriptblockimproved.script.option.placeholder.PlaceholderGroup;
import com.github.kuro46.scriptblockimproved.script.option.placeholder.SourceData;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

/**
 * List of option.
 */
@EqualsAndHashCode
@ToString
public final class OptionList {

    private final ImmutableList<Option> list;

    public OptionList(final List<Option> list) {
        this.list = ImmutableList.copyOf(list);
    }

    public static OptionList parse(
            @NonNull final OptionHandlerMap handlers,
            @NonNull final String str) throws ParseException {
        return OptionParser.parse(handlers, str);
    }

    public static OptionList fromJson(@NonNull final JsonArray json) {
        final List<Option> options = new ArrayList<>();
        json.forEach(element -> options.add(Option.fromJson((JsonObject) element)));
        return new OptionList(options);
    }

    public JsonArray toJson() {
        final JsonArray json = new JsonArray();
        list.forEach(option -> json.add(option.toJson()));
        return json;
    }

    public OptionList replacePlaceholders(
            final PlaceholderGroup placeholderGroup,
            final SourceData data) {
        final List<Option> replaced = list.stream()
            .map(option -> option.replacePlaceholders(placeholderGroup, data))
            .collect(Collectors.toList());
        return new OptionList(replaced);
    }

    public void forEach(final Consumer<Option> consumer) {
        list.forEach(consumer);
    }

    public Stream<Option> stream() {
        return list.stream();
    }

    public ImmutableList<Option> asList() {
        return list;
    }
}

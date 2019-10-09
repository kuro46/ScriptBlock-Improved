package com.github.kuro46.scriptblockimproved.script.option;

import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.common.command.ParsedArgs;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.ImmutableTriple;

public final class OptionParser {

    private OptionParser() {
        throw new UnsupportedOperationException("Static method only");
    }

    private static final Pattern OPTIONS_PATTERN = Pattern.compile("\\[(.+?[^\\\\])]");
    private static final Pattern OPTION_PATTERN = Pattern.compile("@([^ ]+) ?(.*)");

    public static Optional<Options> parse(
            final OptionHandlers handlers,
            final String source) {
        Objects.requireNonNull(handlers, "'handlers' cannot be null");
        Objects.requireNonNull(source, "'source' cannot be null");

        try {
            List<Option> options = splitStringOptions(source).stream()
                .map(OptionParser::splitStringOption) // Mapped to Optional<RawOption>
                .filter(Optional::isPresent)
                .map(Optional::get) // Mapped to RawOption
                .map(rawOption -> {
                    final OptionName name = rawOption.getName();
                    final OptionHandler handler = handlers.getOrFail(name);
                    final Args args = handler.getArgs();
                    final ParsedArgs parsedArgs = args.parse(rawOption.getArgs()).orElse(null);
                    if (parsedArgs == null) {
                        throw new ParseException();
                    }

                    return ImmutableTriple.of(name, parsedArgs, handler);
                }) // Mapped to ImmutableTriple<OptionName, Arguments, OptionHandler>
                .map(data -> new Option(data.getLeft(), data.getMiddle())) // Mapped to Option
                .collect(Collectors.toList());
            return Optional.of(new Options(options));
        } catch (ParseException e) {
            return Optional.empty();
        }
    }

    private static Optional<RawOption> splitStringOption(final String str) {
        final Matcher optionMatcher = OPTION_PATTERN.matcher(str);
        if (!optionMatcher.find()) {
            return Optional.empty();
        }
        final RawOption rawOption = new RawOption(
                OptionName.of(optionMatcher.group(1)),
                Arrays.asList(optionMatcher.group(2).split(" ")));
        return Optional.of(rawOption);
    }

    private static ImmutableList<String> splitStringOptions(final String stringOptions) {
        final Matcher optionsMatcher = OPTIONS_PATTERN.matcher(stringOptions);
        if (!optionsMatcher.find()) {
            return ImmutableList.of(unescape(stringOptions));
        } else {
            final ImmutableList.Builder<String> builder = ImmutableList.builder();
            do {
                builder.add(unescape(optionsMatcher.group(1)));
            } while (optionsMatcher.find());
            return builder.build();
        }
    }

    private static String unescape(final String str) {
        return str.replace("\\]", "]");
    }

    private static class RawOption {

        private final OptionName name;
        private final ImmutableList<String> args;

        public RawOption(final OptionName name, final List<String> args) {
            this.name = Objects.requireNonNull(name, "'name' cannot be null");
            this.args = ImmutableList.copyOf(
                    Objects.requireNonNull(args, "'args' cannot be null"));
        }

        public OptionName getName() {
            return name;
        }

        public ImmutableList<String> getArgs() {
            return args;
        }
    }

    @SuppressWarnings("serial")
    private static final class ParseException extends RuntimeException {
    }
}

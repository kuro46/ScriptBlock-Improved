package com.github.kuro46.scriptblockimproved.script.option;

import arrow.core.Either;
import com.github.kuro46.commandutility.syntax.CommandSyntax;
import com.github.kuro46.commandutility.syntax.ParseErrorReason;
import com.github.kuro46.scriptblockimproved.common.tuple.Triple;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
                    final CommandSyntax syntax = handler.getSyntax();
                    final Either<kotlin.Pair<ParseErrorReason, Map<String, String>>,
                          Map<String, String>> wrappedArgs = syntax.parse(rawOption.getArgs());
                    if (wrappedArgs.isLeft()) {
                        throw new ParseException();
                    }
                    @SuppressWarnings("unchecked")
                    final Map<String, String> args =
                        ((Either.Right<Map<String, String>>) wrappedArgs).getB();

                    return Triple.of(name, new Arguments(args), handler);
                }) // Mapped to Triple<OptionName, Arguments, OptionHandler>
                .map(data -> new Option(data.left(), data.middle())) // Mapped to Option
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

    private static final class ParseException extends RuntimeException {
    }
}

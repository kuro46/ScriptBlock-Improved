package com.github.kuro46.scriptblockimproved.script.option;

import com.github.kuro46.scriptblockimproved.common.MessageUtils;
import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.common.command.ParsedArgs;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.NonNull;

public final class OptionParser {

    private static final Pattern OPTIONS_PATTERN = Pattern.compile("\\[(.+?[^\\\\])]");
    private static final Pattern OPTION_PATTERN = Pattern.compile("@([^ ]+) ?(.*)");

    private OptionParser() {
        throw new UnsupportedOperationException("Static method only");
    }

    public static OptionList parse(
            @NonNull final OptionHandlerMap handlers,
            @NonNull final String source) throws ParseException {
        final List<Option> options = new ArrayList<>();
        for (final String stringOption : splitStringOptions(source)) {
            final RawOption rawOption = splitStringOption(stringOption).orElseThrow(() -> {
                return new ParseException("Illegal script format");
            });
            final OptionName name = rawOption.getName();
            final OptionHandler handler = handlers.get(name).orElseThrow(() -> {
                return new ParseException(String.format("'@%s' is unknown option", name));
            });
            final Args args = handler.getArgs();
            final ParsedArgs parsedArgs = args.parse(rawOption.getArgs()).orElseThrow(() -> {
                return new ParseException(String.format("Incorrect usage of '@%s'", name));
            });
            final Option option = new Option(name, parsedArgs);
            options.add(option);
        }
        return new OptionList(options);
    }

    /**
     * Split an option in String to an option in RawOption.
     */
    private static Optional<RawOption> splitStringOption(final String str) {
        final Matcher optionMatcher = OPTION_PATTERN.matcher(str);
        if (!optionMatcher.find()) return Optional.empty();
        final String value = MessageUtils.translateColorCodes('&', optionMatcher.group(2));
        final RawOption rawOption = new RawOption(
                OptionName.of(optionMatcher.group(1)),
                Arrays.asList(value.split(" ")));
        return Optional.of(rawOption);
    }

    /**
     * Split options in String to List&lt;an option&gt;
     * <pre>&quot;[@opt1 arg][@opt2 arg]&quot; to &quot;@opt1 arg&quot;,&quot;@opt2 arg&quot;</pre>
     */
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

        @NonNull
        @Getter
        private final OptionName name;
        @NonNull
        @Getter
        private final ImmutableList<String> args;

        public RawOption(@NonNull final OptionName name, @NonNull final List<String> args) {
            this.name = name;
            this.args = ImmutableList.copyOf(args);
        }
    }
}

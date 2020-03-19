package com.github.kuro46.scriptblockimproved;

import com.github.kuro46.scriptblockimproved.common.MessageUtils;
import com.github.kuro46.scriptblockimproved.handler.OptionHandler;
import com.github.kuro46.scriptblockimproved.handler.ValidationResult;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.NonNull;

public final class OptionListParser {

    private static final Pattern OPTIONS_PATTERN = Pattern.compile("\\[(.+?[^\\\\])]");
    private static final Pattern OPTION_PATTERN = Pattern.compile("@([^ ]+) ?(.*)");
    private static final Splitter OPTION_SPLITTER = Splitter.on(' ').omitEmptyStrings();

    private OptionListParser() {
        throw new UnsupportedOperationException("Static method only");
    }

    public static ImmutableList<Script.Option> parse(@NonNull final String string) throws ParseException {
        final ImmutableList<String> stringOptions = stringOptions(string);
        final List<Script.Option> options = new ArrayList<>(stringOptions.size());
        for (String stringOption : stringOptions) {
            final Script.Option option = stringOption(stringOption);
            final OptionHandler handler = ScriptBlockImproved.getInstance().getScriptHandler()
                .getHandler(option.getName()).orElseThrow(() -> new ParseException("Cannot find handler for " + option.getName()));
            if (handler.validateArgs(option.getArgs()) == ValidationResult.INVALID) {
                throw new ParseException(String.format("Invalid args for '%s' : %s", option.getName(), option.getArgs()));
            }
            options.add(option);
        }
        return ImmutableList.copyOf(options);
    }

    private static Script.Option stringOption(@NonNull String source) throws ParseException {
        final Matcher optionMatcher = OPTION_PATTERN.matcher(source);
        if (!optionMatcher.find()) {
            throw new ParseException(String.format("Cannot parse option: %s", source));
        }
        return new Script.Option(
            optionMatcher.group(1),
            ImmutableList.copyOf(OPTION_SPLITTER.split(MessageUtils.translateColorCodes('&', optionMatcher.group(2)))));
    }

    private static ImmutableList<String> stringOptions(@NonNull final String source) {
        final Matcher optionsMatcher = OPTIONS_PATTERN.matcher(source);
        final List<String> options = new ArrayList<>();
        while (optionsMatcher.find()) {
            options.add(optionsMatcher.group(1));
        }
        if (options.isEmpty()) {
            options.add(source);
        }
        return ImmutableList.copyOf(options);
    }

    @SuppressWarnings("serial")
    public static final class ParseException extends Exception {
        public ParseException(@NonNull final String message) {
            super(message);
        }
    }
}

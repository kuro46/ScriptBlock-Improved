package com.github.kuro46.scriptblockimproved.script.author;

public final class ConsoleAuthorData implements AuthorData {

    private static final ConsoleAuthorData INSTANCE = new ConsoleAuthorData();

    private ConsoleAuthorData() {
    }

    public static ConsoleAuthorData getInstance() {
        return INSTANCE;
    }

    @Override
    public String getName() {
        return "CONSOLE";
    }

    @Override
    public String toString() {
        return getName();
    }
}

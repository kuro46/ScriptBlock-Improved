package com.github.kuro46.scriptblockimproved.script.author;

public final class SystemAuthorData implements AuthorData {

    private static final SystemAuthorData INSTANCE = new SystemAuthorData();

    private SystemAuthorData() {
    }

    public static SystemAuthorData getInstance() {
        return INSTANCE;
    }

    @Override
    public String getName() {
        return "SYSTEM";
    }
}

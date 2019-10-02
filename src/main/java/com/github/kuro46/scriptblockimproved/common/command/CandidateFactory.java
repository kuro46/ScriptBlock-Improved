package com.github.kuro46.scriptblockimproved.common.command;

import java.util.List;

@FunctionalInterface
public interface CandidateFactory {

    List<String> create(final String currentValue);
}

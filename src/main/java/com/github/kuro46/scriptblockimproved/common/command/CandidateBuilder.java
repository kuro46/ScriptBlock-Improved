package com.github.kuro46.scriptblockimproved.common.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class CandidateBuilder {

    private final Map<ArgName, CandidateFactory> factories = new HashMap<>();

    public CandidateBuilder when(
            final String name,
            final CandidateFactory candidateFactory) {
        return when(ArgName.of(name), candidateFactory);
    }

    public CandidateBuilder when(
            final ArgName name,
            final CandidateFactory candidateFactory) {
        factories.put(name, candidateFactory);

        return this;
    }

    public List<String> build(final ArgName name, final String currentValue) {
        final CandidateFactory factory = factories.get(name);
        return Optional.ofNullable(factory)
            .map(f -> f.create(currentValue))
            .orElse(null);
    }
}

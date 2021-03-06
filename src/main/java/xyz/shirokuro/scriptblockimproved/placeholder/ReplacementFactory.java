package xyz.shirokuro.scriptblockimproved.placeholder;

/**
 * Replacement factory.
 */
@FunctionalInterface
public interface ReplacementFactory {

    /**
     * Creates replacement.
     *
     * @param data data
     * @return replacement
     */
    String create(SourceData data);
}

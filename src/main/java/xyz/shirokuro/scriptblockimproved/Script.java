package xyz.shirokuro.scriptblockimproved;

import com.google.common.collect.ImmutableList;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public final class Script {

    /**
     * Author of this script.
     */
    @Getter
    private final Author author;
    /**
     * Timestamp that this script is created
     */
    @Getter
    private final OffsetDateTime createdAt;
    @Getter
    private final String triggerName;
    @Getter
    private final ImmutableList<Option> options;

    @AllArgsConstructor
    @ToString
    @EqualsAndHashCode
    @Builder
    public static final class Option {

        @Getter
        private final String name;
        @Getter
        private final ImmutableList<String> args;
    }
}

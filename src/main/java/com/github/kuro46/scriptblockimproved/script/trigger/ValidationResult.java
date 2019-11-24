package com.github.kuro46.scriptblockimproved.script.trigger;

import java.util.Optional;
import lombok.NonNull;

/**
 * ValidationResult is a result of Trigger#validateCondition.<br>
 * To get an invalid result, you have to use ValidateResult#invalid.<br>
 * To get a valid result, you have to use ValidateResult#valid.
 */
public final class ValidationResult {

    private static final ValidationResult INVALID = new ValidationResult(null);

    private final AdditionalEventData data;

    private ValidationResult(final AdditionalEventData data) {
        this.data = data;
    }

    /**
     * Returns an invalid result.
     *
     * @return An invalid result
     */
    public static ValidationResult invalid() {
        return INVALID;
    }

    /**
     * Returns a valid result.
     *
     * @param data Additional data of the validated event.
     * @return A valid result
     */
    public static ValidationResult valid(@NonNull final AdditionalEventData data) {
        return new ValidationResult(data);
    }

    public Optional<AdditionalEventData> getData() {
        return Optional.ofNullable(data);
    }

    public boolean isValid() {
        return data != null;
    }

    public boolean isInvalid() {
        return data == null;
    }
}

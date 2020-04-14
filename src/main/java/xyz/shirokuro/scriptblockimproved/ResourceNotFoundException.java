package xyz.shirokuro.scriptblockimproved;

import java.io.IOException;
import java.util.Objects;

public final class ResourceNotFoundException extends IOException {

    private final String resourceName;

    public ResourceNotFoundException(String resourceName) {
        super("Failed to find resource: '" + resourceName + "'");
        this.resourceName = Objects.requireNonNull(resourceName);
    }

    public String getResourceName() {
        return resourceName;
    }
}

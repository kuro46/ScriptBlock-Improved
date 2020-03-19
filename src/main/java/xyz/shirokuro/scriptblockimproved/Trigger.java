package xyz.shirokuro.scriptblockimproved;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@ToString
public final class Trigger {

    private static int currentId = 0;

    @Getter
    private final int id;
    @Getter
    private final String name;
    @Getter
    private final Runnable onUnregistered;
    @Getter
    private boolean isUnregistered = false;

    public Trigger(@NonNull final String name, @NonNull final Runnable onUnregistered) {
        currentId++;
        this.id = currentId;
        this.name = name;
        this.onUnregistered = onUnregistered;
    }

    public void unregister() {
        onUnregistered.run();
        isUnregistered = true;
    }
}

package im.slayclient.addon;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class AddonDescriptor {
    private final String id;
    private final String name;
    private final String author;
    private final String description;
    private final SlayAddon instance;
}

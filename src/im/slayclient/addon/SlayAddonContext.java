package im.slayclient.addon;

import im.expensive.Expensive;
import net.minecraft.client.Minecraft;

/**
 * Lightweight context passed to addons on initialization to expose useful services.
 */
public class SlayAddonContext {

    private final Minecraft minecraft;
    private final Expensive expensive;

    public SlayAddonContext(Minecraft minecraft, Expensive expensive) {
        this.minecraft = minecraft;
        this.expensive = expensive;
    }

    public Minecraft getMinecraft() {
        return minecraft;
    }

    public Expensive getExpensive() {
        return expensive;
    }
}

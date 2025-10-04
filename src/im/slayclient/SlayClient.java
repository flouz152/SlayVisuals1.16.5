package im.slayclient;

import im.slayclient.addon.AddonManager;
import im.slayclient.settings.SlayClientSettings;
import im.slayclient.ui.SlayClientScreen;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;

import java.io.File;

/**
 * Entry point for the SlayClient experience. The class exposes managers that can be used
 * by the legacy Expensive code-base without forcing a tight coupling. This allows the
 * code to be assembled into a standalone "SlayClient" distribution where addons and
 * custom UI live beside the existing module system.
 */
public final class SlayClient {

    @Getter
    private static final SlayClient instance = new SlayClient();

    private final File clientDirectory;
    private final File addonDirectory;
    private final AddonManager addonManager;
    private final SlayClientSettings settings = new SlayClientSettings();
    private SlayClientScreen cachedScreen;

    private SlayClient() {
        this.clientDirectory = new File(Minecraft.getInstance().gameDir, "slayclient");
        if (!clientDirectory.exists()) {
            clientDirectory.mkdirs();
        }
        this.addonDirectory = new File(clientDirectory, "addons");
        if (!addonDirectory.exists()) {
            addonDirectory.mkdirs();
        }
        this.addonManager = new AddonManager(addonDirectory);
    }

    public void initialize() {
        addonManager.discoverAddons();
    }

    public AddonManager getAddonManager() {
        return addonManager;
    }

    public SlayClientSettings getSettings() {
        return settings;
    }

    public File getClientDirectory() {
        return clientDirectory;
    }

    public void openScreen() {
        if (cachedScreen == null) {
            cachedScreen = new SlayClientScreen(new StringTextComponent("SlayClient"));
        }
        Minecraft.getInstance().displayGuiScreen(cachedScreen);
    }
}

package im.slayclient.settings;

import lombok.Getter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Lightweight container for bespoke SlayClient options that are not backed by the
 * existing Expensive module system. Settings are persisted inside the SlayClient
 * directory so players can carry their preferences between launches.
 */
@Getter
public class SlayClientSettings {

    private final File settingsFile;

    private boolean performanceTweaks = true;
    private boolean hitColorEnabled = true;
    private boolean viewModelExtended = false;
    private boolean zoomAnimation = true;
    private boolean compactHotbar = false;
    private boolean squadOverlay = true;
    private boolean voiceChatBridge = false;
    private boolean quickMacroWheel = true;
    private boolean replayStudio = false;
    private boolean skyboxThemes = true;

    public SlayClientSettings(File baseDirectory) {
        this.settingsFile = new File(baseDirectory, "settings.properties");
        load();
    }

    public void ensureDefaultsWritten() {
        if (!settingsFile.exists()) {
            save();
        }
    }

    public void togglePerformanceTweaks() {
        performanceTweaks = !performanceTweaks;
        save();
    }

    public void toggleHitColorEnabled() {
        hitColorEnabled = !hitColorEnabled;
        save();
    }

    public void toggleViewModelExtended() {
        viewModelExtended = !viewModelExtended;
        save();
    }

    public void toggleZoomAnimation() {
        zoomAnimation = !zoomAnimation;
        save();
    }

    public void toggleCompactHotbar() {
        compactHotbar = !compactHotbar;
        save();
    }

    public void toggleSquadOverlay() {
        squadOverlay = !squadOverlay;
        save();
    }

    public void toggleVoiceChatBridge() {
        voiceChatBridge = !voiceChatBridge;
        save();
    }

    public void toggleQuickMacroWheel() {
        quickMacroWheel = !quickMacroWheel;
        save();
    }

    public void toggleReplayStudio() {
        replayStudio = !replayStudio;
        save();
    }

    public void toggleSkyboxThemes() {
        skyboxThemes = !skyboxThemes;
        save();
    }

    private void load() {
        if (!settingsFile.exists()) {
            return;
        }
        Properties properties = new Properties();
        try (FileInputStream stream = new FileInputStream(settingsFile)) {
            properties.load(stream);
            performanceTweaks = getBoolean(properties, "performanceTweaks", performanceTweaks);
            hitColorEnabled = getBoolean(properties, "hitColorEnabled", hitColorEnabled);
            viewModelExtended = getBoolean(properties, "viewModelExtended", viewModelExtended);
            zoomAnimation = getBoolean(properties, "zoomAnimation", zoomAnimation);
            compactHotbar = getBoolean(properties, "compactHotbar", compactHotbar);
            squadOverlay = getBoolean(properties, "squadOverlay", squadOverlay);
            voiceChatBridge = getBoolean(properties, "voiceChatBridge", voiceChatBridge);
            quickMacroWheel = getBoolean(properties, "quickMacroWheel", quickMacroWheel);
            replayStudio = getBoolean(properties, "replayStudio", replayStudio);
            skyboxThemes = getBoolean(properties, "skyboxThemes", skyboxThemes);
        } catch (IOException ignored) {
        }
    }

    private void save() {
        Properties properties = new Properties();
        properties.setProperty("performanceTweaks", Boolean.toString(performanceTweaks));
        properties.setProperty("hitColorEnabled", Boolean.toString(hitColorEnabled));
        properties.setProperty("viewModelExtended", Boolean.toString(viewModelExtended));
        properties.setProperty("zoomAnimation", Boolean.toString(zoomAnimation));
        properties.setProperty("compactHotbar", Boolean.toString(compactHotbar));
        properties.setProperty("squadOverlay", Boolean.toString(squadOverlay));
        properties.setProperty("voiceChatBridge", Boolean.toString(voiceChatBridge));
        properties.setProperty("quickMacroWheel", Boolean.toString(quickMacroWheel));
        properties.setProperty("replayStudio", Boolean.toString(replayStudio));
        properties.setProperty("skyboxThemes", Boolean.toString(skyboxThemes));
        try (FileOutputStream stream = new FileOutputStream(settingsFile)) {
            properties.store(stream, "SlayClient settings");
        } catch (IOException ignored) {
        }
    }

    private boolean getBoolean(Properties properties, String key, boolean def) {
        String value = properties.getProperty(key);
        if (value == null) {
            return def;
        }
        return Boolean.parseBoolean(value);
    }
}

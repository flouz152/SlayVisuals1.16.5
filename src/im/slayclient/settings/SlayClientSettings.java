package im.slayclient.settings;

import lombok.Getter;
import lombok.Setter;

/**
 * Lightweight container for bespoke SlayClient options that are not backed by the
 * existing Expensive module system. Persistence is handled externally by the
 * surrounding client distribution.
 */
@Getter
@Setter
public class SlayClientSettings {

    private boolean performanceTweaks = true;
    private boolean hitColorEnabled = true;
    private boolean viewModelExtended = false;
    private boolean zoomAnimation = true;
    private boolean compactHotbar = false;

    public void togglePerformanceTweaks() {
        performanceTweaks = !performanceTweaks;
    }

    public void toggleHitColorEnabled() {
        hitColorEnabled = !hitColorEnabled;
    }

    public void toggleViewModelExtended() {
        viewModelExtended = !viewModelExtended;
    }

    public void toggleZoomAnimation() {
        zoomAnimation = !zoomAnimation;
    }

    public void toggleCompactHotbar() {
        compactHotbar = !compactHotbar;
    }
}

package im.slayclient.ui.tabs.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.render.ColorUtils;
import im.expensive.utils.render.DisplayUtils;
import im.expensive.utils.render.font.Fonts;
import im.slayclient.SlayClient;
import im.slayclient.settings.SlayClientSettings;
import im.slayclient.ui.tabs.SlayTab;

import java.util.ArrayList;
import java.util.List;

public class ClientFeaturesTab implements SlayTab {

    private final List<ToggleButton> buttons = new ArrayList<>();

    public ClientFeaturesTab() {
        SlayClientSettings settings = SlayClient.getInstance().getSettings();
        buttons.add(new ToggleButton("Performance Tweaks", "Batch particles and simplify entity updates.", settings::isPerformanceTweaks, settings::togglePerformanceTweaks));
        buttons.add(new ToggleButton("Hit Color", "Highlight damaged entities with gradient overlays.", settings::isHitColorEnabled, settings::toggleHitColorEnabled));
        buttons.add(new ToggleButton("Extended ViewModel", "Customize hand positions for cinematic looks.", settings::isViewModelExtended, settings::toggleViewModelExtended));
        buttons.add(new ToggleButton("Zoom Animation", "Smooth zoom inspired by cinematic clients.", settings::isZoomAnimation, settings::toggleZoomAnimation));
        buttons.add(new ToggleButton("Compact Hotbar", "Minimalistic HUD layout for PvP.", settings::isCompactHotbar, settings::toggleCompactHotbar));
    }

    @Override
    public String title() {
        return "Client";
    }

    @Override
    public void render(MatrixStack stack, float x, float y, float width, float height, int mouseX, int mouseY, float partialTicks) {
        Fonts.montserrat.drawText(stack, "Client Enhancements", x, y, -1, 8, 0.05f);
        Fonts.montserrat.drawText(stack, "Preferences persist in slayclient/settings.properties.", x, y + 14, ColorUtils.rgba(170, 170, 180, 220), 6, 0.05f);
        float offsetY = y + 30;
        for (ToggleButton button : buttons) {
            float entryHeight = 62;
            DisplayUtils.drawRoundedRect(x, offsetY, width, entryHeight, 6, ColorUtils.rgba(24, 24, 32, 140));
            Fonts.montserrat.drawText(stack, button.title, x + 12, offsetY + 12, -1, 8, 0.05f);
            Fonts.montserrat.drawText(stack, button.description, x + 12, offsetY + 26, ColorUtils.rgba(170, 170, 180, 220), 6, 0.05f);

            float toggleWidth = 60;
            float toggleHeight = 20;
            float toggleX = x + width - toggleWidth - 12;
            float toggleY = offsetY + 16;
            boolean hovered = MathUtil.isHovered(mouseX, mouseY, toggleX, toggleY, toggleWidth, toggleHeight);
            boolean enabled = button.state.getAsBoolean();
            int background = enabled ? ColorUtils.rgba(76, 115, 228, hovered ? 230 : 200) : ColorUtils.rgba(46, 46, 56, hovered ? 200 : 160);
            DisplayUtils.drawRoundedRect(toggleX, toggleY, toggleWidth, toggleHeight, 8, background);
            Fonts.montserrat.drawCenteredText(stack, enabled ? "ON" : "OFF", toggleX + toggleWidth / 2f, toggleY + 9, -1, 7, 0.05f);

            button.lastBoundsX = toggleX;
            button.lastBoundsY = toggleY;
            button.lastBoundsW = toggleWidth;
            button.lastBoundsH = toggleHeight;

            offsetY += entryHeight + 10;
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return;
        }
        for (ToggleButton toggleButton : buttons) {
            if (MathUtil.isHovered((float) mouseX, (float) mouseY, toggleButton.lastBoundsX, toggleButton.lastBoundsY, toggleButton.lastBoundsW, toggleButton.lastBoundsH)) {
                toggleButton.toggle.run();
            }
        }
    }

    private static final class ToggleButton {
        private final String title;
        private final String description;
        private final java.util.function.BooleanSupplier state;
        private final Runnable toggle;
        private float lastBoundsX;
        private float lastBoundsY;
        private float lastBoundsW;
        private float lastBoundsH;

        private ToggleButton(String title, String description, java.util.function.BooleanSupplier state, Runnable toggle) {
            this.title = title;
            this.description = description;
            this.state = state;
            this.toggle = toggle;
        }
    }
}

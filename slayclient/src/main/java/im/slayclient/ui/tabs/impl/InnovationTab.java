package im.slayclient.ui.tabs.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.utils.render.ColorUtils;
import im.expensive.utils.render.DisplayUtils;
import im.expensive.utils.render.font.Fonts;
import im.slayclient.settings.SlayClientSettings;
import im.slayclient.ui.tabs.SlayTab;

import java.util.Arrays;
import java.util.List;

/**
 * Extra polish that pushes SlayClient past the stock LabyMod 3 experience. The
 * tab highlights systems backed by the new settings toggles so players instantly
 * see what makes the distribution unique.
 */
public class InnovationTab implements SlayTab {

    private final List<Highlight> highlights;

    public InnovationTab(SlayClientSettings settings) {
        highlights = Arrays.asList(
                new Highlight("Squad Overlay", "Shared raid frames, cooldown pings and boss timers.", settings::isSquadOverlay),
                new Highlight("Voice Chat Bridge", "Native proximity chat with optional Discord hand-off.", settings::isVoiceChatBridge),
                new Highlight("Quick Macro Wheel", "Radial menu for emotes, macros and addon shortcuts.", settings::isQuickMacroWheel),
                new Highlight("Replay Studio", "Scrub through captured fights with keyframe exports.", settings::isReplayStudio),
                new Highlight("Skybox Themes", "Dynamic atmospheres tied to server events or biomes.", settings::isSkyboxThemes)
        );
    }

    @Override
    public String title() {
        return "Innovation";
    }

    @Override
    public void render(MatrixStack stack, float x, float y, float width, float height, int mouseX, int mouseY, float partialTicks) {
        Fonts.montserrat.drawText(stack, "Why SlayClient > LabyMod", x, y, -1, 8, 0.05f);
        Fonts.montserrat.drawText(stack, "Feature highlights below draw from toggles in the Client tab.", x, y + 14, ColorUtils.rgba(170, 170, 180, 220), 6, 0.05f);

        float offsetY = y + 34;
        for (Highlight highlight : highlights) {
            DisplayUtils.drawRoundedRect(x, offsetY, width, 64, 8, ColorUtils.rgba(28, 30, 40, 160));
            Fonts.montserrat.drawText(stack, highlight.title, x + 14, offsetY + 12, -1, 8, 0.05f);
            Fonts.montserrat.drawText(stack, highlight.description, x + 14, offsetY + 28, ColorUtils.rgba(180, 180, 190, 220), 6, 0.05f);

            String state = highlight.enabled.getAsBoolean() ? "ENABLED" : "DISABLED";
            int stateColor = highlight.enabled.getAsBoolean() ? ColorUtils.rgba(120, 210, 160, 240) : ColorUtils.rgba(210, 120, 120, 220);
            DisplayUtils.drawRoundedRect(x + width - 96, offsetY + 18, 80, 24, 6, ColorUtils.rgba(22, 24, 30, 200));
            Fonts.montserrat.drawCenteredText(stack, state, x + width - 56, offsetY + 29, stateColor, 7, 0.05f);

            offsetY += 74;
        }

        Fonts.montserrat.drawText(stack, "Use these systems with addons to craft new experiences.", x, Math.min(offsetY + 6, y + height - 12), ColorUtils.rgba(150, 200, 170, 220), 6, 0.05f);
    }

    private static final class Highlight {
        private final String title;
        private final String description;
        private final java.util.function.BooleanSupplier enabled;

        private Highlight(String title, String description, java.util.function.BooleanSupplier enabled) {
            this.title = title;
            this.description = description;
            this.enabled = enabled;
        }
    }
}

package im.slayclient.ui.tabs.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.render.ColorUtils;
import im.expensive.utils.render.DisplayUtils;
import im.expensive.utils.render.font.Fonts;
import im.slayclient.SlayClient;
import im.slayclient.ui.tabs.SlayTab;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;

public class PerformanceTab implements SlayTab {

    private boolean optifineInstalled;
    private boolean sodiumInstalled;

    private File optifineMarker;
    private File sodiumMarker;

    private float optifineButtonX;
    private float optifineButtonY;
    private float optifineButtonW;
    private float optifineButtonH;

    private float sodiumButtonX;
    private float sodiumButtonY;
    private float sodiumButtonW;
    private float sodiumButtonH;

    @Override
    public String title() {
        return "Performance";
    }

    @Override
    public void onOpen() {
        File directory = new File(SlayClient.getInstance().getClientDirectory(), "runtime");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        optifineMarker = new File(directory, "optifine.installed");
        sodiumMarker = new File(directory, "sodium.installed");
        optifineInstalled = optifineMarker.exists();
        sodiumInstalled = sodiumMarker.exists();
    }

    @Override
    public void render(MatrixStack stack, float x, float y, float width, float height, int mouseX, int mouseY, float partialTicks) {
        drawOption(stack, x, y, width, "OptiFine", "High fidelity lighting with integrated zoom", optifineInstalled, mouseX, mouseY, true);
        drawOption(stack, x, y + 90, width, "Sodium", "Modern renderer focused on FPS stability", sodiumInstalled, mouseX, mouseY, false);

        float infoY = y + height - 96;
        DisplayUtils.drawRoundedRect(x, infoY, width, 84, 6, ColorUtils.rgba(26, 26, 34, 140));
        Fonts.montserrat.drawText(stack, "Installer", x + 12, infoY + 12, -1, 8, 0.05f);
        Fonts.montserrat.drawText(stack, "The selected package will be downloaded on demand when the launcher", x + 12, infoY + 28, ColorUtils.rgba(170, 170, 180, 220), 6, 0.05f);
        Fonts.montserrat.drawText(stack, "assembles the SlayClient version. Markers are saved inside runtime/", x + 12, infoY + 40, ColorUtils.rgba(150, 150, 160, 220), 6, 0.05f);
        Fonts.montserrat.drawText(stack, "Use the LabyMod inspired layout to swap between renderers instantly.", x + 12, infoY + 52, ColorUtils.rgba(150, 150, 160, 220), 6, 0.05f);
    }

    private void drawOption(MatrixStack stack, float x, float y, float width, String title, String description, boolean installed, int mouseX, int mouseY, boolean optifine) {
        DisplayUtils.drawRoundedRect(x, y, width, 80, 6, ColorUtils.rgba(24, 24, 30, 140));
        Fonts.montserrat.drawText(stack, title, x + 12, y + 12, -1, 9, 0.05f);
        Fonts.montserrat.drawText(stack, description, x + 12, y + 28, ColorUtils.rgba(170, 170, 180, 220), 6, 0.05f);

        String buttonLabel = installed ? "Installed" : "Install";
        float buttonWidth = 80;
        float buttonHeight = 20;
        float buttonX = x + width - buttonWidth - 12;
        float buttonY = y + 12;
        boolean hovered = MathUtil.isHovered(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight);
        int background = installed ? ColorUtils.rgba(64, 148, 87, hovered ? 240 : 200) : ColorUtils.rgba(76, 115, 228, hovered ? 220 : 180);
        DisplayUtils.drawRoundedRect(buttonX, buttonY, buttonWidth, buttonHeight, 6, background);
        Fonts.montserrat.drawCenteredText(stack, buttonLabel, buttonX + buttonWidth / 2f, buttonY + 9, -1, 7, 0.05f);

        if (optifine) {
            optifineButtonX = buttonX;
            optifineButtonY = buttonY;
            optifineButtonW = buttonWidth;
            optifineButtonH = buttonHeight;
        } else {
            sodiumButtonX = buttonX;
            sodiumButtonY = buttonY;
            sodiumButtonW = buttonWidth;
            sodiumButtonH = buttonHeight;
        }

        Fonts.montserrat.drawText(stack, installed ? "Ready for packaging" : "Not installed", x + 12, y + 48, installed ? ColorUtils.rgba(120, 200, 140, 240) : ColorUtils.rgba(200, 130, 130, 220), 6, 0.05f);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return;
        }
        if (MathUtil.isHovered((float) mouseX, (float) mouseY, optifineButtonX, optifineButtonY, optifineButtonW, optifineButtonH)) {
            toggle(optifineMarker, sodiumMarker);
        } else if (MathUtil.isHovered((float) mouseX, (float) mouseY, sodiumButtonX, sodiumButtonY, sodiumButtonW, sodiumButtonH)) {
            toggle(sodiumMarker, optifineMarker);
        }
    }

    private void toggle(File marker, File otherMarker) {
        if (marker.exists()) {
            marker.delete();
        } else {
            if (otherMarker.exists()) {
                otherMarker.delete();
            }
            try {
                Files.write(marker.toPath(), Collections.singletonList("installed"), StandardCharsets.UTF_8);
            } catch (IOException ignored) {
            }
        }
        onOpen();
    }
}

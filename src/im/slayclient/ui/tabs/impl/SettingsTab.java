package im.slayclient.ui.tabs.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.utils.render.ColorUtils;
import im.expensive.utils.render.DisplayUtils;
import im.expensive.utils.render.font.Fonts;
import im.slayclient.SlayClient;
import im.slayclient.ui.tabs.SlayTab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.VideoSettingsScreen;
import net.minecraft.client.gui.screen.options.OptionsScreen;

import java.io.File;

public class SettingsTab implements SlayTab {

    private float buttonX;
    private float buttonY;
    private float buttonW;
    private float buttonH;
    private boolean optifineSelected;
    private boolean sodiumSelected;

    @Override
    public String title() {
        return "Settings";
    }

    @Override
    public void render(MatrixStack stack, float x, float y, float width, float height, int mouseX, int mouseY, float partialTicks) {
        refreshRendererState();
        Fonts.montserrat.drawText(stack, "Game Settings", x, y, -1, 8, 0.05f);
        Fonts.montserrat.drawText(stack, "Open the vanilla options screen with one click.", x, y + 14, ColorUtils.rgba(170, 170, 180, 220), 6, 0.05f);

        buttonW = 160;
        buttonH = 28;
        buttonX = x;
        buttonY = y + 40;

        boolean hovered = mouseX >= buttonX && mouseX <= buttonX + buttonW && mouseY >= buttonY && mouseY <= buttonY + buttonH;
        int background = hovered ? ColorUtils.rgba(76, 115, 228, 220) : ColorUtils.rgba(76, 115, 228, 180);
        DisplayUtils.drawRoundedRect(buttonX, buttonY, buttonW, buttonH, 8, background);
        Fonts.montserrat.drawCenteredText(stack, "Open Settings", buttonX + buttonW / 2f, buttonY + 12, -1, 7, 0.05f);

        float infoY = buttonY + buttonH + 20;
        DisplayUtils.drawRoundedRect(x, infoY, width, height - (infoY - y), 6, ColorUtils.rgba(26, 26, 34, 140));
        Fonts.montserrat.drawText(stack, "Renderer Integration", x + 12, infoY + 12, -1, 7, 0.05f);
        String state = optifineSelected ? "OptiFine" : sodiumSelected ? "Sodium" : "Vanilla";
        Fonts.montserrat.drawText(stack, "Current preference: " + state, x + 12, infoY + 26, ColorUtils.rgba(170, 170, 180, 220), 6, 0.05f);
        Fonts.montserrat.drawText(stack, "Switch profiles in the Performance tab to change available options.", x + 12, infoY + 38, ColorUtils.rgba(150, 150, 160, 220), 6, 0.05f);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return;
        }
        if (mouseX >= buttonX && mouseX <= buttonX + buttonW && mouseY >= buttonY && mouseY <= buttonY + buttonH) {
            openVanillaSettings();
        }
    }

    private void openVanillaSettings() {
        Minecraft minecraft = Minecraft.getInstance();
        Screen prev = minecraft.currentScreen;
        Screen target = new OptionsScreen(prev, minecraft.gameSettings);
        if (optifineSelected) {
            target = new VideoSettingsScreen(prev, minecraft.gameSettings);
        }
        minecraft.displayGuiScreen(target);
    }

    private void refreshRendererState() {
        File runtimeDir = new File(SlayClient.getInstance().getClientDirectory(), "runtime");
        File optifineMarker = new File(runtimeDir, "optifine.installed");
        File sodiumMarker = new File(runtimeDir, "sodium.installed");
        optifineSelected = optifineMarker.exists();
        sodiumSelected = sodiumMarker.exists();
    }
}

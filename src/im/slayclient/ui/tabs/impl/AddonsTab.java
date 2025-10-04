package im.slayclient.ui.tabs.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.utils.render.ColorUtils;
import im.expensive.utils.render.DisplayUtils;
import im.expensive.utils.render.font.Fonts;
import im.slayclient.SlayClient;
import im.slayclient.addon.AddonDescriptor;
import im.slayclient.ui.tabs.SlayTab;

import java.util.List;

public class AddonsTab implements SlayTab {
    @Override
    public String title() {
        return "Addons";
    }

    @Override
    public void onOpen() {
        SlayClient.getInstance().getAddonManager().discoverAddons();
    }

    @Override
    public void render(MatrixStack stack, float x, float y, float width, float height, int mouseX, int mouseY, float partialTicks) {
        Fonts.montserrat.drawText(stack, "Installed Addons", x, y, -1, 8, 0.05f);
        float offsetY = y + 18;
        List<AddonDescriptor> addons = SlayClient.getInstance().getAddonManager().getLoadedAddons();
        if (addons.isEmpty()) {
            Fonts.montserrat.drawText(stack, "Drop Java 19 compiled jars into /slayclient/addons", x, offsetY, ColorUtils.rgba(180, 180, 190, 200), 6, 0.05f);
            offsetY += 12;
            Fonts.montserrat.drawText(stack, "Use META-INF/services to register implementations of SlayAddon", x, offsetY, ColorUtils.rgba(150, 150, 160, 200), 6, 0.05f);
            offsetY += 12;
        } else {
            for (AddonDescriptor descriptor : addons) {
                DisplayUtils.drawRoundedRect(x, offsetY - 4, width, 34, 6, ColorUtils.rgba(26, 26, 32, 150));
                Fonts.montserrat.drawText(stack, descriptor.getName(), x + 8, offsetY, -1, 7, 0.05f);
                Fonts.montserrat.drawText(stack, "Author: " + descriptor.getAuthor(), x + 8, offsetY + 10, ColorUtils.rgba(170, 170, 180, 220), 6, 0.05f);
                Fonts.montserrat.drawText(stack, descriptor.getDescription(), x + 8, offsetY + 20, ColorUtils.rgba(150, 150, 160, 200), 6, 0.05f);
                offsetY += 38;
            }
        }

        float documentationY = y + height / 2f + 12;
        Fonts.montserrat.drawText(stack, "Documentation", x, documentationY, -1, 8, 0.05f);
        documentationY += 18;
        DisplayUtils.drawRoundedRect(x, documentationY - 10, width, 80, 6, ColorUtils.rgba(28, 28, 36, 140));
        Fonts.montserrat.drawText(stack, "API Entry Point", x + 8, documentationY, ColorUtils.rgba(170, 170, 180, 220), 6, 0.05f);
        Fonts.montserrat.drawText(stack, "im.slayclient.addon.SlayAddon", x + 8, documentationY + 10, -1, 7, 0.05f);
        Fonts.montserrat.drawText(stack, "Context: SlayAddonContext provides Minecraft + Expensive access", x + 8, documentationY + 20, ColorUtils.rgba(170, 170, 180, 220), 6, 0.05f);
        Fonts.montserrat.drawText(stack, "Use services loader and java 19 language features freely.", x + 8, documentationY + 30, ColorUtils.rgba(150, 150, 160, 220), 6, 0.05f);

        float rejectedY = documentationY + 50;
        List<java.io.File> rejected = SlayClient.getInstance().getAddonManager().getRejectedAddons();
        if (!rejected.isEmpty()) {
            DisplayUtils.drawRoundedRect(x, rejectedY - 8, width, 48, 6, ColorUtils.rgba(34, 26, 30, 160));
            Fonts.montserrat.drawText(stack, "Rejected Addons", x + 8, rejectedY, ColorUtils.rgba(230, 120, 120, 220), 6, 0.05f);
            rejectedY += 12;
            for (java.io.File file : rejected) {
                Fonts.montserrat.drawText(stack, file.getName() + " (requires Java 19 class files)", x + 8, rejectedY, ColorUtils.rgba(210, 120, 120, 220), 6, 0.05f);
                rejectedY += 10;
            }
        }
    }
}

package im.slayclient.ui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import im.expensive.utils.client.ClientUtil;
import im.expensive.utils.client.Vec2i;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.render.ColorUtils;
import im.expensive.utils.render.DisplayUtils;
import im.expensive.utils.render.KawaseBlur;
import im.expensive.utils.render.Scissor;
import im.expensive.utils.render.font.Fonts;
import im.slayclient.SlayClient;
import im.slayclient.ui.tabs.SlayTab;
import im.slayclient.ui.tabs.impl.AddonsTab;
import im.slayclient.ui.tabs.impl.ClientFeaturesTab;
import im.slayclient.ui.tabs.impl.PerformanceTab;
import im.slayclient.ui.tabs.impl.SettingsTab;
import im.slayclient.ui.tabs.impl.InnovationTab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;

public class SlayClientScreen extends Screen {

    private final List<SlayTab> tabs = new ArrayList<>();
    private final List<TabHitbox> tabHitboxes = new ArrayList<>();
    private SlayTab activeTab;

    private float scale = 1.0f;
    private float animation = 0f;

    public SlayClientScreen(ITextComponent title) {
        super(title);
        tabs.add(new AddonsTab());
        tabs.add(new PerformanceTab());
        tabs.add(new SettingsTab());
        tabs.add(new ClientFeaturesTab());
        tabs.add(new InnovationTab(SlayClient.getInstance().getSettings()));
        activeTab = tabs.get(0);
    }

    @Override
    protected void init() {
        animation = 0;
        activeTab.onOpen();
        SlayClient.getInstance().getAddonManager().writeTemplateAddon();
        super.init();
    }

    @Override
    public void tick() {
        animation = MathUtil.fast(animation, 1.0f, 10);
        if (activeTab != null) {
            activeTab.tick();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        KawaseBlur.blur.updateBlur(3, 3);
        mc.gameRenderer.setupOverlayRendering(2);
        animation = MathUtil.fast(animation, 1.0f, 12);

        float width = 540;
        float height = 320;

        updateScale(width);

        int windowWidth = ClientUtil.calc(mc.getMainWindow().getScaledWidth());
        int windowHeight = ClientUtil.calc(mc.getMainWindow().getScaledHeight());

        Vec2i fixMouse = adjustMouse(mouseX, mouseY);
        Vec2i scaledMouse = ClientUtil.getMouse(fixMouse.getX(), fixMouse.getY());
        mouseX = scaledMouse.getX();
        mouseY = scaledMouse.getY();

        float x = (windowWidth - width * scale) / 2f;
        float y = (windowHeight - height * scale) / 2f;

        GlStateManager.pushMatrix();
        GlStateManager.translatef(windowWidth / 2f, windowHeight / 2f, 0);
        GlStateManager.scaled(scale * animation, scale * animation, 1);
        GlStateManager.translatef(-windowWidth / 2f, -windowHeight / 2f, 0);

        DisplayUtils.drawRoundedRect(x, y, width, height, new Vector4f(10, 10, 10, 10), ColorUtils.rgba(15, 15, 17, 210));
        drawHeader(matrixStack, x, y, width);
        drawTabs(matrixStack, x, y, width, mouseX, mouseY);
        drawContent(matrixStack, x, y, width, height, mouseX, mouseY, partialTicks);

        GlStateManager.popMatrix();
        mc.gameRenderer.setupOverlayRendering();
    }

    private void drawHeader(MatrixStack stack, float x, float y, float width) {
        Fonts.montserrat.drawText(stack, "SlayClient", x + 16, y + 18, -1, 12, 0.05f);
        Fonts.montserrat.drawText(stack, "Modular experience inspired by LabyMod 3", x + 16, y + 32, ColorUtils.rgba(170, 170, 180, 180), 6, 0.05f);
        Fonts.montserrat.drawText(stack, "…but upgraded with party overlays, replay studio and more.", x + 16, y + 42, ColorUtils.rgba(120, 210, 170, 220), 6, 0.05f);
    }

    private void drawTabs(MatrixStack stack, float x, float y, float width, int mouseX, int mouseY) {
        float tabX = x + 16;
        float tabY = y + 56;
        float tabSpacing = 6;

        tabHitboxes.clear();

        for (SlayTab tab : tabs) {
            String title = tab.title();
            float textWidth = Fonts.montserrat.getWidth(title, 7);
            float tabWidth = textWidth + 20;
            boolean hovered = MathUtil.isHovered(mouseX, mouseY, tabX, tabY, tabWidth, 20);
            int background = hovered || tab == activeTab ? ColorUtils.rgba(46, 46, 56, 180) : ColorUtils.rgba(26, 26, 32, 160);
            DisplayUtils.drawRoundedRect(tabX, tabY, tabWidth, 20, 6, background);
            Fonts.montserrat.drawCenteredText(stack, title, tabX + tabWidth / 2f, tabY + 9, -1, 7, 0.05f);
            tabHitboxes.add(new TabHitbox(tab, tabX, tabY, tabWidth, 20));
            tabX += tabWidth + tabSpacing;
        }
    }

    private void selectTab(SlayTab tab) {
        if (tab == activeTab) {
            return;
        }
        activeTab = tab;
        activeTab.onOpen();
    }

    private void drawContent(MatrixStack stack, float x, float y, float width, float height, int mouseX, int mouseY, float partialTicks) {
        float contentX = x + 16;
        float contentY = y + 84;
        float contentWidth = width - 32;
        float contentHeight = height - 100;

        DisplayUtils.drawRoundedRect(contentX, contentY, contentWidth, contentHeight, new Vector4f(8, 8, 8, 8), ColorUtils.rgba(13, 13, 16, 180));

        float animationValue = MathHelper.clamp(animation, 0, 1);
        float testX = contentX + (contentWidth * (1 - animationValue) / 2f);
        float testY = contentY + (contentHeight * (1 - animationValue) / 2f);
        float testW = contentWidth * animationValue;
        float testH = contentHeight * animationValue;

        Scissor.push();
        Scissor.setFromComponentCoordinates(testX, testY, testW, testH);
        if (activeTab != null) {
            activeTab.render(stack, contentX + 12, contentY + 12, contentWidth - 24, contentHeight - 24, mouseX, mouseY, partialTicks);
        }
        Scissor.unset();
        Scissor.pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Vec2i fixMouse = adjustMouse((int) mouseX, (int) mouseY);
        Vec2i scaledMouse = ClientUtil.getMouse(fixMouse.getX(), fixMouse.getY());
        mouseX = scaledMouse.getX();
        mouseY = scaledMouse.getY();
        if (button == 0) {
            for (TabHitbox hitbox : tabHitboxes) {
                if (MathUtil.isHovered((float) mouseX, (float) mouseY, hitbox.x, hitbox.y, hitbox.w, hitbox.h)) {
                    selectTab(hitbox.tab);
                    break;
                }
            }
        }
        if (activeTab != null) {
            activeTab.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void updateScale(float width) {
        float screenWidth = mc.getMainWindow().getScaledWidth();
        float required = width + 64;
        if (required >= screenWidth) {
            scale = MathHelper.clamp(screenWidth / required, 0.65f, 1.0f);
        } else {
            scale = 1.0f;
        }
    }

    private Vec2i adjustMouse(int mouseX, int mouseY) {
        int windowWidth = mc.getMainWindow().getScaledWidth();
        int windowHeight = mc.getMainWindow().getScaledHeight();
        float adjustedMouseX = (mouseX - windowWidth / 2f) / scale + windowWidth / 2f;
        float adjustedMouseY = (mouseY - windowHeight / 2f) / scale + windowHeight / 2f;
        return new Vec2i((int) adjustedMouseX, (int) adjustedMouseY);
    }
    private static final class TabHitbox {
        final SlayTab tab;
        final float x;
        final float y;
        final float w;
        final float h;

        TabHitbox(SlayTab tab, float x, float y, float w, float h) {
            this.tab = tab;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }
}

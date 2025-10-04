package im.expensive.ui.dropdown;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import im.expensive.functions.api.Category;
import im.expensive.utils.CustomFramebuffer;
import im.expensive.utils.client.IMinecraft;
import im.expensive.utils.client.ClientUtil;
import im.expensive.utils.client.Vec2i;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.render.*;
import im.expensive.utils.render.font.Fonts;
import lombok.Getter;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.util.text.ITextComponent;
import ru.hogoshi.Animation;
import ru.hogoshi.util.Easings;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class DropDown extends Screen implements IMinecraft {

    private final List<Panel> panels = new ArrayList<>();
    @Getter
    private static Animation animation = new Animation();
    private static final float GRID_SPACING = 18f;
    private static final float BOARD_HEADER = 72f;
    private static final int MAX_COLUMNS = 3;
    private float boardX;
    private float boardY;
    private float boardWidth;
    private float boardHeight;
    private int totalModules;

    public DropDown(ITextComponent titleIn) {
        super(titleIn);
        for (Category category : Category.values()) {
            if (category == Category.Theme) continue;
            panels.add(new Panel(category));
        }
        panels.add(new PanelStyle(Category.Theme));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        animation = animation.animate(1, 0.25f, Easings.EXPO_OUT);
        super.init();
    }

    public static float scale = 1.0f;

    @Override
    public void closeScreen() {
        super.closeScreen();
        GLFW.glfwSetCursor(Minecraft.getInstance().getMainWindow().getHandle(), Cursors.ARROW);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        // TODO Auto-generated method stub
        Vec2i fixMouse = adjustMouseCoordinates((int) mouseX, (int) mouseY);

        Vec2i fix = ClientUtil.getMouse(fixMouse.getX(), fixMouse.getY());
        mouseX = fix.getX();
        mouseY = fix.getY();

        for (Panel panel : panels) {
            if (MathUtil.isHovered((float) mouseX, (float) mouseY, panel.getX(), panel.getY(), panel.getWidth(),
                    panel.getHeight())) {
                panel.setScroll((float) (panel.getScroll() + (delta * 20)));
            }
        }

        // System.out.println(delta + " " + scale + " " + mouseX + " " + mouseY))
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (Panel panel : panels) {
            panel.charTyped(codePoint, modifiers);
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        KawaseBlur.blur.updateBlur(3, 3);
        mc.gameRenderer.setupOverlayRendering(2);
        animation.update();

        if (animation.getValue() < 0.1) {
            closeScreen();
        }
        updateScaleBasedOnScreenWidth();

        int windowWidth = ClientUtil.calc(mc.getMainWindow().getScaledWidth());
        int windowHeight = ClientUtil.calc(mc.getMainWindow().getScaledHeight());

        Vec2i fixMouse = adjustMouseCoordinates(mouseX, mouseY);

        Vec2i fix = ClientUtil.getMouse(fixMouse.getX(), fixMouse.getY());
        mouseX = fix.getX();
        mouseY = fix.getY();

        layoutPanels(windowWidth, windowHeight);

        Stencil.initStencilToWrite();
        GlStateManager.pushMatrix();
        GlStateManager.translatef(windowWidth / 2f, windowHeight / 2f, 0);
        GlStateManager.scaled(animation.getValue(), animation.getValue(), 1);
        GlStateManager.scaled(scale, scale, 1);
        GlStateManager.translatef(-windowWidth / 2f, -windowHeight / 2f, 0);

        DisplayUtils.drawRoundedRect(boardX, boardY, boardWidth, boardHeight, new Vector4f(20, 20, 20, 20), -1);
        GlStateManager.popMatrix();
        Stencil.readStencilBuffer(1);
        GlStateManager.bindTexture(KawaseBlur.blur.BLURRED.framebufferTexture);
        CustomFramebuffer.drawTexture();
        Stencil.uninitStencilBuffer();

        DisplayUtils.drawContrast(1 - (float) (animation.getValue() / 3f));
        DisplayUtils.drawWhite((float) animation.getValue());

        GlStateManager.pushMatrix();
        GlStateManager.translatef(windowWidth / 2f, windowHeight / 2f, 0);
        GlStateManager.scaled(animation.getValue(), animation.getValue(), 1);
        GlStateManager.scaled(scale, scale, 1);
        GlStateManager.translatef(-windowWidth / 2f, -windowHeight / 2f, 0);
        drawBoardBackground(matrixStack, mouseX, mouseY);
        for (Panel panel : panels) {
            float animationValue = (float) animation.getValue() * scale;

            float halfAnimationValueRest = (1 - animationValue) / 2f;

            float testX = panel.getX() + (panel.getWidth() * halfAnimationValueRest);
            float testY = panel.getY() + (panel.getHeight() * halfAnimationValueRest);
            float testW = panel.getWidth() * animationValue;
            float testH = panel.getHeight() * animationValue;

            testX = testX * animationValue + ((windowWidth - testW) *
                    halfAnimationValueRest);

            Scissor.push();
            Scissor.setFromComponentCoordinates(testX, testY, testW, testH - 0.5f);
            panel.render(matrixStack, mouseX, mouseY);
            Scissor.unset();
            Scissor.pop();
        }
        GlStateManager.popMatrix();
        mc.gameRenderer.setupOverlayRendering();


    }

    private void updateScaleBasedOnScreenWidth() {
        if (panels.isEmpty()) {
            scale = 1f;
            return;
        }

        final float MIN_SCALE = 0.55f;
        float requiredWidth = getRequiredBoardWidth();
        float screenWidth = mc.getMainWindow().getScaledWidth();

        if (requiredWidth >= screenWidth) {
            scale = screenWidth / requiredWidth;
            scale = MathHelper.clamp(scale, MIN_SCALE, 1.0f);
        } else {
            scale = 1f;
        }
    }

    private float getRequiredBoardWidth() {
        if (panels.isEmpty()) {
            return 0;
        }
        float panelWidth = panels.get(0).getWidth();
        int columns = Math.min(MAX_COLUMNS, Math.max(1, panels.size()));
        return columns * panelWidth + (columns + 1) * GRID_SPACING;
    }

    private void layoutPanels(int windowWidth, int windowHeight) {
        if (panels.isEmpty()) {
            boardX = boardY = boardWidth = boardHeight = 0;
            totalModules = 0;
            return;
        }

        int columnCount = Math.min(MAX_COLUMNS, Math.max(1, panels.size()));
        int rowCount = MathHelper.ceil((float) panels.size() / columnCount);

        float panelWidth = panels.get(0).getWidth();
        float panelHeight = panels.get(0).getHeight();
        float gridWidth = columnCount * panelWidth + (columnCount + 1) * GRID_SPACING;
        float gridHeight = rowCount * panelHeight + (rowCount + 1) * GRID_SPACING;

        boardWidth = gridWidth;
        boardHeight = gridHeight + BOARD_HEADER;
        boardX = windowWidth / 2f - boardWidth / 2f;
        boardY = windowHeight / 2f - boardHeight / 2f;

        totalModules = 0;

        for (int i = 0; i < panels.size(); i++) {
            Panel panel = panels.get(i);
            int column = i % columnCount;
            int row = i / columnCount;

            float panelX = boardX + GRID_SPACING + column * (panelWidth + GRID_SPACING);
            float panelY = boardY + BOARD_HEADER + GRID_SPACING + row * (panelHeight + GRID_SPACING);

            panel.setX(panelX);
            panel.setY(panelY);
            totalModules += panel.getModules().size();
        }
    }

    private void drawBoardBackground(MatrixStack stack, float mouseX, float mouseY) {
        DisplayUtils.drawRoundedRect(boardX, boardY, boardWidth, boardHeight, new Vector4f(20, 20, 20, 20),
                ColorUtils.rgba(11, 11, 15, (int) (255 * 0.8)));
        DisplayUtils.drawRoundedRect(boardX + 1.5f, boardY + 1.5f, boardWidth - 3, boardHeight - 3,
                new Vector4f(18, 18, 18, 18), ColorUtils.rgba(6, 6, 9, (int) (255 * 0.85)));

        DisplayUtils.drawRoundedRect(boardX, boardY, boardWidth, BOARD_HEADER, new Vector4f(20, 20, 10, 10),
                ColorUtils.rgba(24, 24, 32, (int) (255 * 0.9)));
        DisplayUtils.drawRectVerticalW(boardX, boardY, boardWidth, BOARD_HEADER,
                ColorUtils.rgba(76, 129, 255, 45), ColorUtils.rgba(168, 75, 255, 30));

        Fonts.montserrat.drawText(stack, "Expensive Panel Hub", boardX + GRID_SPACING,
                boardY + 26, ColorUtils.rgb(210, 213, 232), 9f, 0.1f);
        Fonts.montserrat.drawText(stack, totalModules + " modules across " + panels.size() + " categories",
                boardX + GRID_SPACING, boardY + 26 + Fonts.montserrat.getHeight(6.5f) + 4,
                ColorUtils.rgb(110, 115, 140), 6.5f, 0.05f);

        float chipY = boardY + BOARD_HEADER - 24;
        float chipX = boardX + GRID_SPACING;

        for (Panel panel : panels) {
            String label = panel.getCategory().name();
            float chipWidth = Fonts.montserrat.getWidth(label, 6.5f) + 20;
            float chipHeight = 18f;
            if (chipX + chipWidth > boardX + boardWidth - GRID_SPACING) {
                break;
            }
            boolean hovered = MathUtil.isHovered(mouseX, mouseY, panel.getX(), panel.getY(), panel.getWidth(),
                    panel.getHeight());
            int baseAlpha = hovered ? 160 : 110;

            DisplayUtils.drawRoundedRect(chipX, chipY, chipWidth, chipHeight, new Vector4f(9, 9, 9, 9),
                    ColorUtils.rgba(34, 36, 52, baseAlpha));
            DisplayUtils.drawRoundedRect(chipX + 1, chipY + 1, chipWidth - 2, chipHeight - 2,
                    new Vector4f(7, 7, 7, 7), ColorUtils.rgba(16, 17, 26, baseAlpha + 30));

            Fonts.montserrat.drawCenteredText(stack, label, chipX + chipWidth / 2f,
                    chipY + chipHeight / 2f - Fonts.montserrat.getHeight(6.5f) / 2f + 1, ColorUtils.rgb(190, 194, 214), 6.5f,
                    0.05f);

            chipX += chipWidth + 8;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Panel panel : panels) {
            panel.keyPressed(keyCode, scanCode, modifiers);
        }
        // TODO Auto-generated method stub
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            animation = animation.animate(0, 0.25f, Easings.EXPO_OUT);
            return false;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private Vec2i adjustMouseCoordinates(int mouseX, int mouseY) {
        int windowWidth = mc.getMainWindow().getScaledWidth();
        int windowHeight = mc.getMainWindow().getScaledHeight();

        float adjustedMouseX = (mouseX - windowWidth / 2f) / scale + windowWidth / 2f;
        float adjustedMouseY = (mouseY - windowHeight / 2f) / scale + windowHeight / 2f;

        return new Vec2i((int) adjustedMouseX, (int) adjustedMouseY);
    }

    private double pathX(float mouseX, float scale) {
        if (scale == 1) return mouseX;
        int windowWidth = mc.getMainWindow().scaledWidth();
        int windowHeight = mc.getMainWindow().scaledHeight();
        mouseX /= (scale);
        mouseX -= (windowWidth / 2f) - (windowWidth / 2f) * (scale);
        return mouseX;
    }

    private double pathY(float mouseY, float scale) {
        if (scale == 1) return mouseY;
        int windowWidth = mc.getMainWindow().scaledWidth();
        int windowHeight = mc.getMainWindow().scaledHeight();
        mouseY /= scale;
        mouseY -= (windowHeight / 2f) - (windowHeight / 2f) * (scale);
        return mouseY;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Vec2i fixMouse = adjustMouseCoordinates((int) mouseX, (int) mouseY);

        Vec2i fix = ClientUtil.getMouse(fixMouse.getX(), fixMouse.getY());
        mouseX = fix.getX();
        mouseY = fix.getY();

        for (Panel panel : panels) {
            panel.mouseClick((float) mouseX, (float) mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        // TODO Auto-generated method stub
        Vec2i fixMouse = adjustMouseCoordinates((int) mouseX, (int) mouseY);

        Vec2i fix = ClientUtil.getMouse(fixMouse.getX(), fixMouse.getY());
        mouseX = fix.getX();
        mouseY = fix.getY();
        for (Panel panel : panels) {
            panel.mouseRelease((float) mouseX, (float) mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

}

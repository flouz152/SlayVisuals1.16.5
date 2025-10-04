package im.expensive.ui.dropdown;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.expensive.Expensive;
import im.expensive.functions.api.Category;
import im.expensive.ui.styles.Style;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.math.Vector4i;
import im.expensive.utils.render.*;
import im.expensive.utils.render.font.Fonts;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector4f;
import org.lwjgl.glfw.GLFW;

@Getter
public class PanelStyle extends Panel {

    public PanelStyle(Category category) {
        super(category);
        // TODO Auto-generated constructor stub
    }

    float max = 0;

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {
        float header = 44f;
        float footer = 30f;
        float headerFont = 8.5f;
        setAnimatedScrool(MathUtil.fast(getAnimatedScrool(), getScroll(), 10));

        DisplayUtils.drawRoundedRect(x, y, width, height, new Vector4f(12, 12, 12, 12),
                ColorUtils.rgba(11, 11, 15, (int) (255 * 0.75)));

        DisplayUtils.drawRoundedRect(x + 1, y + 1, width - 2, height - 2, new Vector4f(10, 10, 10, 10),
                ColorUtils.rgba(6, 6, 9, (int) (255 * 0.8)));

        DisplayUtils.drawRoundedRect(x, y, width, header, new Vector4f(12, 12, 8, 8),
                ColorUtils.rgba(24, 24, 32, (int) (255 * 0.92)));

        DisplayUtils.drawRectVerticalW(x, y, width, header,
                ColorUtils.rgba(255, 177, 86, 70), ColorUtils.rgba(255, 99, 147, 45));

        DisplayUtils.drawRoundedRect(x + 6, y + header - 1.5f, width - 12, 1.5f, new Vector4f(1, 1, 1, 1),
                ColorUtils.rgba(255, 255, 255, 35));

        Fonts.montserrat.drawText(stack, getCategory().name(), x + 12,
                y + header / 2f - Fonts.montserrat.getHeight(headerFont) / 2f, ColorUtils.rgb(194, 198, 214), headerFont,
                0.1f);

        Fonts.montserrat.drawText(stack, "Switch looks in a click", x + 12,
                y + header / 2f + Fonts.montserrat.getHeight(6.5f) / 2f + 2, ColorUtils.rgb(97, 103, 130), 6.5f, 0.1f);

        drawOutline();

        if (max > height - header - footer - 10) {
            setScroll(MathHelper.clamp(getScroll(), -max + height - header - footer - 10, 0));
            setAnimatedScrool(MathHelper.clamp(getAnimatedScrool(), -max + height - header - footer - 10, 0));
        } else {
            setScroll(0);
            setAnimatedScrool(0);
        }

        float animationValue = (float) DropDown.getAnimation().getValue() * DropDown.scale;

        float halfAnimationValueRest = (1 - animationValue) / 2f;
        float contentHeight = getHeight() - header - footer;
        float testX = getX() + (getWidth() * halfAnimationValueRest);
        float testY = getY() + header + (contentHeight * halfAnimationValueRest);
        float testW = getWidth() * animationValue;
        float testH = contentHeight * animationValue;

        testX = testX * animationValue + ((Minecraft.getInstance().getMainWindow().getScaledWidth() - testW) * halfAnimationValueRest);
        Scissor.push();
        Scissor.setFromComponentCoordinates(testX, testY, testW, testH);
        int offset = 0;

        boolean hovered = false;
        float cardHeight = 34f;
        float cardSpacing = 10f;

        for (Style style : Expensive.getInstance().getStyleManager().getStyleList()) {
            float cardX = this.x + 8;
            float cardY = this.y + header + 8 + offset * (cardHeight + cardSpacing) + getAnimatedScrool();
            float cardWidth = width - 16;

            boolean current = Expensive.getInstance().getStyleManager().getCurrentStyle() == style;
            boolean cardHovered = MathUtil.isHovered(mouseX, mouseY, cardX, cardY, cardWidth, cardHeight);
            hovered |= cardHovered;

            float alpha = current ? 0.85f : (cardHovered ? 0.7f : 0.55f);

            DisplayUtils.drawRoundedRect(cardX, cardY, cardWidth, cardHeight, new Vector4f(10, 10, 10, 10),
                    ColorUtils.rgba(20, 22, 34, (int) (255 * alpha)));

            DisplayUtils.drawRoundedRect(cardX + 1, cardY + 1, cardWidth - 2, cardHeight - 2, new Vector4f(8, 8, 8, 8),
                    ColorUtils.rgba(10, 11, 18, (int) (255 * alpha)));

            if (current) {
                DisplayUtils.drawRoundedRect(cardX - 1.5f, cardY - 1.5f, cardWidth + 3, cardHeight + 3,
                        new Vector4f(11, 11, 11, 11), ColorUtils.rgba(93, 122, 255, 55));
            }

            Fonts.montserrat.drawText(stack, style.getStyleName(), cardX + 12, cardY + 8, ColorUtils.rgb(202, 206, 223), 7f,
                    0.05f);

            Fonts.montserrat.drawText(stack, "Preset blend", cardX + 12, cardY + cardHeight - 10,
                    ColorUtils.rgb(108, 116, 142), 6f, 0.05f);

            float previewWidth = 60f;
            float previewHeight = cardHeight - 12;
            float previewX = cardX + cardWidth - previewWidth - 10;
            float previewY = cardY + (cardHeight - previewHeight) / 2f;

            DisplayUtils.drawRoundedRect(previewX, previewY, previewWidth, previewHeight, new Vector4f(6, 6, 6, 6),
                    new Vector4i(style.getFirstColor().getRGB(), style.getFirstColor().getRGB(),
                            style.getSecondColor().getRGB(), style.getSecondColor().getRGB()));

            offset++;
        }

        if (MathUtil.isHovered(mouseX, mouseY, x, y, width, height)) {
            if (hovered) {
                GLFW.glfwSetCursor(Minecraft.getInstance().getMainWindow().getHandle(), Cursors.HAND);
            } else {
                GLFW.glfwSetCursor(Minecraft.getInstance().getMainWindow().getHandle(), Cursors.ARROW);
            }
        }
        Scissor.unset();
        Scissor.pop();
        max = offset * cardHeight + Math.max(0, offset - 1) * cardSpacing;

        DisplayUtils.drawRoundedRect(x, y + height - footer, width, footer, new Vector4f(0, 0, 12, 12),
                new Vector4i(ColorUtils.rgba(27, 27, 38, 0), ColorUtils.rgba(27, 27, 38, 0),
                        ColorUtils.rgba(36, 36, 48, (int) (255 * 0.45)),
                        ColorUtils.rgba(36, 36, 48, (int) (255 * 0.7))));

        Fonts.montserrat.drawText(stack,
                "Current: " + Expensive.getInstance().getStyleManager().getCurrentStyle().getStyleName(), x + 12,
                y + height - footer / 2f - Fonts.montserrat.getHeight(6f) / 2f, ColorUtils.rgb(110, 115, 140), 6f, 0.05f);
    }

    @Override
    public void keyPressed(int key, int scanCode, int modifiers) {

    }

    @Override
    public void mouseClick(float mouseX, float mouseY, int button) {
        float header = 44f;
        float cardHeight = 34f;
        float cardSpacing = 10f;
        int offset = 0;
        for (Style style : Expensive.getInstance().getStyleManager().getStyleList()) {
            float cardX = this.x + 8;
            float cardY = this.y + header + 8 + offset * (cardHeight + cardSpacing) + getAnimatedScrool();
            float cardWidth = width - 16;
            if (MathUtil.isHovered(mouseX, mouseY, cardX, cardY, cardWidth, cardHeight)) {
                Expensive.getInstance().getStyleManager().setCurrentStyle(style);
            }
            offset++;
        }

    }

    @Override
    public void mouseRelease(float mouseX, float mouseY, int button) {

    }

}

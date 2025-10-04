package im.expensive.ui.dropdown;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import im.expensive.Expensive;
import im.expensive.functions.api.Category;
import im.expensive.functions.api.Function;
import im.expensive.ui.dropdown.components.ModuleComponent;
import im.expensive.ui.dropdown.impl.Component;
import im.expensive.ui.dropdown.impl.IBuilder;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.math.Vector4i;
import im.expensive.utils.render.ColorUtils;
import im.expensive.utils.render.DisplayUtils;
import im.expensive.utils.render.Scissor;
import im.expensive.utils.render.Stencil;
import im.expensive.utils.render.font.Fonts;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector4f;

@Getter
@Setter
public class Panel implements IBuilder {

    private final Category category;
    protected float x;
    protected float y;
    protected final float width = 165;
    protected final float height = 747 / 2f;

    private List<ModuleComponent> modules = new ArrayList<>();
    private float scroll, animatedScrool;


    public Panel(Category category) {
        this.category = category;

        for (Function function : Expensive.getInstance().getFunctionRegistry().getFunctions()) {
            if (function.getCategory() == category) {
                ModuleComponent component = new ModuleComponent(function);
                component.setPanel(this);
                modules.add(component);
            }
        }

    }

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {

        animatedScrool = MathUtil.fast(animatedScrool, scroll, 10);
        float header = 44f;
        float footer = 30f;
        float headerFont = 8.5f;

        DisplayUtils.drawRoundedRect(x, y, width, height, new Vector4f(12, 12, 12, 12),
                ColorUtils.rgba(11, 11, 15, (int) (255 * 0.75)));

        DisplayUtils.drawRoundedRect(x + 1, y + 1, width - 2, height - 2, new Vector4f(10, 10, 10, 10),
                ColorUtils.rgba(6, 6, 9, (int) (255 * 0.8)));

        DisplayUtils.drawRoundedRect(x, y, width, header, new Vector4f(12, 12, 8, 8),
                ColorUtils.rgba(24, 24, 32, (int) (255 * 0.92)));

        DisplayUtils.drawRectVerticalW(x, y, width, header,
                ColorUtils.rgba(86, 121, 255, 65), ColorUtils.rgba(171, 74, 255, 35));

        DisplayUtils.drawRoundedRect(x + 6, y + header - 1.5f, width - 12, 1.5f, new Vector4f(1, 1, 1, 1),
                ColorUtils.rgba(255, 255, 255, 35));

        Fonts.montserrat.drawText(stack, category.name(), x + 12,
                y + header / 2f - Fonts.montserrat.getHeight(headerFont) / 2f, ColorUtils.rgb(194, 198, 214),
                headerFont, 0.1f);

        Fonts.montserrat.drawText(stack, modules.size() + " modules", x + 12,
                y + header / 2f + Fonts.montserrat.getHeight(6.5f) / 2f + 2, ColorUtils.rgb(97, 103, 130), 6.5f, 0.1f);

        drawComponents(stack, mouseX, mouseY);

        drawOutline();

        DisplayUtils.drawRoundedRect(x, y + height - footer, width, footer, new Vector4f(0, 0, 12, 12),
                new Vector4i(ColorUtils.rgba(27, 27, 38, 0), ColorUtils.rgba(27, 27, 38, 0),
                        ColorUtils.rgba(36, 36, 48, (int) (255 * 0.45)),
                        ColorUtils.rgba(36, 36, 48, (int) (255 * 0.7))));

        Fonts.montserrat.drawText(stack, "Hold shift to expand", x + 12,
                y + height - footer / 2f - Fonts.montserrat.getHeight(6f) / 2f, ColorUtils.rgb(110, 115, 140), 6f,
                0.05f);

    }

    protected void drawOutline() {
        Stencil.initStencilToWrite();

        DisplayUtils.drawRoundedRect(x + 0.5f, y + 0.5f, width - 1, height - 1, new Vector4f(11F, 11F, 11F, 11F),
                ColorUtils.rgba(32, 32, 43, (int) (255 * 0.4)));

        Stencil.readStencilBuffer(0);

        DisplayUtils.drawRoundedRect(x, y, width, height,
                new Vector4f(11f, 11f, 11f, 11f),
                new Vector4i(ColorUtils.rgb(40, 44, 54), ColorUtils.rgb(12, 12, 18), ColorUtils.rgb(40, 44, 54),
                        ColorUtils.rgb(12, 12, 18)));

        Stencil.uninitStencilBuffer();
    }

    float max = 0;

    private void drawComponents(MatrixStack stack, float mouseX, float mouseY) {
        float animationValue = (float) DropDown.getAnimation().getValue() * DropDown.scale;

        float halfAnimationValueRest = (1 - animationValue) / 2f;
        float header = 44f;
        float footer = 30f;
        float contentHeight = getHeight() - header - footer;
        float testX = getX() + (getWidth() * halfAnimationValueRest);
        float testY = getY() + header + (contentHeight * halfAnimationValueRest);
        float testW = getWidth() * animationValue;
        float testH = contentHeight * animationValue;

        testX = testX * animationValue + ((Minecraft.getInstance().getMainWindow().getScaledWidth() - testW) *
                halfAnimationValueRest);

        Scissor.push();
        Scissor.setFromComponentCoordinates(testX, testY, testW, testH);
        float offset = 0;
        float availableHeight = contentHeight - 10;

        if (max > availableHeight) {
            scroll = MathHelper.clamp(scroll, -max + availableHeight, 0);
            animatedScrool = MathHelper.clamp(animatedScrool, -max + availableHeight, 0);
        } else {
            scroll = 0;
            animatedScrool = 0;
        }
        for (ModuleComponent component : modules) {
            component.setX(getX() + 8);
            component.setY(getY() + header + 8 + offset + animatedScrool);
            component.setWidth(getWidth() - 16);
            component.setHeight(20);
            component.animation.update();
            if (component.animation.getValue() > 0) {
                float componentOffset = 0;
                for (Component component2 : component.getComponents()) {
                    if (component2.isVisible())
                        componentOffset += component2.getHeight();
                }
                componentOffset *= component.animation.getValue();
                component.setHeight(component.getHeight() + componentOffset);
            }
            component.render(stack, mouseX, mouseY);
            offset += component.getHeight() + 3.5f;
        }
        max = offset;

        Scissor.unset();
        Scissor.pop();

    }

    @Override
    public void mouseClick(float mouseX, float mouseY, int button) {
        for (ModuleComponent component : modules) {
            component.mouseClick(mouseX, mouseY, button);
        }
    }

    @Override
    public void keyPressed(int key, int scanCode, int modifiers) {
        for (ModuleComponent component : modules) {
            component.keyPressed(key, scanCode, modifiers);
        }
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
        for (ModuleComponent component : modules) {
            component.charTyped(codePoint, modifiers);
        }
    }

    @Override
    public void mouseRelease(float mouseX, float mouseY, int button) {
        for (ModuleComponent component : modules) {
            component.mouseRelease(mouseX, mouseY, button);
        }
    }

}

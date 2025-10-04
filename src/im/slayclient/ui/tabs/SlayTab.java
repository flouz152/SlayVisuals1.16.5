package im.slayclient.ui.tabs;

import com.mojang.blaze3d.matrix.MatrixStack;

public interface SlayTab {

    String title();

    default void onOpen() {
    }

    default void tick() {
    }

    void render(MatrixStack stack, float x, float y, float width, float height, int mouseX, int mouseY, float partialTicks);

    default void mouseClicked(double mouseX, double mouseY, int button) {
    }
}

package com.moonkitty.Gui;

import com.moonkitty.Category;
import com.moonkitty.Feature;
import com.moonkitty.FeatureManager;
import com.moonkitty.config.ConfigManager;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class ClickGui extends Screen {
    private final List<ModuleWindow> windows = new ArrayList<>();
    private ModuleWindow draggingWindow = null;
    private ModuleWindow sliderWindow = null;
    private int dragOffsetX, dragOffsetY;

    public static final int BG_COLOR = 0xA0000000;
    public static final int WINDOW_BG_COLOR = 0xE0101010;
    public static final int HEADER_COLOR = 0xFF6B3FA0;
    public static final int MODULE_BG_COLOR = 0xFF1A1A1A;
    public static final int MODULE_ENABLED_COLOR = 0xFF8B4FC3;
    public static final int TEXT_COLOR = 0xFFFFFFFF;
    public static final int TEXT_DISABLED_COLOR = 0xFFAAAAAA;

    public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");

    public ClickGui() {
        super(Text.literal("Click GUI"));
        initWindows();
    }

    private void initWindows() {
        int spacing = 10;
        int startY = 10;
        int screenWidth = this.width > 0 ? this.width : 400;
        int currentX = 10;
        int currentY = startY;
        for (Category category : Category.values()) {
            List<Feature> categoryModules = getModulesByCategory(category);
            if (!categoryModules.isEmpty()) {
                if (currentX + ModuleWindow.WIDTH > screenWidth - 10) {
                    currentX = 10;
                    currentY += ModuleWindow.HEADER_HEIGHT + 10 + 100;
                }
                ModuleWindow window = new ModuleWindow(category, currentX, currentY, categoryModules);
                windows.add(window);
                currentX += ModuleWindow.WIDTH + spacing;
            }
        }
    }

    private List<Feature> getModulesByCategory(Category category) {
        List<Feature> modules = new ArrayList<>();
        for (Feature feature : FeatureManager.INSTANCE.featureList) {
            if (feature.getCategory() == category) {
                modules.add(feature);
            }
        }
        return modules;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        for (ModuleWindow window : windows) {
            window.render(context, textRenderer, mouseX, mouseY, delta);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, BG_COLOR);
    }

    @Override
    public boolean mouseClicked(Click click, boolean dragScrolling) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();

        if (button == 0) {
            for (int i = windows.size() - 1; i >= 0; i--) {
                ModuleWindow window = windows.get(i);
                if (window.isHeaderHovered((int) mouseX, (int) mouseY)) {
                    draggingWindow = window;
                    dragOffsetX = (int) mouseX - window.x;
                    dragOffsetY = (int) mouseY - window.y;

                    windows.remove(i);
                    windows.add(window);
                    return true;
                }
            }
        }

        for (ModuleWindow window : windows) {
            if (window.handleClick((int) mouseX, (int) mouseY, button)) {
                if (button == 0) {
                    sliderWindow = window;
                }
                return true;
            }
        }

        return super.mouseClicked(click, dragScrolling);
    }

    @Override
    public boolean mouseReleased(Click click) {
        int button = click.button();
        if (button == 0) {
            draggingWindow = null;
            if (sliderWindow != null) {
                sliderWindow.releaseDrag();
                sliderWindow = null;
            }
        }
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();

        if (button == 0) {
            if (draggingWindow != null) {
                draggingWindow.x = (int) mouseX - dragOffsetX;
                draggingWindow.y = (int) mouseY - dragOffsetY;
                return true;
            }

            if (sliderWindow != null) {
                sliderWindow.handleDrag((int) mouseX, (int) mouseY);
                return true;
            }
        }

        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        LOGGER.info("Menu Closed Saving config");
        ConfigManager.saveConfigSettings();
        super.close();
    }
}

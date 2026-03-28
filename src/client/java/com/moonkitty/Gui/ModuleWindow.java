package com.moonkitty.Gui;

import net.minecraft.client.gui.DrawContext;
import java.util.ArrayList;
import java.util.List;

import com.moonkitty.BooleanSetting;
import com.moonkitty.ButtonSetting;
import com.moonkitty.Category;
import com.moonkitty.Feature;
import com.moonkitty.FeatureManager;
import com.moonkitty.NumberSetting;
import com.moonkitty.Setting;
import com.moonkitty.Features.esp;
import com.moonkitty.Features.fakeplayer;
import com.moonkitty.Features.companion;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.client.gui.widget.ScrollableWidget;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.font.TextRenderer;

public class ModuleWindow {

    public static final int WIDTH = 150;
    public static final int HEADER_HEIGHT = 16;
    public static final int ENTRY_HEIGHT = 18;
    public static final int SETTING_HEIGHT = 16;

    private final Category category;
    private final List<Feature> modules;
    public int x, y;
    private boolean expanded = true;
    private Feature expandedModule = null;
    private Setting<?> draggingSetting = null;

    public ModuleWindow(Category category, int x, int y, List<Feature> modules) {
        this.category = category;
        this.x = x;
        this.y = y;
        this.modules = modules;
    }

    public void render(DrawContext ctx, TextRenderer tr, int mx, int my, float delta) {
        int currentY = y;

        // header
        ctx.fill(x, currentY, x + WIDTH, currentY + HEADER_HEIGHT, ClickGui.HEADER_COLOR);
        ctx.drawText(tr, category.getName(), x + 4, currentY + 4, ClickGui.TEXT_COLOR, true);

        currentY += HEADER_HEIGHT;

        if (expanded) {
            // draw modules
            for (Feature module : modules) {
                int bgColor = module.isEnabled() ? ClickGui.MODULE_ENABLED_COLOR : ClickGui.MODULE_BG_COLOR;
                ctx.fill(x, currentY, x + WIDTH, currentY + ENTRY_HEIGHT, bgColor);

                ctx.fill(x, currentY, x + WIDTH, currentY + 1, 0xFF000000); // top border

                int textColor = module.isEnabled() ? ClickGui.TEXT_COLOR : ClickGui.TEXT_DISABLED_COLOR;
                String moduleName = module.name != null ? module.name : module.getClass().getSimpleName();
                ctx.drawText(tr, moduleName, x + 4, currentY + 5, textColor, false);

                // arrow for settings
                if (!module.getSettings().isEmpty()) {
                    String arrow = expandedModule == module ? "v" : ">";
                    ctx.drawText(tr, arrow, x + WIDTH - 10, currentY + 5, textColor, false);
                }

                currentY += ENTRY_HEIGHT;

                // draw settings if expanded
                if (expandedModule == module && !module.getSettings().isEmpty()) {
                    for (Setting<?> setting : module.getSettings()) {
                        ctx.fill(x + 4, currentY, x + WIDTH, currentY + SETTING_HEIGHT,
                                ClickGui.MODULE_BG_COLOR - 0x00101010);

                        int settingTextColor = 0xFFCCCCCC;

                        if (setting instanceof BooleanSetting) {
                            // Render boolean setting as text
                            String settingText = setting.getName() + ": " + setting.getDisplayValue();
                            ctx.drawText(tr, settingText, x + 8, currentY + 4, settingTextColor, false);
                        } else if (setting instanceof NumberSetting) {

                            NumberSetting numSetting = (NumberSetting) setting;

                            // setting name
                            ctx.drawText(tr, setting.getName(), x + 8, currentY + 2, settingTextColor, false);

                            int sliderX = x + 8;
                            int sliderY = currentY + 11;
                            int sliderWidth = WIDTH - 16;
                            int sliderHeight = 3;

                            ctx.fill(sliderX, sliderY, sliderX + sliderWidth, sliderY + sliderHeight, 0xFF404040);

                            double percentage = (numSetting.getValue() - numSetting.getMin())
                                    / (numSetting.getMax() - numSetting.getMin());
                            int filledWidth = (int) (sliderWidth * percentage);

                            ctx.fill(sliderX, sliderY, sliderX + filledWidth, sliderY + sliderHeight,
                                    ClickGui.MODULE_ENABLED_COLOR);

                            String valueText = setting.getDisplayValue();
                            int valueWidth = tr.getWidth(valueText);
                            ctx.drawText(tr, valueText, x + WIDTH - valueWidth - 8, currentY + 2, settingTextColor,
                                    false);
                        } else if (setting instanceof ButtonSetting) {
                            // Render button setting as a clickable button
                            int buttonX = x + 8;
                            int buttonY = currentY + 2;
                            int buttonWidth = WIDTH - 16;
                            int buttonHeight = SETTING_HEIGHT - 4;

                            // Draw button background
                            boolean isHovered = mx >= buttonX && mx <= buttonX + buttonWidth &&
                                    my >= buttonY && my <= buttonY + buttonHeight;
                            int buttonColor = isHovered ? ClickGui.MODULE_ENABLED_COLOR : 0xFF505050;
                            ctx.fill(buttonX, buttonY, buttonX + buttonWidth, buttonY + buttonHeight, buttonColor);

                            // Draw button border
                            ctx.fill(buttonX, buttonY, buttonX + buttonWidth, buttonY + 1, 0xFF000000); // Top
                            ctx.fill(buttonX, buttonY + buttonHeight - 1, buttonX + buttonWidth, buttonY + buttonHeight,
                                    0xFF000000); // Bottom
                            ctx.fill(buttonX, buttonY, buttonX + 1, buttonY + buttonHeight, 0xFF000000); // Left
                            ctx.fill(buttonX + buttonWidth - 1, buttonY, buttonX + buttonWidth, buttonY + buttonHeight,
                                    0xFF000000); // Right

                            // Center button text
                            String buttonText = setting.getName();
                            int textWidth = tr.getWidth(buttonText);
                            int textX = buttonX + (buttonWidth - textWidth) / 2;
                            int textY = buttonY + (buttonHeight - 8) / 2;
                            ctx.drawText(tr, buttonText, textX, textY, ClickGui.TEXT_COLOR, false);
                        } else {
                            String settingText = setting.getName() + ": " + setting.getDisplayValue();
                            ctx.drawText(tr, settingText, x + 8, currentY + 4, settingTextColor, false);
                        }

                        currentY += SETTING_HEIGHT;
                    }
                }
            }
        }
    }

    public boolean isHeaderHovered(int mx, int my) {
        return mx >= x && mx <= x + WIDTH && my >= y && my <= y + HEADER_HEIGHT;
    }

    public boolean handleClick(int mx, int my, int button) {
        if (mx < x || mx > x + WIDTH) {
            return false;
        }

        if (my >= y && my <= y + HEADER_HEIGHT) {
            expanded = !expanded;
            return true;
        }

        if (!expanded) {
            return false;
        }

        int currentY = y + HEADER_HEIGHT;
        for (Feature module : modules) {
            if (my >= currentY && my <= currentY + ENTRY_HEIGHT) {
                if (button == 0) {
                    module.toggle();
                } else if (button == 1) {
                    if (!module.getSettings().isEmpty()) {
                        expandedModule = expandedModule == module ? null : module;
                    }
                }
                return true;
            }
            currentY += ENTRY_HEIGHT;

            if (expandedModule == module && !module.getSettings().isEmpty()) {
                for (Setting<?> setting : module.getSettings()) {
                    if (my >= currentY && my <= currentY + SETTING_HEIGHT) {
                        if (setting instanceof NumberSetting && button == 0) {
                            draggingSetting = setting;
                            updateSliderValue((NumberSetting) setting, mx);
                            return true;
                        } else if (setting instanceof ButtonSetting && button == 0) {
                            ((ButtonSetting) setting).execute();
                            return true;
                        } else {
                            handleSettingClick(setting, button);
                            return true;
                        }
                    }
                    currentY += SETTING_HEIGHT;
                }
            }
        }

        return false;
    }

    private void updateSliderValue(NumberSetting setting, int mouseX) {
        int sliderX = x + 8;
        int sliderWidth = WIDTH - 16;

        double percentage = (mouseX - sliderX) / (double) sliderWidth;
        setting.setValueFromPercentage(percentage);
    }

    public boolean handleDrag(int mx, int my) {
        if (draggingSetting instanceof NumberSetting) {
            updateSliderValue((NumberSetting) draggingSetting, mx);
            return true;
        }
        return false;
    }

    public void releaseDrag() {
        draggingSetting = null;
    }

    private void handleSettingClick(Setting<?> setting, int button) {
        if (setting instanceof BooleanSetting) {
            if (button == 0) {
                ((BooleanSetting) setting).toggle();
            }
        } else if (setting instanceof NumberSetting) {
            NumberSetting numSetting = (NumberSetting) setting;
            if (button == 0) {
                numSetting.decrement();
            } else if (button == 1) {
                numSetting.increment();
            }
        } else if (setting instanceof ButtonSetting) {
            if (button == 0) {
                ((ButtonSetting) setting).execute();
            }
        }
    }

    public int getHeight() {
        if (!expanded) {
            return HEADER_HEIGHT;
        }
        int height = HEADER_HEIGHT;
        for (Feature module : modules) {
            height += ENTRY_HEIGHT;
            if (expandedModule == module && !module.getSettings().isEmpty()) {
                height += module.getSettings().size() * SETTING_HEIGHT;
            }
        }
        return height;
    }
}

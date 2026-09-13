package de.tobi.voxelconfig;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

/**
 * A generic, reusable settings screen with category tabs on the left
 * and a scrollable options list on the right.
 *
 * <p>Usage:
 * <pre>{@code
 * ConfigScreen screen = ConfigScreen.create(
 *     Component.translatable("mymod.settings.title"),
 *     myConfigProvider.categories(),
 *     parentScreen
 * );
 * minecraft.setScreen(screen);
 * }</pre>
 */
public class ConfigScreen extends Screen {
    private static final int HEADER_HEIGHT = 32;
    private static final int FOOTER_HEIGHT = 32;
    private static final int CATEGORY_GAP = 4;

    private final Component heading;
    private final List<SettingsCategory> categories;
    private final List<Button> categoryButtons = new ArrayList<>();
    private final Screen parent;
    private final Runnable onSave;
    private int selectedCategory;
    private int contentX, contentY, contentWidth, contentHeight, categoryWidth;
    private SettingsListWidget optionList;

    /**
     * Creates a new config screen.
     *
     * @param heading    the screen title
     * @param categories the settings categories to display
     * @param parent     the parent screen to return to
     * @param onSave     callback invoked when the screen is closed to save settings
     */
    protected ConfigScreen(Component heading, List<SettingsCategory> categories, Screen parent, Runnable onSave) {
        super(heading);
        this.heading = heading;
        this.categories = List.copyOf(categories);
        this.parent = parent;
        this.onSave = onSave;
    }

    /**
     * Creates a config screen.
     */
    public static ConfigScreen create(Component title, List<SettingsCategory> categories, Screen parent) {
        return create(title, categories, parent, () -> {});
    }

    /**
     * Creates a config screen with a save callback.
     */
    public static ConfigScreen create(Component title, List<SettingsCategory> categories, Screen parent, Runnable onSave) {
        return new ConfigScreen(title, categories, parent, onSave);
    }

    /**
     * Creates a config screen from a {@link ConfigProvider}.
     */
    public static ConfigScreen create(Component title, ConfigProvider provider, Screen parent, Runnable onSave) {
        return new ConfigScreen(title, provider.categories(), parent, onSave);
    }

    @Override
    protected void init() {
        clearWidgets();
        categoryButtons.clear();

        int maximumWidth = 760;
        contentWidth = Math.min(width - 16, maximumWidth);
        contentHeight = Math.max(80, height - HEADER_HEIGHT - FOOTER_HEIGHT);
        contentX = (width - contentWidth) / 2;
        contentY = HEADER_HEIGHT;
        categoryWidth = Math.clamp(contentWidth / 5, 92, 132);

        for (int i = 0; i < categories.size(); i++) {
            int index = i;
            Button button = Button.builder(categories.get(i).title(), ignored -> selectCategory(index))
                    .bounds(contentX, contentY + i * 24, categoryWidth, 20).build();
            categoryButtons.add(addRenderableWidget(button));
        }

        addRenderableWidget(Button.builder(Component.translatable("gui.done"), ignored -> onClose())
                .bounds(width / 2 - 100, height - 27, 200, 20).build());

        rebuildContent();
    }

    private void selectCategory(int index) {
        if (index == selectedCategory) return;
        selectedCategory = index;
        rebuildContent();
    }

    /** Rebuilds the options list for the currently selected category. */
    public void rebuildContent() {
        if (optionList != null) {
            optionList.commitPendingText();
            removeWidget(optionList);
        }
        int listX = contentX + categoryWidth + CATEGORY_GAP;
        int listWidth = contentWidth - categoryWidth - CATEGORY_GAP;
        optionList = new SettingsListWidget(this, listX, contentY, listWidth, contentHeight, categories.get(selectedCategory));
        addRenderableWidget(optionList);
        updateCategoryButtons();
    }

    /** Cycles to the next choice value for a choice option. Called by SettingsListWidget. */
    public void cycleChoice(SettingsOption<?> option) {
        if (option.choices().isEmpty()) return;
        int currentIndex = 0;
        for (int i = 0; i < option.choices().size(); i++) {
            if (option.choices().get(i).value().equals(option.value())) {
                currentIndex = i;
                break;
            }
        }
        setChoiceUnchecked(option, (currentIndex + 1) % option.choices().size());
    }

    @SuppressWarnings("unchecked")
    private static <T> void setChoiceUnchecked(SettingsOption<T> option, int index) {
        option.set(option.choices().get(index).value());
    }

    private void updateCategoryButtons() {
        for (int i = 0; i < categoryButtons.size(); i++) {
            Button button = categoryButtons.get(i);
            boolean selected = i == selectedCategory;
            button.active = !selected;
            button.setMessage(selected
                    ? Component.literal("◆ ").withStyle(ChatFormatting.AQUA).append(categories.get(i).title())
                    : categories.get(i).title());
        }
    }

    @Override
    public Font getFont() {
        return super.getFont();
    }

    @Override
    public void onClose() {
        if (optionList != null) optionList.commitPendingText();
        this.minecraft.gui.setScreen(parent);
    }

    @Override
    public void removed() {
        if (optionList != null) optionList.commitPendingText();
        onSave.run();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        graphics.centeredText(getFont(), heading, width / 2, 12, 0xFFFFFFFF);
    }

    @Override
    public void extractMenuBackground(GuiGraphicsExtractor graphics) {
        super.extractMenuBackground(graphics);
        graphics.fill(contentX - 4, contentY - 4, contentX + contentWidth + 4, contentY + contentHeight + 4, 0x66000000);
        graphics.fill(contentX + categoryWidth + 1, contentY, contentX + categoryWidth + 2, contentY + contentHeight, 0x88707070);
    }
}
